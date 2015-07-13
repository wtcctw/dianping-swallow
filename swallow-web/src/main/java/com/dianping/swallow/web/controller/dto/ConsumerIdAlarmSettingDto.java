package com.dianping.swallow.web.controller.dto;


public class ConsumerIdAlarmSettingDto {
	
	private long sendpeak;
	
	private long sendvalley;

	private int sendfluctuation;

	private long ackpeak;
	
	private long ackvalley;
	
	private int ackfluctuation;

	private long senddelay;
	
	private long ackdelay;
	
	private long accumulation;
	
	private String consumerId;

	public long getSendpeak() {
		return sendpeak;
	}

	public void setSendpeak(long sendpeak) {
		this.sendpeak = sendpeak;
	}

	public long getSendvalley() {
		return sendvalley;
	}

	public void setSendvalley(long sendvalley) {
		this.sendvalley = sendvalley;
	}

	public int getSendfluctuation() {
		return sendfluctuation;
	}

	public void setSendfluctuation(int sendfluctuation) {
		this.sendfluctuation = sendfluctuation;
	}

	public long getAckpeak() {
		return ackpeak;
	}

	public void setAckpeak(long ackpeak) {
		this.ackpeak = ackpeak;
	}

	public long getAckvalley() {
		return ackvalley;
	}

	public void setAckvalley(long ackvalley) {
		this.ackvalley = ackvalley;
	}

	public int getAckfluctuation() {
		return ackfluctuation;
	}

	public void setAckfluctuation(int ackfluctuation) {
		this.ackfluctuation = ackfluctuation;
	}

	public long getSenddelay() {
		return senddelay;
	}

	public void setSenddelay(long senddelay) {
		this.senddelay = senddelay;
	}

	public long getAckdelay() {
		return ackdelay;
	}

	public void setAckdelay(long ackdelay) {
		this.ackdelay = ackdelay;
	}

	public long getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(long accumulation) {
		this.accumulation = accumulation;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	
	
}
