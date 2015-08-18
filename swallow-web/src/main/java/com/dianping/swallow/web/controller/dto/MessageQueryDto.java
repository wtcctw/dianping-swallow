package com.dianping.swallow.web.controller.dto;


/**
 * @author mingdongli
 *
 * 2015年8月17日下午7:47:27
 */
public class MessageQueryDto {
	
	private int offset;

	private int limit;
	
	private String topic;
	
	private String messageId;
	
	private String startdt;
	
	private String stopdt;
	
	private String basemid;
	
	private boolean sort;

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

	public String getMessageId() {
		return messageId;
	}

	public void setMessageId(String messageId) {
		this.messageId = messageId;
	}

	public String getStartdt() {
		return startdt;
	}

	public void setStartdt(String startdt) {
		this.startdt = startdt;
	}

	public String getStopdt() {
		return stopdt;
	}

	public void setStopdt(String stopdt) {
		this.stopdt = stopdt;
	}

	public String getBasemid() {
		return basemid;
	}

	public void setBasemid(String basemid) {
		this.basemid = basemid;
	}

	public boolean isSort() {
		return sort;
	}

	public void setSort(boolean sort) {
		this.sort = sort;
	}
	
}
