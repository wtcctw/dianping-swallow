package com.dianping.swallow.web.model.alarm.backup;

/**
 * 
 * @author qiyin
 *
 */
public class ConsumerBaseAlarmSetting {

	private QPSAlarmSetting senderQpsAlarmSetting;

	private QPSAlarmSetting ackQpsAlarmSetting;

	private long senderDelay;

	private long ackDelay;

	private long accumulation;

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
		return "ConsumerClientBaseAlarmSetting [senderQpsAlarmSetting = " + senderQpsAlarmSetting
				+ ", ackQpsAlarmSetting = " + ackQpsAlarmSetting + ", senderDelay = " + senderDelay + ", ackDelay = "
				+ ackDelay + ", accumulation = " + accumulation + "]";
	}

	public long getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(long accumulation) {
		this.accumulation = accumulation;
	}

	public QPSAlarmSetting getSenderQpsAlarmSetting() {
		return senderQpsAlarmSetting;
	}

	public void setSenderQpsAlarmSetting(QPSAlarmSetting senderQpsAlarmSetting) {
		this.senderQpsAlarmSetting = senderQpsAlarmSetting;
	}

	public QPSAlarmSetting getAckQpsAlarmSetting() {
		return ackQpsAlarmSetting;
	}

	public void setAckQpsAlarmSetting(QPSAlarmSetting ackQpsAlarmSetting) {
		this.ackQpsAlarmSetting = ackQpsAlarmSetting;
	}
}
