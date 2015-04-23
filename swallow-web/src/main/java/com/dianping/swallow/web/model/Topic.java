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
	private String 				id;

	private String 				name;
	private String 				prop;
	private String 				dept;
	private String 				time;
	private long 				messageNum;

	public Topic() {
	}

	public Topic(String id, String name, String prop, String dept, String time,  long messageNum) {
		this.id = id;
		this.name = name;
		this.prop = prop;
		this.dept = dept;
		this.time = time;
		this.messageNum = messageNum;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProp() {
		return prop;
	}

	public void setProp(String prop) {
		this.prop = prop;
	}
	
	public String getDept() {
		return dept;
	}

	public void setDept(String dept) {
		this.dept = dept;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	public long getMessageNum() {
		return messageNum;
	}

	public void setMessageNum(long messageNum) {
		this.messageNum = messageNum;
	}
	
	@Override
	public String toString() {
		return id + "::" + name + "::" + prop + "::" + dept + "::" + time +  "::" + messageNum;
	}
}