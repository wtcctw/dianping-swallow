package com.dianping.swallow.web.model.statis;

import org.springframework.data.annotation.Id;

public class ConsumerIdStatsData {
	
	@Id
	private String id;
	
	private String topicName;
	
	private long timeKey;
	
	private String consumerId;
	
	private ConsumerBaseStatsData statisData;
	
	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public ConsumerBaseStatsData getStatisData() {
		return statisData;
	}

	public void setStatisData(ConsumerBaseStatsData statisData) {
		this.statisData = statisData;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public long getTimeKey() {
		return timeKey;
	}

	public void setTimeKey(long timeKey) {
		this.timeKey = timeKey;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
