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
	
	private String eventId;
	
	private int number;

	private AlarmLevelType type;

	private SendType sendType;

	private String title;

	private String body;

	private String receiver;

	private Date createTime;

	private String sourceIp;

	public Alarm() {

	}

	public String getId() {
		return id;
	}

	public Alarm setId(String id) {
		this.id = id;
		return this;
	}

	public AlarmLevelType getType() {
		return type;
	}

	public Alarm setType(AlarmLevelType type) {
		this.type = type;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Alarm setTitle(String title) {
		this.title = title;
		return this;
	}

	public String getBody() {
		return body;
	}

	public Alarm setBody(String body) {
		this.body = body;
		return this;
	}

	public String getReceiver() {
		return receiver;
	}

	public Alarm setReceiver(String receiver) {
		this.receiver = receiver;
		return this;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public Alarm setCreateTime(Date createTime) {
		this.createTime = createTime;
		return this;
	}

	public String getSourceIp() {
		return sourceIp;
	}

	public Alarm setSourceIp(String sourceIp) {
		this.sourceIp = sourceIp;
		return this;
	}

	@Override
	public String toString() {
		return "Alarm[ id = " + id + ", type = " + type + ", title = " + title + ",body = " + body + ",receiver = "
				+ receiver + ", createTime = " + createTime + ", sourceIp = " + sourceIp + "]";
	}

	public SendType getSendType() {
		return sendType;
	}

	public Alarm setSendType(SendType sendType) {
		this.sendType = sendType;
		return this;
	}

	public String getEventId() {
		return eventId;
	}

	public Alarm setEventId(String eventId) {
		this.eventId = eventId;
		return this;
	}

	public int getNumber() {
		return number;
	}

	public Alarm setNumber(int number) {
		this.number = number;
		return this;
	}

}
