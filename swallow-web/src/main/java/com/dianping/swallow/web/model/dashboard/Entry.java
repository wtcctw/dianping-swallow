package com.dianping.swallow.web.model.dashboard;

import org.springframework.data.annotation.Id;

/**
 * @author mingdongli
 *
 *         2015年7月8日上午10:32:58
 */
public class Entry implements Comparable<Entry> {

	private String server;

	@Id
	private String topic;

	@Id
	private String consumerId;

	private float senddelay;

	private float ackdelay;

	private long accu;

	private int senddelayAlarm;

	private int ackdelayAlarm;

	private int accuAlarm;

	private Float normalizedSendDelaly;

	private Float normalizedAckDelaly;

	private Float normalizedAccu;

	private Integer numAlarm;

	private String name;

	private String email;

	private String dpMobile;

	public Entry() {

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

	public Float getNormalizedSendDelaly() {
		return normalizedSendDelaly;
	}

	public Entry setNormalizedSendDelaly(Float normalizedSendDelaly) {
		this.normalizedSendDelaly = normalizedSendDelaly;
		return this;
	}

	public Float getNormalizedAckDelaly() {
		return normalizedAckDelaly;
	}

	public Entry setNormalizedAckDelaly(Float normalizedAckDelaly) {
		this.normalizedAckDelaly = normalizedAckDelaly;
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
	public int compareTo(Entry entry) {

		int numAlarm = this.getNumAlarm().compareTo(entry.getNumAlarm());
		if (numAlarm == 0) {
			Float _f = this.normalizedSendDelaly +  this.normalizedAckDelaly + this.normalizedAccu;
			Float f = entry.getNormalizedSendDelaly() + entry.getNormalizedAckDelaly() + entry.getNormalizedAccu();

			return _f.compareTo(f);
		} else {
			return numAlarm;
		}
	}

	@Override
	public String toString() {
		return "Entry [server=" + server + ", topic=" + topic + ", consumerId=" + consumerId + ", name=" + name
				+ ", senddelay=" + senddelay + ", ackdelay=" + ackdelay + ", accu=" + accu + ", senddelayAlarm="
				+ senddelayAlarm + ", ackdelayAlarm=" + ackdelayAlarm + ", accuAlarm=" + accuAlarm
				+ ", baseSendDelaly=" + normalizedSendDelaly + ", baseAckDelaly=" + normalizedAckDelaly + ", baseAccu=" + normalizedAccu
				+ ", numAlarm=" + numAlarm + ", email=" + email + ", dpMobile=" + dpMobile + "]";
	}

}
