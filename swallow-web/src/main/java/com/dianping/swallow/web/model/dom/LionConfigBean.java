package com.dianping.swallow.web.model.dom;


/**
 * @author mingdongli
 *
 * 2015年9月9日下午5:30:40
 */
public class LionConfigBean {
	
	private String topic;
	
	private String mongo;
	
	private String consumerServer;
	
	private int size;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMongo() {
		return mongo;
	}

	public void setMongo(String mongo) {
		this.mongo = mongo;
	}

	public String getConsumerServer() {
		return consumerServer;
	}

	public void setConsumerServer(String consumerServer) {
		this.consumerServer = consumerServer;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
