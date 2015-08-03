package com.dianping.swallow.web.model.stats;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import com.dianping.swallow.web.alarm.EventReporter;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:57:09
 */
public abstract class StatsData {

	@Id
	private String id;

	@Indexed
	private long timeKey;

	@Transient
	protected EventReporter eventReporter;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimeKey() {
		return timeKey;
	}

	public void setTimeKey(long timeKey) {
		this.timeKey = timeKey;
	}

	public void setEventReporter(EventReporter eventReporter) {
		this.eventReporter = eventReporter;
	}

	@Override
	public String toString() {
		return "StatsData [id=" + id + ", timeKey=" + timeKey + "]";
	}

}
