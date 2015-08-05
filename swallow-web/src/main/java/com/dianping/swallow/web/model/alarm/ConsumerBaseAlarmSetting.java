package com.dianping.swallow.web.model.alarm;

/**
 * 
 * @author qiyin
 *
 * 2015年8月5日 上午10:46:32
 */
public class ConsumerBaseAlarmSetting {

	private QPSAlarmSetting sendQpsAlarmSetting;

	private QPSAlarmSetting ackQpsAlarmSetting;

	private long sendDelay;

	private long ackDelay;

	private long accumulation;

	public long getSendDelay() {
		return sendDelay;
	}

	public void setSendDelay(long sendDelay) {
		this.sendDelay = sendDelay;
	}

	public long getAckDelay() {
		return ackDelay;
	}

	public void setAckDelay(long ackDelay) {
		this.ackDelay = ackDelay;
	}

	@Override
	public String toString() {
		return "ConsumerClientBaseAlarmSetting [senderQpsAlarmSetting = " + sendQpsAlarmSetting
				+ ", ackQpsAlarmSetting = " + ackQpsAlarmSetting + ", senderDelay = " + sendDelay + ", ackDelay = "
				+ ackDelay + ", accumulation = " + accumulation + "]";
	}

	public long getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(long accumulation) {
		this.accumulation = accumulation;
	}

	public QPSAlarmSetting getSendQpsAlarmSetting() {
		return sendQpsAlarmSetting;
	}

	public void setSendQpsAlarmSetting(QPSAlarmSetting sendQpsAlarmSetting) {
		this.sendQpsAlarmSetting = sendQpsAlarmSetting;
	}

	public QPSAlarmSetting getAckQpsAlarmSetting() {
		return ackQpsAlarmSetting;
	}

	public void setAckQpsAlarmSetting(QPSAlarmSetting ackQpsAlarmSetting) {
		this.ackQpsAlarmSetting = ackQpsAlarmSetting;
	}
}
