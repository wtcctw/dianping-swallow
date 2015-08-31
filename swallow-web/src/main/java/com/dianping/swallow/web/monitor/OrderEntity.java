package com.dianping.swallow.web.monitor;

public class OrderEntity implements Comparable<OrderEntity> {

	private String topicName;

	private String consumerId;

	private long sumData;

	public OrderEntity() {

	}

	public OrderEntity(String topicName, String consumerId, long sumData) {
		this.topicName = topicName;
		this.consumerId = consumerId;
		this.sumData = sumData;
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
}
