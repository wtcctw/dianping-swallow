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

import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
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

	private static final String DEFAULT_RECORD = "default";

	private static final String KEY_SPLIT = "&";

	private static final String DEFAULT_DEFAULT_RECORD = DEFAULT_RECORD + KEY_SPLIT + DEFAULT_RECORD;

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

	private Map<String, GlobalAlarmSetting> globalAlarmSettings = new ConcurrentHashMap<String, GlobalAlarmSetting>();

	private Map<String, ProducerServerAlarmSetting> pServerAlarmSettings = new ConcurrentHashMap<String, ProducerServerAlarmSetting>();

	private Map<String, ConsumerServerAlarmSetting> cServerAlarmSettings = new ConcurrentHashMap<String, ConsumerServerAlarmSetting>();

	private Map<String, TopicAlarmSetting> topicAlarmSettings = new ConcurrentHashMap<String, TopicAlarmSetting>();

	private Map<String, ConsumerIdAlarmSetting> consumerIdAlarmSettings = new ConcurrentHashMap<String, ConsumerIdAlarmSetting>();

	public void findSettingData() {
		
	}

	private void findGlobalSettingData() {
	}

	private void findPServerSettingData() {

	}
	
	private void findCServerSettingData(){
		
	}
	
	private void findTopicSettingData(){
		
	}
	
	private void findConsumerIdSettingData(){
		
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
		findSettingData();
	}

	@Override
	public GlobalAlarmSetting findGlobalAlarmSetting(String globalId) {
		if (globalAlarmSettings.containsKey(globalId)) {
			return globalAlarmSettings.get(globalId);
		}
		if (globalAlarmSettings.containsKey(DEFAULT_RECORD)) {
			return globalAlarmSettings.get(DEFAULT_RECORD);
		}
		return null;
	}

	@Override
	public ProducerServerAlarmSetting findProducerServerAlarmSetting(String serverId) {
		if (pServerAlarmSettings.containsKey(serverId)) {
			return pServerAlarmSettings.get(serverId);
		}
		if (pServerAlarmSettings.containsKey(DEFAULT_RECORD)) {
			return pServerAlarmSettings.get(DEFAULT_RECORD);
		}
		return null;
	}

	@Override
	public ConsumerServerAlarmSetting findConsumerServerAlarmSetting(String serverId) {
		if (cServerAlarmSettings.containsKey(serverId)) {
			return cServerAlarmSettings.get(serverId);
		} else {
			if (cServerAlarmSettings.containsKey(DEFAULT_RECORD)) {
				return cServerAlarmSettings.get(DEFAULT_RECORD);
			}
		}
		return null;
	}

	@Override
	public TopicAlarmSetting findTopicAlarmSetting(String topicName) {
		if (topicAlarmSettings.containsKey(topicName)) {
			return topicAlarmSettings.get(topicName);
		} else {
			if (topicAlarmSettings.containsKey(DEFAULT_RECORD)) {
				return topicAlarmSettings.get(DEFAULT_RECORD);
			}
		}
		return null;
	}

	@Override
	public ConsumerIdAlarmSetting findConsumerIdAlarmSetting(String topicName, String consumerId) {
		String key = topicName + "KEY_SPLIT" + consumerId;
		if (consumerIdAlarmSettings.containsKey(key)) {
			return consumerIdAlarmSettings.get(key);
		} else {
			String defaultkKey = topicName + KEY_SPLIT + DEFAULT_RECORD;
			if (consumerIdAlarmSettings.containsKey(defaultkKey)) {
				return consumerIdAlarmSettings.get(defaultkKey);
			} else if (consumerIdAlarmSettings.containsKey(DEFAULT_DEFAULT_RECORD)) {
				return consumerIdAlarmSettings.get(DEFAULT_DEFAULT_RECORD);
			}
		}
		return null;
	}
}
