package com.dianping.swallow.web.controller.dto;

/**
 * 
 * @author mingdongli
 *
 * 2015年7月14日下午2:26:06
 */
public class ServerResourceDto {
	
	private String id;
	
	private String ip;
	
	private String hostname;
	
	private boolean alarm;
	
	private long sendpeak;
	
	private long sendvalley;
	
	private int sendfluctuation;
	
	private long sendfluctuationBase;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public boolean isAlarm() {
		return alarm;
	}

	public void setAlarm(boolean alarm) {
		this.alarm = alarm;
	}

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

	public long getSendfluctuationBase() {
		return sendfluctuationBase;
	}

	public void setSendfluctuationBase(long sendfluctuationBase) {
		this.sendfluctuationBase = sendfluctuationBase;
	}

}
