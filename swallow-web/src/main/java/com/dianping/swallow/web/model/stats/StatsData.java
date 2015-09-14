package com.dianping.swallow.web.model.stats;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.IndexDirection;
import org.springframework.data.mongodb.core.index.Indexed;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;

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
	protected EventFactory eventFactory;

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

	public void setEventFactory(EventFactory eventFactory) {
		this.eventFactory = eventFactory;
	}

	@Override
	public String toString() {
		return "StatsData [id=" + id + ", timeKey=" + timeKey + "]";
	}

}
