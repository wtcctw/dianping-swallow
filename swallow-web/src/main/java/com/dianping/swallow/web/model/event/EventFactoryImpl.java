package com.dianping.swallow.web.model.event;

import com.dianping.swallow.web.alarmer.container.AlarmMetaContainer;
import com.dianping.swallow.web.manager.AlarmReceiverManager;
import com.dianping.swallow.web.monitor.jmx.event.BrokerKafkaEvent;
import com.dianping.swallow.web.monitor.jmx.event.ControllerElectionEvent;
import com.dianping.swallow.web.monitor.jmx.event.ControllerKafkaEvent;
import com.dianping.swallow.web.monitor.jmx.event.UnderReplicaEvent;
import com.dianping.swallow.web.monitor.zookeeper.event.TopicCuratorEvent;
import com.dianping.swallow.web.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:13:05
 */
@Service("eventFactory")
public class EventFactoryImpl implements EventFactory {

	@Autowired
	private EventConfig eventConfig;

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private AlarmReceiverManager receiverManager;

	@Autowired
	private AlarmMetaContainer alarmMetaContainer;

	private void setComponent(Event event) {
		event.setEventConfig(eventConfig);
		event.setAlarmService(alarmService);
		event.setAlarmReceiverManager(receiverManager);
		event.setAlarmMetaContainer(alarmMetaContainer);
	}

	@Override
	public TopicEvent createTopicEvent() {
		TopicEvent topicEvent = new TopicEvent();
		setComponent(topicEvent);
		return topicEvent;
	}

	@Override
	public ServerEvent createServerEvent() {
		ServerEvent serverEvent = new ServerEvent();
		setComponent(serverEvent);
		return serverEvent;
	}

	@Override
	public ServerStatisEvent createServerStatisEvent() {
		ServerStatisEvent serverStatisEvent = new ServerStatisEvent();
		setComponent(serverStatisEvent);
		return serverStatisEvent;
	}

	@Override
	public ConsumerIdEvent createConsumerIdEvent() {
		ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
		setComponent(consumerIdEvent);
		return consumerIdEvent;
	}

	@Override
	public ConsumerClientEvent createCClientEvent() {
		ConsumerClientEvent cClientEvent = new ConsumerClientEvent();
		setComponent(cClientEvent);
		return cClientEvent;
	}

	@Override
	public ProducerClientEvent createPClientEvent() {
		ProducerClientEvent pClientEvent = new ProducerClientEvent();
		setComponent(pClientEvent);
		return pClientEvent;
	}

	@Override
	public MongoConfigEvent createMongoConfigEvent() {
		MongoConfigEvent configEvent = new MongoConfigEvent();
		setComponent(configEvent);
		return configEvent;
	}

	@Override
	public BrokerKafkaEvent createBrokerKafkaEvent() {
		BrokerKafkaEvent kafkaEvent = new BrokerKafkaEvent();
		setComponent(kafkaEvent);
		return kafkaEvent;
	}

	@Override
	public ControllerKafkaEvent createControllerKafkaEvent() {
		ControllerKafkaEvent controllerKafkaEvent = new ControllerKafkaEvent();
		setComponent(controllerKafkaEvent);
		return controllerKafkaEvent;
	}

	@Override
	public ControllerElectionEvent createControllerElectionEvent() {
		ControllerElectionEvent controllerElectionEvent = new ControllerElectionEvent();
		setComponent(controllerElectionEvent);
		return controllerElectionEvent;
	}

	@Override
	public UnderReplicaEvent createUnderReplicaEvent() {
		UnderReplicaEvent underReplicaEvent = new UnderReplicaEvent();
		setComponent(underReplicaEvent);
		return underReplicaEvent;
	}

	@Override
	public TopicCuratorEvent createTopicCuratorEvent() {
		TopicCuratorEvent topicCuratorEvent = new TopicCuratorEvent();
		setComponent(topicCuratorEvent);
		return topicCuratorEvent;
	}
}
