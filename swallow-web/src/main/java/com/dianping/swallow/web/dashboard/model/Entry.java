package com.dianping.swallow.web.dashboard.model;

import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;


/**
 * @author mingdongli
 *
 *         2015年7月8日上午10:32:58
 */
public class Entry {

	private String server;

	private String topic;

	private String consumerId;

	private float senddelay;

	private float ackdelay;

	private long accu;

	private int senddelayAlarm;

	private int ackdelayAlarm;

	private int accuAlarm;

	private Float normalizedSendDelay;

	private Float normalizedAckDelay;

	private Float normalizedAccu;

	private Integer numAlarm;

	private String name;

	private String email;

	private String dpMobile;

	public Entry() {

	}
	
	public void setAlert(ConsumerBaseAlarmSetting consumerBaseAlarmSetting, boolean whiteList){
		
		if(!whiteList){
			
			long baseSenddelay = consumerBaseAlarmSetting.getSendDelay();
			long baseackdelay = consumerBaseAlarmSetting.getAckDelay();
			float baseAccu = (float) consumerBaseAlarmSetting.getAccumulation();
			
			this.senddelayAlarm = this.senddelay >= baseSenddelay ? 1 : 0;
			this.ackdelayAlarm = this.ackdelay >= baseackdelay ? 1 : 0;
			this.accuAlarm = this.accu >= baseAccu ? 1 : 0;
			this.numAlarm = this.senddelayAlarm + this.ackdelayAlarm + this.accuAlarm;
			
			this.normalizedSendDelay = this.senddelay / baseSenddelay;
			this.normalizedAckDelay = this.ackdelay / baseackdelay;
			this.normalizedAccu = this.accu / baseAccu;
		}else{
			this.senddelayAlarm = 0;
			this.ackdelayAlarm = 0;
			this.accuAlarm = 0;
			this.numAlarm = 0;
			this.normalizedSendDelay = this.senddelay;
			this.normalizedAckDelay = this.ackdelay;
			this.normalizedAccu = (float) this.accu;
		}

	}
	
	public String getServer() {
		return server;
	}

	public Entry setServer(String server) {
		this.server = server;
		return this;
	}

	public String getTopic() {
		return topic;
	}

	public Entry setTopic(String topic) {
		this.topic = topic;
		return this;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public Entry setConsumerId(String consumerId) {
		this.consumerId = consumerId;
		return this;
	}

	public String getName() {
		return name;
	}

	public Entry setName(String name) {
		this.name = name;
		return this;
	}

	public float getSenddelay() {
		return senddelay;
	}

	public Entry setSenddelay(float senddelay) {
		this.senddelay = senddelay;
		return this;
	}

	public float getAckdelay() {
		return ackdelay;
	}

	public Entry setAckdelay(float ackdelay) {
		this.ackdelay = ackdelay;
		return this;
	}

	public long getAccu() {
		return accu;
	}

	public Entry setAccu(long accu) {
		this.accu = accu;
		return this;
	}

	public int getSenddelayAlarm() {
		return senddelayAlarm;
	}

	public Entry setSenddelayAlarm(int senddelayAlarm) {
		this.senddelayAlarm = senddelayAlarm;
		return this;
	}

	public int getAckdelayAlarm() {
		return ackdelayAlarm;
	}

	public Entry setAckdelayAlarm(int ackdelayAlarm) {
		this.ackdelayAlarm = ackdelayAlarm;
		return this;
	}

	public int getAccuAlarm() {
		return accuAlarm;
	}

	public Entry setAccuAlarm(int accuAlarm) {
		this.accuAlarm = accuAlarm;
		return this;
	}

	public Integer getNumAlarm() {
		return numAlarm;
	}

	public Entry setNumAlarm(Integer numAlarm) {
		this.numAlarm = numAlarm;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Entry setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getDpMobile() {
		return dpMobile;
	}

	public Entry setDpMobile(String dpMobile) {
		this.dpMobile = dpMobile;
		return this;
	}

	public Float getNormalizedSendDelay() {
		return normalizedSendDelay;
	}

	public Entry setNormalizedSendDelay(Float normalizedSendDelay) {
		this.normalizedSendDelay = normalizedSendDelay;
		return this;
	}

	public Float getNormalizedAckDelay() {
		return normalizedAckDelay;
	}

	public Entry setNormalizedAckDelay(Float normalizedAckDelay) {
		this.normalizedAckDelay = normalizedAckDelay;
		return this;
	}

	public Float getNormalizedAccu() {
		return normalizedAccu;
	}

	public Entry setNormalizedAccu(Float normalizedAccu) {
		this.normalizedAccu = normalizedAccu;
		return this;
	}

	@Override
	public String toString() {
		return "Entry [server=" + server + ", topic=" + topic + ", consumerId=" + consumerId + ", senddelay="
				+ senddelay + ", ackdelay=" + ackdelay + ", accu=" + accu + ", senddelayAlarm=" + senddelayAlarm
				+ ", ackdelayAlarm=" + ackdelayAlarm + ", accuAlarm=" + accuAlarm + ", normalizedSendDelaly="
				+ normalizedSendDelay + ", normalizedAckDelaly=" + normalizedAckDelay + ", normalizedAccu="
				+ normalizedAccu + ", numAlarm=" + numAlarm + ", name=" + name + ", email=" + email + ", dpMobile="
				+ dpMobile + "]";
	}

}
