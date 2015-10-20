package com.dianping.swallow.web.model.event;

/**
 * 
 * @author qiyin
 *
 *         2015年8月10日 上午9:30:22
 */
public class AlarmRecord {

	private long lastAlarmTime;

	private long checkAlarmTime;

	private long alarmCount;

	public long getLastAlarmTime() {
		return lastAlarmTime;
	}

	public AlarmRecord setLastAlarmTime(long lastAlarmTime) {
		this.lastAlarmTime = lastAlarmTime;
		return this;
	}

	public long getAlarmCount() {
		return alarmCount;
	}

	public AlarmRecord setAlarmCount(long alarmCount) {
		this.alarmCount = alarmCount;
		return this;
	}

	public long getCheckAlarmTime() {
		return checkAlarmTime;
	}

	public AlarmRecord setCheckAlarmTime(long checkAlarmTime) {
		this.checkAlarmTime = checkAlarmTime;
		return this;
	}

	@Override
	public String toString() {
		return "AlarmRecord [lastAlarmTime=" + lastAlarmTime + ", checkAlarmTime=" + checkAlarmTime + ", alarmCount="
				+ alarmCount + "]";
	}

}
