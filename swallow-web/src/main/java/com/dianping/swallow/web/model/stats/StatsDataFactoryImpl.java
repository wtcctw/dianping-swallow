package com.dianping.swallow.web.model.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;

@Service("statsDataFactory")
public class StatsDataFactoryImpl implements StatsDataFactory {

	@Autowired
	@Qualifier("eventReporter")
	private EventReporter eventReporter;

	@Autowired
	private EventFactory eventFactory;

	public ProducerTopicStatsData createTopicStatsData() {
		ProducerTopicStatsData topicStatsData = new ProducerTopicStatsData();
		setComponent(topicStatsData);
		return topicStatsData;
	}

	public ProducerServerStatsData createProducerServerStatsData() {
		ProducerServerStatsData serverStatsData = new ProducerServerStatsData();
		setComponent(serverStatsData);
		return serverStatsData;
	}

	public ConsumerServerStatsData createConsumerServerStatsData() {
		ConsumerServerStatsData serverStatsData = new ConsumerServerStatsData();
		setComponent(serverStatsData);
		return serverStatsData;
	}

	public ConsumerIdStatsData createConsumerIdStatsData() {
		ConsumerIdStatsData consumerIdStatsData = new ConsumerIdStatsData();
		setComponent(consumerIdStatsData);
		return consumerIdStatsData;
	}

	public ConsumerTopicStatsData createConsumerTopicStatsData() {
		ConsumerTopicStatsData consumerTopicStatsData = new ConsumerTopicStatsData();
		setComponent(consumerTopicStatsData);
		return consumerTopicStatsData;
	}

	public ProducerIpStatsData createProducerIpStatsData() {
		ProducerIpStatsData producerIpStatsData = new ProducerIpStatsData();
		setComponent(producerIpStatsData);
		return producerIpStatsData;
	}

	public ConsumerIpStatsData createConsumerIpStatsData() {
		ConsumerIpStatsData consumerIpStatsData = new ConsumerIpStatsData();
		setComponent(consumerIpStatsData);
		return consumerIpStatsData;
	}

	private void setComponent(StatsData statsData) {
		statsData.setEventReporter(eventReporter);
		statsData.setEventFactory(eventFactory);
	}

}
