package com.dianping.swallow.web.model.stats;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;

@Service("statsDataFactory")
public class StatsDataFactory {

	@Autowired
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

	private void setComponent(StatsData statsData) {
		statsData.setEventReporter(eventReporter);
		statsData.setEventFactory(eventFactory);
	}

}
