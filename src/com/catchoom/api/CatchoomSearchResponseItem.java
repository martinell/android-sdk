// © Catchoom Technologies S.L.
// Licensed under the MIT license.
// https://raw.github.com/catchoom/android-sdk/master/LICENSE
// All warranties and liabilities are disclaimed.
package com.catchoom.api;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

/**
 * CatchoomSearchResponseItem represents the response from the Catchoom Recognition
 * Server for one coincidence within a collection.
 * @author Catchoom
 *
 */
public class CatchoomSearchResponseItem {

	private String itemId = "";
	private int score = -1;
	private Bundle metadata = null;
	
	CatchoomSearchResponseItem(String item_id, int score, Bundle metadata) {
		this.itemId = item_id;
		this.score = score;
		this.metadata = metadata;
	}
	
	/**
	 * Gets the Item's ID.
	 * @return The item's ID.
	 */
	public String getId() {
		return itemId;
	}
	
	/**
	 * Gets the item's score.
	 * @return The item's score.
	 */
	public int getScore() {
		return score;
	}
	
	/**
	 * Gets the item's metadata as a {@link Bundle}.
	 * @return The item's metadata.
	 */
	public Bundle getMetadata() {
		return metadata;
	}
	
	/**
	 * Parse a {@link CatchoomSearchResponseItem} from a {@link JSONObject}.  
	 * @param json The json to parse.
	 * @return The {@link CatchoomSearchResponseItem} parsed.
	 */
	static CatchoomSearchResponseItem parseFromJSON(JSONObject json) {
		
		try {
			
			if (json.has("item_id") && json.has("score")) {
				String parsedItemId = json.getString("item_id");
				int parsedScore = json.getInt("score");
				
				JSONObject rawMetadata = json.getJSONObject("metadata");
				Bundle parsedMetadata = new Bundle();
				
				if (null != rawMetadata) {
					Iterator<?> metadataKeys = rawMetadata.keys();
					
					while (metadataKeys.hasNext()) {
						String key = (String) metadataKeys.next();
						parsedMetadata.putString(key, rawMetadata.getString(key));
					}
				}
			
				return new CatchoomSearchResponseItem(parsedItemId, parsedScore, parsedMetadata);
			} else {
				return null;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}