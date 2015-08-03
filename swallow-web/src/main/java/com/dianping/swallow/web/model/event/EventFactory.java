package com.dianping.swallow.web.model.event;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.SeqGeneratorService;

@Service
public class EventFactory implements InitializingBean {

	private static EventFactory instance;

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPDescManager ipDescManager;

	@Autowired
	protected IPCollectorService ipCollectorService;

	@Autowired
	private SeqGeneratorService seqGeneratorService;

	public static EventFactory getInstance() {
		return instance;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}

	private void setComponent(Event event) {
		event.setIPCollectorService(ipCollectorService);
		event.setIPDescManager(ipDescManager);
		event.setAlarmService(alarmService);
		event.setSeqGeneratorService(seqGeneratorService);
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
}
