package com.dianping.swallow.web.monitor;

public class OrderEntity implements Comparable<OrderEntity> {

	private String topicName;

	private String consumerId;

	private long sumData;

	private double avgData;

	public OrderEntity() {

	}

	public OrderEntity(String topicName, String consumerId, long sumData, long timeInterval) {
		this.topicName = topicName;
		this.consumerId = consumerId;
		this.sumData = sumData;
		accuAvgData(timeInterval);
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public long getSumData() {
		return sumData;
	}

	public void setSumData(long sumData) {
		this.sumData = sumData;
	}

	@Override
	public int compareTo(OrderEntity obj) {
		return this.getSumData() > obj.getSumData() ? 1 : this.getSumData() < obj.getSumData() ? -1 : 0;
	}

	public double getAvgData() {
		return avgData;
	}

	public void setAvgData(double avgData) {
		this.avgData = avgData;
	}

	private double accuAvgData(long timeInterval) {
		if (timeInterval == 0L) {
			return 0.0;
		} else {
			setAvgData(this.sumData * 1.0 / timeInterval);
			return this.avgData;
		}
	}
}
