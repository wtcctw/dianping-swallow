package com.dianping.swallow.web.model.statis;

import org.springframework.data.annotation.Id;

public abstract class AbstractServerStatsData {
	
	@Id
	private String id;
	
	private long timeKey;

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

	@Override
	public String toString() {
		return "AbstractServerStatsData [id=" + id + ", timeKey=" + timeKey + "]";
	}

}
