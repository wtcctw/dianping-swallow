package com.dianping.swallow.web.model.statis;

public class ConsumerTopicStatsData {
	private String id;

	private long timeKey;

	private String topicName;

	private ConsumerBaseStatsData consumerStatisData;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public long getTimeKey() {
		return timeKey;
	}

	public void setTimeKey(long timeKey) {
		this.timeKey = timeKey;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public ConsumerBaseStatsData getConsumerStatisData() {
		return consumerStatisData;
	}

	public void setConsumerStatisData(ConsumerBaseStatsData consumerStatisData) {
		this.consumerStatisData = consumerStatisData;
	}

}
