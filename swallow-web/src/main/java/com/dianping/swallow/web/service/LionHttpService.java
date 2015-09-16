package com.dianping.swallow.web.service;


/**
 * @author mingdongli
 *
 * 2015年9月9日下午6:11:14
 */
public interface LionHttpService {
	
	LionHttpResponse setUsingGet(int id, String env, String key, String value, String group);

	LionHttpResponse setUsingPost(int id, String env, String key, String value, String group);

	LionHttpResponse create(int id, String project, String key, String desc);

	LionHttpResponse get(int id, String env, String key, String group);

	public static class LionHttpResponse{
		
		private String status;
		
		private String message;
		
		private String result;

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}
		
	}
	
}
