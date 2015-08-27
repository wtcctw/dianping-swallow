package com.dianping.swallow.web.monitor.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public abstract class ReStatsData {

	@Id
	private String id;

	@Indexed(name = "IX_FROMTIMEKEY")
	private long fromTimeKey;
	
	@Indexed(name = "IX_TOTIMEKEY")
	private long toTimeKey;

	public long getFromTimeKey() {
		return fromTimeKey;
	}

	public void setFromTimeKey(long fromTimeKey) {
		this.fromTimeKey = fromTimeKey;
	}

	public long getToTimeKey() {
		return toTimeKey;
	}

	public void setToTimeKey(long toTimeKey) {
		this.toTimeKey = toTimeKey;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "ReStatsData [id=" + id + ", fromTimeKey=" + fromTimeKey + ", toTimeKey=" + toTimeKey + "]";
	}
	
	
}
