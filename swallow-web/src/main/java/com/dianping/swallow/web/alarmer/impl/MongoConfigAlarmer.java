package com.dianping.swallow.web.alarmer.impl;

import java.io.IOException;
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
import com.dianping.swallow.common.message.JsonDeserializedException;
import com.dianping.swallow.web.alarmer.AlarmConfig;
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.MongoConfigEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.util.JsonUtil;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.MongoClientOptions;
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

	public static final String TOPIC_CFG_PREFIX = "swallow.topiccfg.";

	private static final String DEFAULT_TOPICCONFIG_NAME = "default";

	@Autowired
	private AlarmConfig alarmConfig;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		alarmInterval = 600;
		alarmDelay = 30;
		if (StringUtils.isNotBlank(alarmConfig.getServerMonitorUrl())) {
			serverMonitorUrl = alarmConfig.getServerMonitorUrl() + MONOGO_MONITOR_SIGN;
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
			return;
		}
		checkProducerConfig(topicConfigs);
		checkConsumerConfig(topicConfigs);
	}

	private void checkProducerConfig(Map<String, TopicConfig> topicConfigs) {
		List<ProducerServerResource> pServerResources = resourceContainer.findProducerServerResources(false);
		for (ProducerServerResource serverResource : pServerResources) {
			if (serverResource.isAlarm()) {
				Map<String, MongoStatus> mongoStatuses = getMongoStatus(serverResource.getIp());
				checkConfigByIp(mongoStatuses, topicConfigs, serverResource.getIp(), EventType.PRODUCER);
			}
		}
	}

	private void checkConsumerConfig(Map<String, TopicConfig> topicConfigs) {
		List<ConsumerServerResource> cServerResources = resourceContainer.findConsumerServerResources(false);
		for (ConsumerServerResource serverResource : cServerResources) {
			if (serverResource.isAlarm()) {
				Map<String, MongoStatus> mongoStatuses = getMongoStatus(serverResource.getIp());
				checkConfigByIp(mongoStatuses, topicConfigs, serverResource.getIp(), EventType.CONSUMER);
			}
		}
	}

	void checkConfigByIp(Map<String, MongoStatus> mongoStatuses, Map<String, TopicConfig> topicConfigs, String ip,
			EventType eventType) {
		if (mongoStatuses == null) {
			logger.error("[checkConfigByIp] mongourl mongoStatuses are both empty.");
			return;
		}
		List<MongoAddress> defaultAddresses = null;
		if (topicConfigs.containsKey(DEFAULT_TOPICCONFIG_NAME)) {
			TopicConfig defaultConfig = topicConfigs.get(DEFAULT_TOPICCONFIG_NAME);
			defaultAddresses = parseMongoUrl(defaultConfig.getMongoUrl());
		}

		for (Map.Entry<String, TopicConfig> configEntry : topicConfigs.entrySet()) {
			String topic = configEntry.getKey();
			TopicConfig topicConfig = configEntry.getValue();
			List<MongoAddress> mongoAddresses = null;
			List<ServerAddress> serverAddresses = null;
			if (!mongoStatuses.containsKey(topic)) {
				continue;
			}
			MongoStatus mongoStatus = mongoStatuses.get(topic);
			serverAddresses = mongoStatus.getServerAddressList();
			mongoAddresses = parseMongoUrl(topicConfig.getMongoUrl());
			if (mongoAddresses == null || mongoAddresses.isEmpty()) {
				mongoAddresses = defaultAddresses;
			}
			if ((serverAddresses == null || serverAddresses.isEmpty())
					&& (mongoAddresses == null || mongoAddresses.isEmpty())) {
				logger.error("[checkConfigByIp] topic {} mongoaddr are both empty.", topic);
				continue;
			} else if ((serverAddresses == null || serverAddresses.isEmpty()) && mongoAddresses != null
					&& !mongoAddresses.isEmpty()) {
				logger.error("[checkConfigByIp] topic {} mongourl mongoaddr is empty.", topic);
				continue;
			} else if (serverAddresses != null && !serverAddresses.isEmpty()
					&& (mongoAddresses == null || mongoAddresses.isEmpty())) {
				logger.error("[checkConfigByIp] topic {} lion mongoaddr both empty.", topic);
				continue;
			}
			for (MongoAddress mongoAddress : mongoAddresses) {
				boolean isAlarm = true;
				for (ServerAddress serverAddress : serverAddresses) {
					if (mongoAddress.equalServerAddress(serverAddress)) {
						isAlarm = false;
						break;
					}
				}
				if (isAlarm) {
					report(ip, topic, eventType);
				}
			}
		}
	}

	private void report(String ip, String topicName, EventType eventType) {
		MongoConfigEvent configEvent = eventFactory.createMongoConfigEvent();
		configEvent.setTopicName(topicName).setIp(ip).setSlaveIp(ip).setServerType(ServerType.MONGO_CONFIG)
				.setEventType(eventType).setCreateTime(new Date()).setCheckInterval(alarmInterval * 1000);
		eventReporter.report(configEvent);
	}

	private Map<String, MongoStatus> getMongoStatus(String ip) {
		String monitorUrl = StringUtils.replace(serverMonitorUrl, "{ip}", ip);
		Map<String, MongoStatus> mongoStatuses = null;
		HttpResult result = httpRequest(monitorUrl);

		if (result.isSuccess()) {
			mongoStatuses = MongoStatusSerializer.fromJson(result.getResponseBody(),
					new TypeReference<Map<String, MongoStatus>>() {
					});
		}
		return mongoStatuses;
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

	@Override
	public int getAlarmInterval() {
		return alarmInterval;
	}

	@Override
	public int getAlarmDelay() {
		return alarmDelay;
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

	public static class MongoStatusSerializer {

		public static ObjectMapper mapper = createObjectMapper();

		private static ObjectMapper createObjectMapper() {

			ObjectMapper mapper = new ObjectMapper();
			// 设置输出时包含属性的风格
			mapper.setSerializationInclusion(Include.NON_EMPTY);
			// 序列化时，忽略空的bean(即沒有任何Field)
			mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
			// 序列化时，忽略在JSON字符串中存在但Java对象实际没有的属性
			mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
			// make all member fields serializable without further annotations,
			// instead of just public fields (default setting).
			mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);

			SimpleModule module = new SimpleModule();
			module.addDeserializer(MongoClientOptions.class, new JsonDeserializer<MongoClientOptions>() {

				@Override
				public MongoClientOptions deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
						JsonProcessingException {

					jp.skipChildren();
					return MongoClientOptions.builder().build();
				}
			});
			mapper.registerModule(module);

			return mapper;

		}

		public static <T> T fromJson(String content, TypeReference<T> typeReference) {
			content = replaceHostField(content);
			content = replacePortField(content);
			try {
				return mapper.readValue(content, typeReference);
			} catch (IOException e) {
				throw new JsonDeserializedException("Deserialized json string error : " + content, e);
			}
		}

		public static String replaceHostField(String content) {
			if (!StringUtils.isBlank(content)) {
				content = StringUtils.replace(content, "\"host\"", "\"_host\"");
			}
			return content;
		}

		public static String replacePortField(String content) {
			if (!StringUtils.isBlank(content)) {
				content = StringUtils.replace(content, "\"port\"", "\"_port\"");
			}
			return content;
		}

	}
}
