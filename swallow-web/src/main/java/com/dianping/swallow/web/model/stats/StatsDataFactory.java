package com.dianping.swallow.web.model.stats;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.alarm.EventReporter;

@Service
public class StatsDataFactory implements InitializingBean {

	private static StatsDataFactory instance;

	@Autowired
	private EventReporter eventReporter;

	public static StatsDataFactory getInstance() {
		return instance;
	}

	public ProducerTopicStatsData createTopicStatsData() {
		ProducerTopicStatsData topicStatsData = new ProducerTopicStatsData();
		topicStatsData.setEventReporter(eventReporter);
		return topicStatsData;
	}

	public ProducerServerStatsData createProducerServerStatsData() {
		ProducerServerStatsData serverStatsData = new ProducerServerStatsData();
		serverStatsData.setEventReporter(eventReporter);
		return serverStatsData;
	}

	public ConsumerServerStatsData createConsumerServerStatsData() {
		ConsumerServerStatsData serverStatsData = new ConsumerServerStatsData();
		serverStatsData.setEventReporter(eventReporter);
		return serverStatsData;
	}

	public ConsumerIdStatsData createConsumerIdStatsData() {
		ConsumerIdStatsData consumerIdStatsData = new ConsumerIdStatsData();
		consumerIdStatsData.setEventReporter(eventReporter);
		return consumerIdStatsData;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}

}
