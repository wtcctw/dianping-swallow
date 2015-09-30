package com.dianping.swallow.web.model.event;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.alarmer.container.AlarmMetaContainer;
import com.dianping.swallow.web.manager.AlarmReceiverManager;
import com.dianping.swallow.web.service.AlarmService;

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
	private AlarmReceiverManager receiverManager;

	@Autowired
	private AlarmMetaContainer alarmMetaContainer;

	private void setComponent(Event event) {
		event.setAlarmService(alarmService);
		event.setAlarmReceiverManager(receiverManager);
		event.setAlarmMetaContainer(alarmMetaContainer);
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
