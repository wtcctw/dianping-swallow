package com.dianping.swallow.web.model.alarm;

import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * 
 * @author qiyin
 *
 */
public class Alarm {

	@Id
	private String id;

	private AlarmType type;

	private String title;

	private String body;

	private String receiver;

	private Date createTime;

	private String sourceIp;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public AlarmType getType() {
		return type;
	}

	public void setType(AlarmType type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public void setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
	}

	@Override
	public String toString() {
		return "Alarm[ id = " + id + ", type = " + type + ", title = " + title + ",body = " + body + ",receiver = "
				+ receiver + ", createTime = " + createTime + ", sourceIp = " + sourceIp + "]";
	}
}
