package com.dianping.swallow.web.monitor.dashboard;

/**
 * @author mingdongli
 *
 *         2015年7月8日上午10:32:58
 */
public class Entry implements Comparable<Entry>{

	private String consumerId;

	private long senddelay;

	private long ackdelay;

	private long accu;

	private String topic;
	
	private int senddelayAlarm;
	
	private int ackdelayAlarm;
	
	private int accuAlarm;
	
	private Integer numAlarm;
	
	public Entry() {

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

	public long getSenddelay() {
		return senddelay;
	}

	public Entry setSenddelay(long senddelay) {
		this.senddelay = senddelay;
		return this;
	}

	public long getAckdelay() {
		return ackdelay;
	}

	public Entry setAckdelay(long ackdelay) {
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

	@Override
	public int compareTo(Entry entry) {

		return entry.getNumAlarm().compareTo(this.getNumAlarm());
	}

	@Override
	public String toString() {
		return "Entry [consumerId=" + consumerId + ", senddelay=" + senddelay + ", ackdelay=" + ackdelay + ", accu="
				+ accu + ", topic=" + topic + ", senddelayAlarm=" + senddelayAlarm + ", ackdelayAlarm=" + ackdelayAlarm
				+ ", accuAlarm=" + accuAlarm + ", numAlarm=" + numAlarm + "]";
	}
	
}
