package com.dianping.swallow.web.model.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoStatus;
import com.dianping.swallow.common.message.JsonDeserializedException;
import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.MongoConfigEvent;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
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
 *         2015年8月31日 下午4:57:26
 */
public abstract class Server implements Sendable, Serviceable {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	private static final long SENDER_INTERVAL = 20 * 1000;

	protected EventType eventType;

	private boolean isSendLastAlarmed = false;

	private String serverMonitorUrl = "http://{ip}:8080/name/mongoManager";

	private static final String DEFAULT_TOPICCONFIG_NAME = "default";

	protected String ip;

	protected ServerConfig serverConfig;

	protected HttpService httpService;

	protected EventReporter eventReporter;

	protected EventFactory eventFactory;

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public void setServerConfig(ServerConfig serverConfig) {
		this.serverConfig = serverConfig;
	}

	public void setEventReporter(EventReporter eventReporter) {
		this.eventReporter = eventReporter;
	}

	public void setEventFactory(EventFactory eventFactory) {
		this.eventFactory = eventFactory;
	}

	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
	}

	public void initServer() {
		if (StringUtils.isNotBlank(serverConfig.getServerMonitorUrl())) {
			serverMonitorUrl = serverConfig.getServerMonitorUrl() + "mongoManager";
		}
	}

	@Override
	public String senderIp() {
		return ip;
	}

	@Override
	public void checkSender(long sendTimeStamp) {
		if (System.currentTimeMillis() - sendTimeStamp > SENDER_INTERVAL) {
			report(ip, ip, ServerType.SERVER_SENDER);
			isSendLastAlarmed = true;
		} else {
			if (isSendLastAlarmed) {
				report(ip, ip, ServerType.SERVER_SENDER_OK);
			}
			isSendLastAlarmed = false;
		}
	}

	protected HttpResult requestUrl(String url) {
		int count = 0;
		HttpResult result = null;
		do {
			if (count != 0) {
				threadSleep();
			}
			result = httpService.httpGet(url);
			count++;
		} while (!result.isSuccess() && count < 3);
		return result;
	}

	protected void threadSleep() {
		try {
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			logger.error("[threadSleep] interrupted.", e);
		}
	}

	protected void report(String masterIp, String slaveIp, ServerType serverType) {
		ServerEvent serverEvent = eventFactory.createServerEvent();
		report(serverEvent, masterIp, slaveIp, serverType);
	}

	private void report(ServerEvent serverEvent, String masterIp, String slaveIp, ServerType serverType) {
		serverEvent.setIp(masterIp).setSlaveIp(slaveIp).setServerType(serverType).setEventType(eventType)
				.setCreateTime(new Date());
		eventReporter.report(serverEvent);
	}

	@Override
	public String toString() {
		return "Server [eventType=" + eventType + ", isSendLastAlarmed=" + isSendLastAlarmed + ", ip=" + ip
				+ ", serverConfig=" + serverConfig + "] " + super.toString();
	}

	// mongoconfig check start
	public void checkConfig(Map<String, TopicConfig> topicConfigs, long checkInterval) {
		Map<String, MongoStatus> mongoStatuses = getMongoStatus(ip);
		checkConfigDetail(mongoStatuses, topicConfigs, checkInterval);
	}

	void checkConfigDetail(Map<String, MongoStatus> mongoStatuses, Map<String, TopicConfig> topicConfigs,
			long checkInterval) {
		if (mongoStatuses == null) {
			logger.error("[checkConfigByIp] mongourl mongoStatuses are both empty.");
			return;
		}
		List<MongoAddress> defaultAddresses = null;
		if (topicConfigs.containsKey(DEFAULT_TOPICCONFIG_NAME)) {
			TopicConfig defaultConfig = topicConfigs.get(DEFAULT_TOPICCONFIG_NAME);
			defaultAddresses = parseMongoUrl(defaultConfig.getStoreUrl());
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
			mongoAddresses = parseMongoUrl(topicConfig.getStoreUrl());
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
					MongoConfigEvent event = eventFactory.createMongoConfigEvent().setTopicName(topic);
					event.setCheckInterval(checkInterval);
					report(event, ip, ip, ServerType.MONGO_CONFIG);
				}
			}
		}
	}

	private Map<String, MongoStatus> getMongoStatus(String ip) {
		String monitorUrl = StringUtils.replace(serverMonitorUrl, "{ip}", ip);
		Map<String, MongoStatus> mongoStatuses = null;
		HttpResult result = requestUrl(monitorUrl);

		if (result.isSuccess()) {
			mongoStatuses = MongoStatusSerializer.fromJson(result.getResponseBody(),
					new TypeReference<Map<String, MongoStatus>>() {
					});
		}
		return mongoStatuses;
	}

	private List<MongoAddress> parseMongoUrl(String mongoUrl) {
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
	// mongoconfig check end
}
