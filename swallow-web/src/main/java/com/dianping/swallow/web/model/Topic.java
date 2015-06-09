package com.dianping.swallow.web.model;

import org.springframework.data.annotation.Id;

/**
 * @author mingdongli
 *
 * 2015年4月22日 上午12:06:00
 */
public class Topic {

	// id will be used for storing MongoDB _id
	@Id
	private String id;
	private String name;
	private String prop;
	private String time;
	private long messageNum;

	public Topic() {
	}

	public String getId() {
		return id;
	}

	public Topic setId(String id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public Topic setName(String name) {
		this.name = name;
		return this;
	}

	public String getProp() {
		return prop;
	}

	public Topic setProp(String prop) {
		this.prop = prop;
		return this;
	}

	public String getTime() {
		return time;
	}

	public Topic setTime(String time) {
		this.time = time;
		return this;
	}

	public long getMessageNum() {
		return messageNum;
	}

	public Topic setMessageNum(long messageNum) {
		this.messageNum = messageNum;
		return this;
	}

	@Override
	public String toString() {
		return id + "::" + name + "::" + prop + "::" + time
				+ "::" + messageNum;
	}
}