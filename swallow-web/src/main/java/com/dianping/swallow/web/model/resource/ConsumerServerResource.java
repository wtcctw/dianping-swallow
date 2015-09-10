package com.dianping.swallow.web.model.resource;

import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午2:44:55
 */
@Document(collection = "CONSUMER_SERVER_RESOURCE")
public class ConsumerServerResource extends ServerResource{
	
	private QPSAlarmSetting sendAlarmSetting;
	
	private QPSAlarmSetting ackAlarmSetting;

	public QPSAlarmSetting getSendAlarmSetting() {
		return sendAlarmSetting;
	}

	public void setSendAlarmSetting(QPSAlarmSetting sendAlarmSetting) {
		this.sendAlarmSetting = sendAlarmSetting;
	}

	public QPSAlarmSetting getAckAlarmSetting() {
		return ackAlarmSetting;
	}

	public void setAckAlarmSetting(QPSAlarmSetting ackAlarmSetting) {
		this.ackAlarmSetting = ackAlarmSetting;
	}

	@Override
	public String toString() {
		return "ConsumerServerResource [sendAlarmSetting=" + sendAlarmSetting + ", ackAlarmSetting=" + ackAlarmSetting
				+ ", toString()=" + super.toString() + "]";
	}
	
}
