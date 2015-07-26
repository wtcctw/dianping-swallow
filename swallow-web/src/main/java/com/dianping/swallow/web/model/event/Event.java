package com.dianping.swallow.web.model.event;

import java.util.Date;

import com.dianping.swallow.web.model.alarm.AlarmType;

/**
 * 
 * @author qiyin
 *
 */
public abstract class Event {
	
	private String eventId;
	
	private Date createTime;
	
	private AlarmType alarmType;
	
	private EventType eventType;
	
	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public String getEventId() {
		return eventId;
	}

	public void setEventId(String eventId) {
		this.eventId = eventId;
	}

	public AlarmType getAlarmType() {
		return alarmType;
	}

	public void setAlarmType(AlarmType alarmType) {
		this.alarmType = alarmType;
	}

	public EventType getEventType() {
		return eventType;
	}

	public void setEventType(EventType eventType) {
		this.eventType = eventType;
	}
}
