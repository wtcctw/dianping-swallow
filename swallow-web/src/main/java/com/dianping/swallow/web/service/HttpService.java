package com.dianping.swallow.web.service;

import java.util.List;

import org.apache.http.NameValuePair;

import com.dianping.swallow.web.model.alarm.ResultType;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:43:08
 */
public interface HttpService {

	/**
	 * http post request
	 * 
	 * @param url
	 * @param params
	 * @return json
	 */
	HttpResult httpPost(String url, List<NameValuePair> params);

	/**
	 * http get request
	 * 
	 * @param url
	 * @return json
	 */
	HttpResult httpGet(String url);

	/**
	 * 
	 * http request result
	 * 
	 * @author qiyin
	 *
	 */
	public static class HttpResult {

		private boolean isSuccess;

		private ResultType resultType;

		private String responseBody;

		public boolean isSuccess() {
			return isSuccess;
		}

		public void setSuccess(boolean isSuccess) {
			this.isSuccess = isSuccess;
		}

		public ResultType getResultType() {
			return resultType;
		}

		public void setResultType(ResultType resultType) {
			this.resultType = resultType;
		}

		public String getResponseBody() {
			return responseBody;
		}

		public void setResponseBody(String responseBody) {
			this.responseBody = responseBody;
		}

		@Override
		public String toString() {
			return "HttpResult [isSuccess=" + isSuccess + ", resultType=" + resultType + ", responseBody="
					+ responseBody + "]";
		}

	}

}
