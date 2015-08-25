package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月17日下午5:49:15
 */
public class TopicQueryDto extends BaseDto{

	private String topic;
	
	private String prop;
	
	private String producerServer;
	
	private String consumerIp;
	
	public TopicQueryDto(){
		
	}
	
	public TopicQueryDto(String topic){
		this(topic, null);
	}
	
	public TopicQueryDto(String topic, String prop){
		
		this.topic = topic;
		this.prop = prop;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}

	public String getProducerServer() {
		return producerServer;
	}

	public void setProducerServer(String producerServer) {
		this.producerServer = producerServer;
	}

	public String getConsumerIp() {
		return consumerIp;
	}

	public void setConsumerIp(String consumerIp) {
		this.consumerIp = consumerIp;
	}
	
}
