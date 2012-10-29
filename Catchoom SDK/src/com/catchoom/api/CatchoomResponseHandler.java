// © Catchoom Technologies S.L.
// Licensed under the MIT license.
// https://raw.github.com/catchoom/android-sdk/master/LICENSE
// All warranties and liabilities are disclaimed.
package com.catchoom.api;

/**
 * CatchoomResponseHanlder expose the interface used by the Catchoom requests to
 * perform callbacks once they have finished. Any class implementing this interface
 * must override its methods.
 * 
 * <code>Remember to set the handler to your {@link Catchoom} object through setResponseHandler</code>.
 * @author Catchoom
 *
 */
public interface CatchoomResponseHandler {

	/**
	 * Callback triggered once a request has completed succesfully.
	 * @param requestCode The code corresponding to the request type has been sent.
	 * Can be checked using {@link Catchoom.Request}.
	 * @param responseData The data returned by the request. Its type and content
	 * can vary depending on the request performed.
	 */
	public void requestCompletedResponse(int requestCode, Object responseData);
	
	/**
	 * Callback triggered once a request has completed unsuccessfully.
	 * Connection unavailable or server not reached are also considered as
	 * unsuccessful requests.
	 * @param responseError Catchoom error item containing the HTTP error code
	 * and phrase and an error description. 
	 */
	public void requestFailedResponse(CatchoomErrorResponseItem responseError);
	
}
