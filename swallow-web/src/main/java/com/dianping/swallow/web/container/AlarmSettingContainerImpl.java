package com.dianping.swallow.web.container;

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
//@Component("alarmSettingContainer")
public class AlarmSettingContainerImpl implements AlarmSettingContainer, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AlarmSettingContainerImpl.class);

	private static final String DEFAULT_RECORD = "default";

	private static final String KEY_SPLIT = "&";

	private static final String DEFAULT_DEFAULT_RECORD = DEFAULT_RECORD + KEY_SPLIT + DEFAULT_RECORD;

	private int interval = 300;// 秒

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

	private volatile Map<String, GlobalAlarmSetting> globalAlarmSettings = null;

	private volatile Map<String, ProducerServerAlarmSetting> pServerAlarmSettings = null;

	private volatile Map<String, ConsumerServerAlarmSetting> cServerAlarmSettings = null;

	private volatile Map<String, TopicAlarmSetting> topicAlarmSettings = null;

	private volatile Map<String, ConsumerIdAlarmSetting> consumerIdAlarmSettings = null;

	public void findAlarmSettingData() {
		findGlobalSettingData();
		findPServerSettingData();
		findCServerSettingData();
		findTopicSettingData();
		findConsumerIdSettingData();
	}

	private void findGlobalSettingData() {
		long count = globalAlarmSettingService.count();
		List<GlobalAlarmSetting> globalAlarmSettingList = globalAlarmSettingService.findByPage(0, (int) count);
		Map<String, GlobalAlarmSetting> tempGlobalAlarmSettings = new HashMap<String, GlobalAlarmSetting>();
		if (globalAlarmSettingList != null) {
			for (GlobalAlarmSetting globalAlarmSetting : globalAlarmSettingList) {
				if (StringUtils.isNotBlank(globalAlarmSetting.getSwallowId())) {
					tempGlobalAlarmSettings.put(globalAlarmSetting.getSwallowId(), globalAlarmSetting);
				}
			}
		}
		this.globalAlarmSettings = tempGlobalAlarmSettings;

	}

	private void findPServerSettingData() {
		long count = pServerAlarmSettingService.count();
		List<ProducerServerAlarmSetting> pServerAlarmSettingList = pServerAlarmSettingService
				.findByPage(0, (int) count);
		Map<String, ProducerServerAlarmSetting> tempServerAlarmSettings = new HashMap<String, ProducerServerAlarmSetting>();
		if (pServerAlarmSettingList != null) {
			for (ProducerServerAlarmSetting pServerAlarmSetting : pServerAlarmSettingList) {
				if (StringUtils.isNotBlank(pServerAlarmSetting.getServerId())) {
					tempServerAlarmSettings.put(pServerAlarmSetting.getServerId(), pServerAlarmSetting);
				}
			}
		}
		pServerAlarmSettings = tempServerAlarmSettings;
	}

	private void findCServerSettingData() {
		long count = cServerAlarmSettingService.count();
		List<ConsumerServerAlarmSetting> cServerAlarmSettingList = cServerAlarmSettingService
				.findByPage(0, (int) count);
		Map<String, ConsumerServerAlarmSetting> tempServerAlarmSettings = new HashMap<String, ConsumerServerAlarmSetting>();
		if (cServerAlarmSettingList != null) {
			for (ConsumerServerAlarmSetting cServerAlarmSetting : cServerAlarmSettingList) {
				if (StringUtils.isNotBlank(cServerAlarmSetting.getServerId())) {
					tempServerAlarmSettings.put(cServerAlarmSetting.getServerId(), cServerAlarmSetting);
				}
			}
		}
		cServerAlarmSettings = tempServerAlarmSettings;
	}

	private void findTopicSettingData() {
		long count = topicAlarmSettingService.count();
		List<TopicAlarmSetting> topicAlarmSettingList = topicAlarmSettingService.findByPage(0, (int) count);
		Map<String, TopicAlarmSetting> tempTopicAlarmSettings = new HashMap<String, TopicAlarmSetting>();
		if (topicAlarmSettingList != null) {
			for (TopicAlarmSetting topicAlarmSetting : topicAlarmSettingList) {
				if (StringUtils.isNotBlank(topicAlarmSetting.getTopicName())) {
					tempTopicAlarmSettings.put(topicAlarmSetting.getTopicName(), topicAlarmSetting);
				}
			}
		}
		topicAlarmSettings = tempTopicAlarmSettings;
	}

	private void findConsumerIdSettingData() {
		long count = consumerIdAlarmSettingService.count();
		List<ConsumerIdAlarmSetting> consumerIdAlarmSettingList = consumerIdAlarmSettingService.findByPage(0,
				(int) count);
		Map<String, ConsumerIdAlarmSetting> tempConsumerIdAlarmSettings = new HashMap<String, ConsumerIdAlarmSetting>();
		if (consumerIdAlarmSettingList != null) {
			for (ConsumerIdAlarmSetting consumerIdAlarmSetting : consumerIdAlarmSettingList) {
				if (StringUtils.isNotBlank(consumerIdAlarmSetting.getTopicName())
						|| StringUtils.isNotBlank(consumerIdAlarmSetting.getConsumerId())) {
					String key = consumerIdAlarmSetting.getTopicName() + "KEY_SPLIT"
							+ consumerIdAlarmSetting.getConsumerId();
					tempConsumerIdAlarmSettings.put(key, consumerIdAlarmSetting);
				}
			}
		}
		consumerIdAlarmSettings = tempConsumerIdAlarmSettings;
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
		findAlarmSettingData();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduleAlarmMetaTask();
	}

	@Override
	public GlobalAlarmSetting findGlobalAlarmSetting(String globalId) {
		if (StringUtils.isBlank(globalId)) {
			return globalAlarmSettings.get(DEFAULT_RECORD);
		} else if (globalAlarmSettings.containsKey(globalId)) {
			return globalAlarmSettings.get(globalId);
		} else if (globalAlarmSettings.containsKey(DEFAULT_RECORD)) {
			return globalAlarmSettings.get(DEFAULT_RECORD);
		}
		return null;
	}

	@Override
	public ProducerServerAlarmSetting findProducerServerAlarmSetting(String serverId) {
		if (StringUtils.isBlank(serverId)) {
			return pServerAlarmSettings.get(DEFAULT_RECORD);
		} else if (pServerAlarmSettings.containsKey(serverId)) {
			return pServerAlarmSettings.get(serverId);
		} else if (pServerAlarmSettings.containsKey(DEFAULT_RECORD)) {
			return pServerAlarmSettings.get(DEFAULT_RECORD);
		}
		return null;
	}

	@Override
	public ConsumerServerAlarmSetting findConsumerServerAlarmSetting(String serverId) {
		if (StringUtils.isBlank(serverId)) {
			return cServerAlarmSettings.get(DEFAULT_RECORD);
		} else if (cServerAlarmSettings.containsKey(serverId)) {
			return cServerAlarmSettings.get(serverId);
		} else if (cServerAlarmSettings.containsKey(DEFAULT_RECORD)) {
			return cServerAlarmSettings.get(DEFAULT_RECORD);
		}
		return null;
	}

	@Override
	public TopicAlarmSetting findTopicAlarmSetting(String topicName) {
		if (StringUtils.isBlank(topicName)) {
			return topicAlarmSettings.get(DEFAULT_RECORD);
		} else if (topicAlarmSettings.containsKey(topicName)) {
			return topicAlarmSettings.get(topicName);
		} else if (topicAlarmSettings.containsKey(DEFAULT_RECORD)) {
			return topicAlarmSettings.get(DEFAULT_RECORD);
		}

		return null;
	}

	@Override
	public ConsumerIdAlarmSetting findConsumerIdAlarmSetting(String topicName, String consumerId) {
		if (StringUtils.isBlank(topicName) && StringUtils.isBlank(consumerId)) {
			return consumerIdAlarmSettings.get(DEFAULT_DEFAULT_RECORD);
		}
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
