package com.dianping.swallow.web.alarmer.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.SwallowConfig.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.LionUtilImpl;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoStatus;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.AlarmConfig;
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.MongoConfigEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.util.JsonUtil;
import com.mongodb.ServerAddress;

/**
 * 
 * @author qiyin
 *
 *         2015年9月21日 下午2:40:44
 */
@Component
public class MongoConfigAlarmer extends AbstractServiceAlarmer {

	private String serverMonitorUrl = "http://{ip}:8080/name/mongoManager";

	private static final String MONOGO_MONITOR_SIGN = "mongoManager";

	public static final String TOPIC_CFG_PREFIX = "swallow.topiccfg";

	@Autowired
	private AlarmConfig alarmConfig;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		alarmInterval = 600;
		alarmDelay = 30;
		if (StringUtils.isNotBlank(alarmConfig.getSlaveMonitorUrl())) {
			serverMonitorUrl = alarmConfig.getSlaveMonitorUrl() + MONOGO_MONITOR_SIGN;
		}
	}

	@Override
	public void doAlarm() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				checkConfig();
			}
		});
	}

	private void checkConfig() {
		Map<String, TopicConfig> topicConfigs = getMongoConfig();
		if (topicConfigs == null || topicConfigs.size() == 0) {
			logger.error("[checkConfig] lion mongoconfig is empty.");
		}
		checkProducerConfig(topicConfigs);
		checkConsumerConfig(topicConfigs);
	}

	private void checkProducerConfig(Map<String, TopicConfig> topicConfigs) {
		List<ProducerServerResource> pServerResources = resourceContainer.findProducerServerResources(false);
		for (ProducerServerResource serverResource : pServerResources) {
			if (serverResource.isAlarm()) {
				checkConfigByIp(serverResource.getIp(), EventType.PRODUCER, topicConfigs);
			}
		}
	}

	private void checkConsumerConfig(Map<String, TopicConfig> topicConfigs) {
		List<ConsumerServerResource> cServerResources = resourceContainer.findConsumerServerResources(false);
		for (ConsumerServerResource serverResource : cServerResources) {
			if (serverResource.isAlarm()) {
				checkConfigByIp(serverResource.getIp(), EventType.CONSUMER, topicConfigs);
			}
		}
	}

	private void checkConfigByIp(String ip, EventType eventType, Map<String, TopicConfig> topicConfigs) {
		Map<String, MongoStatus> mongoStatuses = getMongoStatus(ip);
		if (mongoStatuses == null) {
			logger.error("[checkConfigByIp] mongourl mongoStatuses are both empty.");
			return;
		}
		for (Map.Entry<String, TopicConfig> configEntry : topicConfigs.entrySet()) {
			String topic = configEntry.getKey();
			TopicConfig topicConfig = configEntry.getValue();
			if (mongoStatuses.containsKey(topic)) {
				MongoStatus mongoStatus = mongoStatuses.get(topic);
				List<ServerAddress> serverAddresses = mongoStatus.getServerAddressList();
				List<MongoAddress> mongoAddresses = parseMongoUrl(topicConfig.getMongoUrl());
				if ((serverAddresses == null || serverAddresses.size() == 0)
						&& (mongoAddresses == null || mongoAddresses.size() == 0)) {
					logger.error("[checkConfigByIp] topic {} mongoaddr are both empty.", topic);
					break;
				} else if ((serverAddresses == null || serverAddresses.size() == 0) && mongoAddresses != null
						&& mongoAddresses.size() > 0) {
					logger.error("[checkConfigByIp] topic {} mongourl mongoaddr is empty.", topic);
					break;
				} else if (serverAddresses != null && serverAddresses.size() > 0
						&& (mongoAddresses == null || mongoAddresses.size() == 0)) {
					logger.error("[checkConfigByIp] topic {} lion mongoaddr both empty.", topic);
					break;
				}
				for (MongoAddress mongoAddress : mongoAddresses) {
					for (ServerAddress serverAddress : serverAddresses) {
						if (mongoAddress.equalServerAddress(serverAddress)) {
							break;
						}
					}
					report(ip, topic, eventType);
				}
			} else {
				report(ip, topic, eventType);
			}
		}
	}

	private void report(String ip, String topicName, EventType eventType) {
		MongoConfigEvent configEvent = eventFactory.createMongoConfigEvent();
		configEvent.setTopicName(topicName).setIp(ip).setSlaveIp(ip).setServerType(ServerType.MONGO_CONFIG)
				.setEventType(eventType).setCreateTime(new Date());
		eventReporter.report(configEvent);
	}

	@SuppressWarnings("unchecked")
	private Map<String, MongoStatus> getMongoStatus(String ip) {
		String monitorUrl = StringUtils.replace(serverMonitorUrl, "{ip}", ip);
		Map<String, MongoStatus> mongoStatuses = null;
		HttpResult result = httpRequest(monitorUrl);
		if (result.isSuccess()) {
			mongoStatuses = JsonUtil.fromJson(result.getResponseBody(), Map.class);
		}
		return mongoStatuses;
	}

	private Map<String, TopicConfig> getMongoConfig() {
		Map<String, TopicConfig> topicConfigs = new HashMap<String, TopicConfig>();

		LionUtil lionUtil = new LionUtilImpl();
		Map<String, String> lionConfigs = lionUtil.getCfgs(TOPIC_CFG_PREFIX);
		for (Map.Entry<String, String> configEntry : lionConfigs.entrySet()) {
			String topic = configEntry.getKey();
			String value = configEntry.getValue();
			if (StringUtils.isBlank(value)) {
				continue;
			}
			TopicConfig config = JsonUtil.fromJson(value, TopicConfig.class);
			topicConfigs.put(topic, config);
		}
		return topicConfigs;
	}

	List<MongoAddress> parseMongoUrl(String mongoUrl) {
		List<MongoAddress> mongoAddrs = new ArrayList<MongoAddress>();
		final String mongoUrlStart = "mongodb://";
		final String urlSplit = ",";
		if (StringUtils.isNotBlank(mongoUrl)) {
			String tempArr[] = StringUtils.split(mongoUrl, urlSplit);
			if (tempArr != null) {
				for (String temp : tempArr) {
					MongoAddress mongoAddr = null;
					if (StringUtils.startsWith(temp, mongoUrlStart)) {
						String tempStart = temp.substring(mongoUrlStart.length());
						mongoAddr = splitMongoAddr(tempStart);
					} else {
						mongoAddr = splitMongoAddr(temp);
					}
					if (mongoAddr != null) {
						mongoAddrs.add(mongoAddr);
					}
				}
			}
		}
		return mongoAddrs;
	}

	private MongoAddress splitMongoAddr(String mongoAddr) {
		final String portSplit = ":";
		MongoAddress mongoAddress = null;
		if (StringUtils.isBlank(mongoAddr)) {
			return mongoAddress;
		}
		String addr[] = StringUtils.split(mongoAddr, portSplit);
		if (addr != null) {
			if (addr.length > 1) {
				mongoAddress = new MongoAddress(addr[0], Integer.parseInt(addr[1]));
			} else {
				mongoAddress = new MongoAddress(addr[0], 27017);
			}
		}
		return mongoAddress;
	}

	class MongoAddress {
		private String host;
		private int port;

		public String getHost() {
			return this.host;
		}

		public int getPort() {
			return this.port;
		}

		public MongoAddress(String host, int port) {
			this.host = host;
			this.port = port;
		}

		@Override
		public String toString() {
			return "MongoAddress [host=" + host + ", port=" + port + "]";
		}

		public boolean equalServerAddress(ServerAddress address) {
			if (address == null) {
				return false;
			}
			if (StringUtils.isBlank(address.getHost()) || StringUtils.isBlank(this.getHost())) {
				return false;
			}
			if (this.getHost().equals(address.getHost()) && this.getPort() == address.getPort()) {
				return true;
			}
			return false;
		}
	}
}
