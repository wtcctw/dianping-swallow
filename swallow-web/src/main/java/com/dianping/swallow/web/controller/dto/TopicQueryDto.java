package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月17日下午5:49:15
 */
public class TopicQueryDto extends BaseDto{

	private String topic;
	
	private String prop;
	
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
	
	
}