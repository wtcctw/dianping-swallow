package com.dianping.swallow.web.model.alarm;

import java.util.Date;

import org.springframework.data.annotation.Id;

/**
 * 
 * @author qiyin
 *
 */
public class Alarm implements Cloneable {

	@Id
	private String id;

	private long eventId;

	private int number;

	private AlarmLevelType type;

	private SendType sendType;

	private String title;

	private String body;

	private String related;

	private RelatedType relatedType;

	private String receiver;

	private ResultType resultType;

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

	public SendType getSendType() {
		return sendType;
	}

	public Alarm setSendType(SendType sendType) {
		this.sendType = sendType;
		return this;
	}

	public long getEventId() {
		return eventId;
	}

	public Alarm setEventId(long eventId) {
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

	public String getRelated() {
		return related;
	}

	public Alarm setRelated(String related) {
		this.related = related;
		return this;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public Alarm setResultType(ResultType resultType) {
		this.resultType = resultType;
		return this;
	}

	public RelatedType getRelatedType() {
		return relatedType;
	}

	public Alarm setRelatedType(RelatedType relatedType) {
		this.relatedType = relatedType;
		return this;
	}

	@Override
	public Object clone() {
		Alarm alarm;
		try {
			alarm = (Alarm) super.clone();
		} catch (CloneNotSupportedException e) {
			alarm = new Alarm();
		}
		alarm.id = this.id;
		alarm.eventId = this.eventId;
		alarm.number = this.number;
		if (this.createTime != null) {
			alarm.createTime = new Date(this.createTime.getTime());
		}
		alarm.sendType = this.sendType;
		alarm.body = this.body;
		alarm.title = this.title;
		alarm.type = this.type;
		alarm.sourceIp = this.sourceIp;
		alarm.receiver = this.receiver;
		return alarm;
	}

}
