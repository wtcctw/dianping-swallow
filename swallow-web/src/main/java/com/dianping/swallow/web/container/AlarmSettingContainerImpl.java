package com.dianping.swallow.web.container;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.GlobalAlarmSetting;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:34:10
 */
// @Component
public class AlarmSettingContainerImpl implements AlarmSettingContainer, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AlarmSettingContainerImpl.class);

	private int interval = 120;// 秒

	private int delay = 5;

	@SuppressWarnings("unused")
	private ScheduledFuture<?> future = null;

	private static final String FACTORY_NAME = "AlarmSettingTask";

	private ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	@Autowired
	private ProducerServerAlarmSettingService pServerAlarmSettingService;

	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;

	@Autowired
	private ConsumerServerAlarmSettingService cServerAlarmSettingService;

	@Autowired
	private ConsumerIdAlarmSettingService consumerIdAlarmSettingService;

	private volatile GlobalAlarmSetting globalAlarmSetting;

	private Map<String, ProducerServerAlarmSetting> pServerAlarmSettings = new ConcurrentHashMap<String, ProducerServerAlarmSetting>();

	private Map<String, ConsumerServerAlarmSettingService> cServerAlarmSettings = new ConcurrentHashMap<String, ConsumerServerAlarmSettingService>();

	private Map<String, TopicAlarmSetting> topicAlarmSettings = new ConcurrentHashMap<String, TopicAlarmSetting>();

	private Map<String, ConsumerIdAlarmSetting> consumerIdAlarmSettings = new ConcurrentHashMap<String, ConsumerIdAlarmSetting>();

	public void initSettingData() {
		globalAlarmSetting = globalAlarmSettingService.findDefault();
	}

	private void initGlobalSettingData() {
		globalAlarmSetting = globalAlarmSettingService.findDefault();
	}

	private void initPServerSettingData() {

	}

	private void scheduleAlarmMetaTask() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					doLoadSettingDataTask();
				} catch (Throwable th) {
					logger.error("[scheduleAlarmMetaTask]", th);
				} finally {

				}
			}

		}, delay, interval, TimeUnit.SECONDS);
	}

	private void doLoadSettingDataTask() {
		logger.info("[doAlarmMetaTask] scheduled load setting data.");
	}

	@Override
	public void afterPropertiesSet() throws Exception {

	}
}
