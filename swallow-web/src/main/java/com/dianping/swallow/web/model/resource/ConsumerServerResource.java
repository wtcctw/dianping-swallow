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
	
	private int port;
	
	private ServerType type;
	
	private String ipCorrelated;
	
	private QPSAlarmSetting sendAlarmSetting;
	
	private QPSAlarmSetting ackAlarmSetting;

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public ServerType getType() {
		return type;
	}

	public void setType(ServerType type) {
		this.type = type;
	}

	public String getIpCorrelated() {
		return ipCorrelated;
	}

	public void setIpCorrelated(String ipCorrelated) {
		this.ipCorrelated = ipCorrelated;
	}

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
		return "ConsumerServerResource [port=" + port + ", type=" + type + ", ipCorrelated=" + ipCorrelated
				+ ", sendAlarmSetting=" + sendAlarmSetting + ", ackAlarmSetting=" + ackAlarmSetting + ", toString()="
				+ super.toString() + "]";
	}
	
}
