package com.dianping.swallow.web.model.resource;


/**
 * @author mingdongli
 *
 * 2015年8月13日上午11:02:32
 */
public class Topic2ProducerServer extends BaseResource{

	private String topic;
	
	private String producerServer;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getProducerServer() {
		return producerServer;
	}

	public void setProducerServer(String producerServer) {
		this.producerServer = producerServer;
	}

	@Override
	public String toString() {
		return "Topic2ProducerServer [topic=" + topic + ", producerServer=" + producerServer + ", toString()="
				+ super.toString() + "]";
	}

}
