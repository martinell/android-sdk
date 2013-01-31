// (c) Catchoom Technologies S.L.
// Licensed under the MIT license.
// https://raw.github.com/catchoom/android-sdk/master/LICENSE
// All warranties and liabilities are disclaimed.
package com.catchoom.api;

/**
 * CatchoomErrorResponseItem is used to comunicate to a {@link CatchoomResponseHandler}
 * an error raised during a request.
 * @author Catchoom
 *
 */
public class CatchoomErrorResponseItem {
	
	private int errorCode = -1;
	private String errorPhrase = null;
	private String errorDescription = null;
	
	CatchoomErrorResponseItem(int errorCode, String errorPhrase, String errorDescription) {
		this.errorCode = errorCode;
		this.errorPhrase = errorPhrase;
		this.errorDescription = errorDescription;
	}
	
	/**
	 * Gets the HTTP error code.
	 * @return The error code.
	 */
	public int getErrorCode() {
		return errorCode;
	}
	
	/**
	 * Gets the HTTP error phrase.
	 * @return The error phrase.
	 */
	public String getErrorPhrase() {
		return errorPhrase;
	}
	
	/**
	 * Gets the Catchoom error description. <strong>Watch out:</strong> this
	 * error description should be used for debugging purposes only and not relay
	 * on it when implementing your own error handling system.
	 * @return
	 */
	public String getErrorDescription() {
		return errorDescription;
	}
}
