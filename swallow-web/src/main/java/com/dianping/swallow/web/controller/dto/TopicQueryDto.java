package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月17日下午5:49:15
 */
public class TopicQueryDto {

	private int offset;
	
	private int limit;
	
	private String topic;
	
	private String prop;

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
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
