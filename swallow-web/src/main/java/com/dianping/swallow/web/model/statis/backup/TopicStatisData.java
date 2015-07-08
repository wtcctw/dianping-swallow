package com.dianping.swallow.web.model.statis.backup;

public class TopicStatisData {
	
	private String id;
	
	private long timeKey;

	private String topicName;

	private ProducerClientStatisData producerStatisData;
	
	private ConsumerClientStatisData consumerStatisData;

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

	public ProducerClientStatisData getProducerStatisData() {
		return producerStatisData;
	}

	public void setProducerStatisData(ProducerClientStatisData producerStatisData) {
		this.producerStatisData = producerStatisData;
	}

	public ConsumerClientStatisData getConsumerStatisData() {
		return consumerStatisData;
	}

	public void setConsumerStatisData(ConsumerClientStatisData consumerStatisData) {
		this.consumerStatisData = consumerStatisData;
	}
	

}
