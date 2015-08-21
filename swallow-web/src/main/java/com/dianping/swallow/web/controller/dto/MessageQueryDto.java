package com.dianping.swallow.web.controller.dto;

import java.util.Date;


/**
 * @author mingdongli
 *
 * 2015年8月17日下午7:47:27
 */
public class MessageQueryDto extends BaseDto{
	
	private String topic;
	
	private String messageId;
	
	private Date startdt;
	
	private Date stopdt;
	
	private String basemid;
	
	private boolean sort;

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

	public Date getStartdt() {
		return startdt;
	}

	public void setStartdt(Date startdt) {
		this.startdt = startdt;
	}

	public Date getStopdt() {
		return stopdt;
	}

	public void setStopdt(Date stopdt) {
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
