package com.dianping.swallow.web.service;

import java.util.List;

import org.apache.http.NameValuePair;

/**
 * 
 * http api
 * 
 * @author qiyin
 *
 */
public interface HttpService {

	/**
	 * http post request
	 * 
	 * @param url
	 * @param params
	 * @return json
	 */
	public HttpResult httpPost(String url, List<NameValuePair> params);

	/**
	 * http get request
	 * 
	 * @param url
	 * @return json
	 */
	public HttpResult httpGet(String url);

	/**
	 * 
	 * http request result
	 * 
	 * @author qiyin
	 *
	 */
	public static class HttpResult {

		private boolean isSuccess;

		private String responseBody;

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}

		public String getResponseBody() {
			return responseBody;
		}

		public void setResponseBody(String responseBody) {
			this.responseBody = responseBody;
		}
	}

}
