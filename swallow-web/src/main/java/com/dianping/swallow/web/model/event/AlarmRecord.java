package com.dianping.swallow.web.model.event;

/**
 * 
 * @author qiyin
 *
 * 2015年8月10日 上午9:30:22
 */
public class AlarmRecord {

	private long lastAlarmTime;

	private long alarmCount;

	public long getLastAlarmTime() {
		return lastAlarmTime;
	}

	public void setLastAlarmTime(long lastAlarmTime) {
		this.lastAlarmTime = lastAlarmTime;
	}

	public long getAlarmCount() {
		return alarmCount;
	}

	public void setAlarmCount(long alarmCount) {
		this.alarmCount = alarmCount;
	}

	public boolean isFirstTime(long lastTimeSpan) {
		return (System.currentTimeMillis() - lastAlarmTime) > lastTimeSpan;
	}
}
