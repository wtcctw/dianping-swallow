package com.dianping.swallow.web.controller.filter.result;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午3:15:58
 */
public class LionConfigure {

	private String mongoServer;
	
	private String consumerServer;
	
	private int size4sevenday;

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

	public int getSize4sevenday() {
		return size4sevenday;
	}

	public void setSize4sevenday(int size4sevenday) {
		this.size4sevenday = size4sevenday;
	}
}
