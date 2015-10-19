package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月17日下午5:49:15
 */
public class TopicQueryDto extends BaseQueryDto{

	private String topic;
	
	private String producerServer;
	
	private boolean inactive;
	
	public TopicQueryDto(){
		
	}
	
	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getProducerServer() {
		return producerServer;
	}

	public boolean isInactive() {
		return inactive;
	}

	public void setInactive(boolean inactive) {
		this.inactive = inactive;
	}

}
