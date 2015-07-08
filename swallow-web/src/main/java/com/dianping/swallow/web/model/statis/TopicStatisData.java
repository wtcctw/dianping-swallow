package com.dianping.swallow.web.model.statis;

public class TopicStatisData {
	
	private String id;
	
	private long timeKey;

	private String topicName;

	private ProducerBaseStatisData producerStatisData;
	
	private ConsumerBaseStatisData consumerStatisData;

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

	public ProducerBaseStatisData getProducerStatisData() {
		return producerStatisData;
	}

	public void setProducerStatisData(ProducerBaseStatisData producerStatisData) {
		this.producerStatisData = producerStatisData;
	}

	public ConsumerBaseStatisData getConsumerStatisData() {
		return consumerStatisData;
	}

	public void setConsumerStatisData(ConsumerBaseStatisData consumerStatisData) {
		this.consumerStatisData = consumerStatisData;
	}

}
