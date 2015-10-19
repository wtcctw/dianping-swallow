package com.dianping.swallow.web.alarmer.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.SwallowConfig.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.LionUtilImpl;
import com.dianping.swallow.web.alarmer.AlarmConfig;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.util.JsonUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年9月21日 下午2:40:44
 */
@Component
public class MongoConfigAlarmer extends AbstractServiceAlarmer {

	public static final String TOPIC_CFG_PREFIX = "swallow.topiccfg.";

	@Autowired
	private AlarmConfig alarmConfig;

	@Autowired
	private ResourceContainer resourceContainer;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		alarmInterval = 600;
		alarmDelay = 30;
	}

	@Override
	public void doAlarm() {
		checkConfig();
	}

	private void checkConfig() {
		Map<String, TopicConfig> topicConfigs = getMongoConfig();
		if (topicConfigs == null || topicConfigs.size() == 0) {
			logger.error("[checkConfig] lion mongoconfig is empty.");
			return;
		}
		checkProducerConfig(topicConfigs);
		checkConsumerConfig(topicConfigs);
	}

	private void checkProducerConfig(Map<String, TopicConfig> topicConfigs) {
		List<ProducerServerResource> pServerResources = resourceContainer.findProducerServerResources(false);
		for (ProducerServerResource serverResource : pServerResources) {
			if (serverResource.isAlarm()) {
			}
		}
	}

	private void checkConsumerConfig(Map<String, TopicConfig> topicConfigs) {
		List<ConsumerServerResource> cServerResources = resourceContainer.findConsumerServerResources(false);
		for (ConsumerServerResource serverResource : cServerResources) {
			if (serverResource.isAlarm()) {
			}
		}
	}

	private Map<String, TopicConfig> getMongoConfig() {
		Map<String, TopicConfig> topicConfigs = new HashMap<String, TopicConfig>();

		LionUtil lionUtil = new LionUtilImpl();
		Map<String, String> lionConfigs = lionUtil.getCfgs(TOPIC_CFG_PREFIX);
		for (Map.Entry<String, String> configEntry : lionConfigs.entrySet()) {
			String topicKey = configEntry.getKey();
			if (!StringUtils.isBlank(topicKey) && topicKey.startsWith(TOPIC_CFG_PREFIX)) {
				String topic = StringUtils.substring(topicKey, TOPIC_CFG_PREFIX.length());
				String value = configEntry.getValue();
				if (StringUtils.isBlank(value)) {
					continue;
				}
				TopicConfig config = JsonUtil.fromJson(value, TopicConfig.class);
				topicConfigs.put(topic, config);
			}
		}
		return topicConfigs;
	}

	
}
