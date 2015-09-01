package com.dianping.swallow.web.alarmer.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerResourceService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:34:10
 */
@Component("alarmResourceContainer")
public class AlarmResourceContainerImpl implements AlarmResourceContainer, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AlarmResourceContainerImpl.class);

	private static final String DEFAULT_RECORD = "default";

	private static final String KEY_SPLIT = "&";

	private static final String DEFAULT_DEFAULT_RECORD = DEFAULT_RECORD + KEY_SPLIT + DEFAULT_RECORD;

	private int interval = 300;// 秒

	private int delay = 5;

	@SuppressWarnings("unused")
	private ScheduledFuture<?> future = null;

	private static final String FACTORY_NAME = "AlarmResourceTask";

	private ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));

	@Autowired
	private ConsumerServerResourceService cServerResourceService;

	@Autowired
	private ProducerServerResourceService pServerResourceService;

	@Autowired
	private TopicResourceService topicResourceService;

	@Autowired
	private ConsumerIdResourceService consumerIdResourceService;

	private volatile Map<String, ConsumerServerResource> cServerResources = null;

	private volatile Map<String, ProducerServerResource> pServerResources = null;

	private volatile Map<String, TopicResource> topicResources = null;

	private volatile Map<String, ConsumerIdResource> consumerIdResources = null;

	public void findResourceData() {
		findCServerResourceData();
		findPServerResourceData();
		findTopicResourceData();
		findConsumerIdResourceData();
	}

	private void findCServerResourceData() {
	}

	private void findPServerResourceData() {

	}

	private void findTopicResourceData() {
	}

	private void findConsumerIdResourceData() {
		List<ConsumerIdResource> tempResources = consumerIdResourceService.findAll();
		if (tempResources != null) {
			for (ConsumerIdResource tempResource : tempResources) {
				consumerIdResources.put(tempResource.generateKey(), tempResource);
			}
		}
	}

	private void scheduleAlarmMetaTask() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					doLoadResourceTask();
				} catch (Throwable th) {
					logger.error("[scheduleAlarmMetaTask]", th);
				} finally {

				}
			}

		}, delay, interval, TimeUnit.SECONDS);
	}

	private void doLoadResourceTask() {
		logger.info("[doLoadResourceTask] scheduled load setting data.");
		findResourceData();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduleAlarmMetaTask();
	}

	@Override
	public ConsumerServerResource findConsumerServerResource(String ip) {
		if(cServerResources.containsKey(ip)){
			cServerResources.get(ip);
		}else{
			cServerResources.get(DEFAULT_RECORD);
		}
		return null;
	}

	@Override
	public ProducerServerResource findProducerServerResource(String ip) {
		if(pServerResources.containsKey(ip)){
			return pServerResources.get(ip);
		}else{
			return pServerResources.get(DEFAULT_RECORD);
		}
	}

	@Override
	public TopicResource findTopicResource(String topic) {
		if (topicResources.containsKey(topic)) {
			return topicResources.get(topic);
		} else {
			return topicResources.get(DEFAULT_RECORD);
		}
	}

	@Override
	public ConsumerIdResource findConsumerIdResource(String topicName, String consumerId) {
		String key = topicName + KEY_SPLIT + consumerId;
		if (consumerIdResources.containsKey(key)) {
			return consumerIdResources.get(key);
		} else {
			return consumerIdResources.get(DEFAULT_DEFAULT_RECORD);
		}
	}
}
