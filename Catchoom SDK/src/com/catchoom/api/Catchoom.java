// © Catchoom Technologies S.L.
// Licensed under the MIT license.
// https://raw.github.com/catchoom/android-sdk/master/LICENSE
// All warranties and liabilities are disclaimed.
package com.catchoom.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Catchoom allows you to send requests to the Catchoom Recognition Server.
 * For further information, please visit {@link http://catchoom.com}.
 * @author Catchoom
 *
 */
public class Catchoom {
	
	static final String TAG = "Catchoom SDK";
	
	public static final class Request {
		public static final int CONNECT_REQUEST = 0;
		public static final int SEARCH_REQUEST = 1;
	}
	
	static final class Config {
		public static final String BASE_URL = "https://r.catchoom.com/v0/";
	}
	
	private DefaultHttpClient mHttpClient = null;
	private CatchoomResponseHandler mCatchoomResponseHandler = null;
	
	public Catchoom() {
		HttpParams httpParams = new BasicHttpParams();
        httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		mHttpClient = new DefaultHttpClient(httpParams);
	}
	
	/**
	 * Sets the handler that will receive the callbacks from the server when
	 * performing operations.
	 * @param responseHandler The response handler.
	 */
	public void setResponseHandler(CatchoomResponseHandler responseHandler) {
		this.mCatchoomResponseHandler = responseHandler;
	}
	
	/**
	 * Performs a connection against the Catchoom server. This request is
	 * asynchronous and will trigger a callback to a {@link CatchoomResponseHandler}
	 * once it completes. If it succeeds, the server will respond with its timestamp.
	 * If the request fails, a {@link CatchoomErrorResponseItem} will be sent.
	 * @param token The token to access the collection.
	 */
	public void connect(String token) {
		if (null != mCatchoomResponseHandler) {
			Connect connectImpl = new Connect();
			connectImpl.execute(token);
		}
	}
	
	/**
	 * Searches the image specified in a collection. This request is
	 * asynchronous and will trigger a callback to a {@link CatchoomResponseHandler}
	 * once it completes. If it succeeds, the server will respond with an {@link ArrayList<CatchoomSearchResponseItem>}.
	 * This list can either contain several {@link CatchoomSearchResponseItem} or be
	 * empty depending on the number of coincidences found.
	 * If the request fails, a {@link CatchoomErrorResponseItem} will be sent.
	 * @param token
	 * @param image
	 */
	public void search(String token, File image) {
		if (null != mCatchoomResponseHandler) {
			Search searchImpl = new Search();
			File processedImage = CatchoomImageUtil.processPicture(image.getPath());
			searchImpl.execute(token, processedImage);
		}
	}
	
	
	// Private inner implementation of the API
	
	/**
	 * Inner class to perform asynchronous connection easily in Android.
	 * @author Catchoom
	 *
	 */
	private class Connect extends AsyncTask<String, Void, Object> {

		private static final String URL = Config.BASE_URL + "timestamp";
		private static final String REQUEST_TOKEN_PARAM = "token";
		
		@Override
		protected Object doInBackground(String... params) {
			
			if (null != params && params.length > 0) {
				
				String token = params[0];
				
				// Create the request and execute it
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		        nameValuePairs.add(new BasicNameValuePair(REQUEST_TOKEN_PARAM, token));
				
				HttpPost request = new HttpPost(URL);
				try {
					request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
					HttpResponse response = mHttpClient.execute(request);
					
					if (null != response) {
						StatusLine status = response.getStatusLine();
						String stringResponse = EntityUtils.toString(response.getEntity());
						JSONObject json = new JSONObject(stringResponse);

						if (200 == status.getStatusCode()) {
							long timestamp = json.getLong("timestamp");
							return Long.valueOf(timestamp);
						} else {
							String error = json.getString("message");
							return new CatchoomErrorResponseItem(status.getStatusCode(), status.getReasonPhrase(), error);
						}
					}
				} catch (UnsupportedEncodingException e1) {
					e1.printStackTrace();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.e(TAG, "Error sending the parameters to the request");
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object response) {
			super.onPostExecute(response);
			
			if (null == response) {
				mCatchoomResponseHandler.requestFailedResponse(null);
			} else if (response instanceof CatchoomErrorResponseItem) {
				mCatchoomResponseHandler.requestFailedResponse((CatchoomErrorResponseItem) response);
			} else {
				long timestamp = (Long) response;
				mCatchoomResponseHandler.requestCompletedResponse(Catchoom.Request.CONNECT_REQUEST, timestamp);
			}
		}
	}
	
	/**
	 * Inner class to perform asynchronous search easily in Android.
	 * @author Catchoom
	 *
	 */
	private class Search extends AsyncTask<Object, Void, Object> {

		private static final String URL = Config.BASE_URL + "search";
		private static final String REQUEST_TOKEN_PARAM = "token";
		private static final String REQUEST_IMAGE_PARAM = "image";
		
		@Override
		protected Object doInBackground(Object... params) {
			
			// Upload the photo and wait for the response
			if (null != params && params.length > 1) {
				// Get params
				String collectionToken = (String) params[0];
				File picture = (File) params[1];
				
	            MultipartEntity multipartEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);  
	            
	            try {
		            multipartEntity.addPart(REQUEST_TOKEN_PARAM, new StringBody(collectionToken));
		            multipartEntity.addPart(REQUEST_IMAGE_PARAM, new FileBody(picture));
	            } catch (UnsupportedEncodingException e) {
	            	e.printStackTrace();
	            }
		        
	            HttpPost request = new HttpPost(URL);
	            request.setEntity(multipartEntity);
				
	            try {
	            	HttpResponse response = mHttpClient.execute(request);
	            	
	    			if (null != response) {
						StatusLine status = response.getStatusLine();
						String stringResponse = EntityUtils.toString(response.getEntity());
						
						if (200 == status.getStatusCode()) {
							JSONArray results = new JSONArray(stringResponse);
							ArrayList<CatchoomSearchResponseItem> items = new ArrayList<CatchoomSearchResponseItem>();
							
							for (int i = 0; i < results.length(); i++) {
								try {
									CatchoomSearchResponseItem parsedItem = CatchoomSearchResponseItem.parseFromJSON(results.getJSONObject(i));
									if (null != parsedItem) {
										items.add(parsedItem);
									}
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}
							
							return items;
						} else {
							JSONObject json = new JSONObject(stringResponse);
							String error = json.getString("message");
							return new CatchoomErrorResponseItem(status.getStatusCode(), status.getReasonPhrase(), error);
						}
					}
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				// Error
				Log.e(TAG, "Error sending the parameters to the request");
			}
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Object response) {
			super.onPostExecute(response);
			
			if (null == response) {
				mCatchoomResponseHandler.requestFailedResponse(null);
			} else if (response instanceof CatchoomErrorResponseItem) {
				mCatchoomResponseHandler.requestFailedResponse((CatchoomErrorResponseItem) response);
			} else {
				ArrayList<CatchoomSearchResponseItem> items = (ArrayList<CatchoomSearchResponseItem>) response;
				mCatchoomResponseHandler.requestCompletedResponse(Catchoom.Request.SEARCH_REQUEST, items);
			}
		}
	}
}
