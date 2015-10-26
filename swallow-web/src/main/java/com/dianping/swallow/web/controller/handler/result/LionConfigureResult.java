package com.dianping.swallow.web.controller.handler.result;


import com.dianping.swallow.web.controller.handler.data.Treatable;

/**
 * @author mingdongli
 *
 * 2015年9月24日下午3:15:58
 */
public class LionConfigureResult implements Result, Treatable{

	private String mongoServer;
	
	private String consumerServer;
	
	private int size4SevenDay;

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

	public int getSize4SevenDay() {
		return size4SevenDay;
	}

	public void setSize4SevenDay(int size4SevenDay) {
		this.size4SevenDay = size4SevenDay;
	}
}
