package com.dianping.swallow.web.model.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.alarmer.container.AlarmMetaContainer;
import com.dianping.swallow.web.manager.IPResourceManager;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:13:05
 */
@Service("eventFactory")
public class EventFactory {

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPResourceManager ipDescManager;

	@Autowired
	private AlarmMetaContainer alarmMetaContainer;

	@Autowired
	protected IPCollectorService ipCollectorService;

	private void setComponent(Event event) {
		event.setAlarmService(alarmService);
		event.setIPDescManager(ipDescManager);
		event.setAlarmMetaContainer(alarmMetaContainer);
		event.setIPCollectorService(ipCollectorService);
	}

	public TopicEvent createTopicEvent() {
		TopicEvent topicEvent = new TopicEvent();
		setComponent(topicEvent);
		return topicEvent;
	}

	public ServerEvent createServerEvent() {
		ServerEvent serverEvent = new ServerEvent();
		setComponent(serverEvent);
		return serverEvent;
	}

	public ServerStatisEvent createServerStatisEvent() {
		ServerStatisEvent serverStatisEvent = new ServerStatisEvent();
		setComponent(serverStatisEvent);
		return serverStatisEvent;
	}

	public ConsumerIdEvent createConsumerIdEvent() {
		ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
		setComponent(consumerIdEvent);
		return consumerIdEvent;
	}

	public ConsumerClientEvent createCClientEvent() {
		ConsumerClientEvent cClientEvent = new ConsumerClientEvent();
		setComponent(cClientEvent);
		return cClientEvent;
	}

	public ProducerClientEvent createPClientEvent() {
		ProducerClientEvent pClientEvent = new ProducerClientEvent();
		setComponent(pClientEvent);
		return pClientEvent;
	}
	
	public MongoConfigEvent createMongoConfigEvent() {
		MongoConfigEvent configEvent = new MongoConfigEvent();
		setComponent(configEvent);
		return configEvent;
	}
}
