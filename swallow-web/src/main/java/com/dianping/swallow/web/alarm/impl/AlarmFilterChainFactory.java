package com.dianping.swallow.web.alarm.impl;

import javax.annotation.Resource;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.alarm.AlarmFilter;
import com.dianping.swallow.web.alarm.AlarmFilterChain;

/**
 *
 * @author qiyin
 *
 */

@Component
public class AlarmFilterChainFactory implements InitializingBean {

	@Resource(name = "producerServiceAlarmFilter")
	private AlarmFilter producerServiceAlarmFilter;

	@Resource(name = "producerSenderAlarmFilter")
	private AlarmFilter producerSenderAlarmFilter;

	@Resource(name = "producerServerStatisAlarmFilter")
	private AlarmFilter producerServerStatisAlarmFilter;

	@Resource(name = "producerTopicStatisAlarmFilter")
	private AlarmFilter producerTopicStatisAlarmFilter;

	@Resource(name = "consumerPortAlarmFilter")
	private AlarmFilter consumerPortAlarmFilter;

	@Resource(name = "consumerSenderAlarmFilter")
	private AlarmFilter consumerSenderAlarmFilter;

	@Resource(name = "consumerServerStatisAlarmFilter")
	private AlarmFilter consumerServerStatisAlarmFilter;

	@Resource(name = "consumerTopicStatisAlarmFilter")
	private AlarmFilter consumerTopicStatisAlarmFilter;

	@Resource(name = "consumerIdStatisAlarmFilter")
	private AlarmFilter consumerIdStatisAlarmFilter;

	@Resource(name = "consumerSlaveServiceAlarmFilter")
	private AlarmFilter consumerSlaveServiceAlarmFilter;

	public static AlarmFilterChainFactory chainFactoryInstance;

	public AlarmFilterChain createProducerServiceFilterChain() {
		AlarmFilterChain alarmFilterChain = new DefaultAlarmFilterChain();
		alarmFilterChain.setChainName("ProducerServiceFilterChain");
		alarmFilterChain.registerFilter(producerServiceAlarmFilter);
		alarmFilterChain.registerFilter(producerSenderAlarmFilter);
		return alarmFilterChain;
	}

	public AlarmFilterChain createProducerStatisFilterChain() {
		AlarmFilterChain alarmFilterChain = new DefaultAlarmFilterChain();
		alarmFilterChain.setChainName("ProducerStatisFilterChain");
		alarmFilterChain.registerFilter(producerServerStatisAlarmFilter);
		alarmFilterChain.registerFilter(producerTopicStatisAlarmFilter);
		return alarmFilterChain;
	}

	public AlarmFilterChain createConsumerServiceFilterChain() {
		AlarmFilterChain alarmFilterChain = new DefaultAlarmFilterChain();
		alarmFilterChain.setChainName("ConsumerServiceFilterChain");
		alarmFilterChain.registerFilter(consumerPortAlarmFilter);
		alarmFilterChain.registerFilter(consumerSenderAlarmFilter);
		alarmFilterChain.registerFilter(consumerSlaveServiceAlarmFilter);
		return alarmFilterChain;
	}

	public AlarmFilterChain createConsumerStatisFilterChain() {
		AlarmFilterChain alarmFilterChain = new DefaultAlarmFilterChain();
		alarmFilterChain.setChainName("ConsumerStatisFilterChain");
		alarmFilterChain.registerFilter(consumerServerStatisAlarmFilter);
		// alarmFilterChain.registerFilter(consumerTopicStatisAlarmFilter);
		alarmFilterChain.registerFilter(consumerIdStatisAlarmFilter);
		return alarmFilterChain;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		chainFactoryInstance = this;
	}

}
