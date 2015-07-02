package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerClientBaseAlarmSetting {

	private QPSAlarmSetting qpsAlarmSetting;

	private long fetcherDelay;

	private long ackDelay;

	private long piler;

	public QPSAlarmSetting getQpsAlarmSetting() {
		return qpsAlarmSetting;
	}

	public void setQpsAlarmSetting(QPSAlarmSetting qpsAlarmSetting) {
		this.qpsAlarmSetting = qpsAlarmSetting;
	}

	public long getFetcherDelay() {
		return fetcherDelay;
	}

	public void setFetcherDelay(long fetcherDelay) {
		this.fetcherDelay = fetcherDelay;
	}

	public long getAckDelay() {
		return ackDelay;
	}

	public void setAckDelay(long ackDelay) {
		this.ackDelay = ackDelay;
	}

	public long getPiler() {
		return piler;
	}

	public void setPiler(long piler) {
		this.piler = piler;
	}

	public String toString() {
		return "ConsumerClientBaseAlarmSetting [qpsAlarmSetting = " + qpsAlarmSetting + ",fetcherDelay = "
				+ fetcherDelay + ", ackDelay = " + ackDelay + ", piler = " + piler + "]";
	}
}
