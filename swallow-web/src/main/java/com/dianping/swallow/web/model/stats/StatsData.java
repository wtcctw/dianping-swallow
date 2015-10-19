package com.dianping.swallow.web.model.stats;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactoryImpl;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.StatisEvent;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:57:09
 */
public abstract class StatsData {

	@Id
	private String id;

	@Indexed(name = "IX_TIMEKEY", direction = IndexDirection.ASCENDING)
	private long timeKey;

	@Transient
	protected EventReporter eventReporter;

	@Transient
	protected EventFactoryImpl eventFactory;

	@Transient
	protected EventType eventType;

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

	public void setEventFactory(EventFactoryImpl eventFactory) {
		this.eventFactory = eventFactory;
	}

	@Override
	public String toString() {
		return "StatsData [id=" + id + ", timeKey=" + timeKey + "]";
	}

	protected void report(StatisEvent event, long currentValue, long expectedValue, StatisType statisType) {
		eventReporter.report(event.setCurrentValue(currentValue).setExpectedValue(expectedValue)
				.setStatisType(statisType).setCreateTime(new Date()).setEventType(eventType));
	}

}
