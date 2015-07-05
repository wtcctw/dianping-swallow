package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerClientBaseAlarmSetting {

	private QPSAlarmSetting qpsAlarmSetting;

	private long senderDelay;

	private long ackDelay;

	private long accumulation;

	public QPSAlarmSetting getQpsAlarmSetting() {
		return qpsAlarmSetting;
	}

	public void setQpsAlarmSetting(QPSAlarmSetting qpsAlarmSetting) {
		this.qpsAlarmSetting = qpsAlarmSetting;
	}

	public long getSenderDelay() {
		return senderDelay;
	}

	public void setSenderDelay(long senderDelay) {
		this.senderDelay = senderDelay;
	}

	public long getAckDelay() {
		return ackDelay;
	}

	public void setAckDelay(long ackDelay) {
		this.ackDelay = ackDelay;
	}

	@Override
	public String toString() {
		return "ConsumerClientBaseAlarmSetting [qpsAlarmSetting = " + qpsAlarmSetting + ",senderDelay = "
				+ senderDelay + ", ackDelay = " + ackDelay + ", accumulation = " + accumulation + "]";
	}

	public long getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(long accumulation) {
		this.accumulation = accumulation;
	}
}
