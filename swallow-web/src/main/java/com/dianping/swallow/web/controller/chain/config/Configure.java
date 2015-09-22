package com.dianping.swallow.web.controller.chain.config;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月21日下午7:49:31
 */
public interface Configure {

	void buildConfigure(final TopicApplyDto topicApplyDto, ConfigureResult configureResult);
	
	static class ConfigureResult{
		
		private String mongoServer;
		
		private String consumerServer;
		
		private int size4sevenday;
		
		private ResponseStatus responseStatus;

		public String getMongoServer() {
			return mongoServer;
		}

		public void setMongoServer(String mongoServer) {
			this.mongoServer = mongoServer;
		}

		public String getConsumerServer() {
			return consumerServer;
		}

		public void setConsumerServer(String consumerServer) {
			this.consumerServer = consumerServer;
		}

		public int getSize4servenday() {
			return size4sevenday;
		}

		public void setSize4servenday(int size4servenday) {
			this.size4sevenday = size4servenday;
		}

		public ResponseStatus getResponseStatus() {
			return responseStatus;
		}

		public void setResponseStatus(ResponseStatus responseStatus) {
			this.responseStatus = responseStatus;
		}
		
	}
	
}
