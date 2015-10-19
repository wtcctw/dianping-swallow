package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 *         2015年8月5日 上午10:46:40
 */
public class ProducerBaseAlarmSetting {

	private QPSAlarmSetting qpsAlarmSetting;

	private long delay;

	public QPSAlarmSetting getQpsAlarmSetting() {
		return qpsAlarmSetting;
	}

	public void setQpsAlarmSetting(QPSAlarmSetting qpsAlarmSetting) {
		this.qpsAlarmSetting = qpsAlarmSetting;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	@Override
	public String toString() {
		return "ProducerClientBaseAlarmSetting [ qpsAlarmSetting = " + qpsAlarmSetting + ", delay = " + delay + "]";
	}
}
