package com.dianping.swallow.web.model.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.web.util.JsonUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * @author qiyin
 *         <p/>
 *         2015年8月31日 下午4:57:26
 */
public abstract class Server implements Sendable, Serviceable {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final long SENDER_INTERVAL = 20 * 1000;

    protected EventType eventType;

    private boolean isSendLastAlarmed = false;

    private String serverMonitorUrl = "http://{ip}:8080/name/&messageDAO";

    private static final String DEFAULT_TOPICCONFIG_NAME = "default";

    private static final String KAFKA_STORE_PREFIX = "kafka://";

    private static final String MONGO_STORE_PREFIX = "mongodb://";

    private static final String IP_REGEX = "(\\d{1,3}\\.){3}\\d{1,3}(:\\d{1,})?";

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
            serverMonitorUrl = serverConfig.getServerMonitorUrl() + "&messageDAO";
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
        Map<String, String> messageDaoInfos = getMessageDaoInfo(ip);
        checkConfigDetail(messageDaoInfos, topicConfigs, checkInterval);
    }

    void checkConfigDetail(Map<String, String> messageDaoInfos, Map<String, TopicConfig> topicConfigs,
                           long checkInterval) {
        if (messageDaoInfos == null) {
            logger.error("[checkConfigByIp] mongourl mongoStatuses are both empty.");
            return;
        }
        List<StoreAddress> defaultAddresses = null;
        if (topicConfigs.containsKey(DEFAULT_TOPICCONFIG_NAME)) {
            TopicConfig defaultConfig = topicConfigs.get(DEFAULT_TOPICCONFIG_NAME);
            defaultAddresses = parseStoreUrl(defaultConfig.getStoreUrl());
        }

        for (Map.Entry<String, TopicConfig> configEntry : topicConfigs.entrySet()) {
            String topic = configEntry.getKey();
            TopicConfig topicConfig = configEntry.getValue();
            List<StoreAddress> storeAddresses = null;
            List<StoreAddress> serverAddresses = null;
            if (!messageDaoInfos.containsKey(topic)) {
                continue;
            }
            String messageDaoInfo = messageDaoInfos.get(topic);
            serverAddresses = parseMessageDaoInfo(messageDaoInfo);

            storeAddresses = parseStoreUrl(topicConfig.getStoreUrl());
            if (storeAddresses == null || storeAddresses.isEmpty()) {
                storeAddresses = defaultAddresses;
            }
            if ((serverAddresses == null || serverAddresses.isEmpty())
                    && (storeAddresses == null || storeAddresses.isEmpty())) {
                logger.error("[checkConfigByIp] topic {} mongoaddr are both empty.", topic);
                continue;
            } else if ((serverAddresses == null || serverAddresses.isEmpty()) && storeAddresses != null
                    && !storeAddresses.isEmpty()) {
                logger.error("[checkConfigByIp] topic {} mongourl mongoaddr is empty.", topic);
                continue;
            } else if (serverAddresses != null && !serverAddresses.isEmpty()
                    && (storeAddresses == null || storeAddresses.isEmpty())) {
                logger.error("[checkConfigByIp] topic {} lion mongoaddr both empty.", topic);
                continue;
            }
            for (StoreAddress lionAddress : storeAddresses) {
                boolean isAlarm = true;
                for (StoreAddress serverAddress : serverAddresses) {
                    if (lionAddress.equalStoreAddress(serverAddress)) {
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

    private Map<String, String> getMessageDaoInfo(String ip) {
        String monitorUrl = StringUtils.replace(serverMonitorUrl, "{ip}", ip);
        Map<String, String> messageDaoInfos = null;
        HttpResult result = requestUrl(monitorUrl);

        if (result.isSuccess()) {
            messageDaoInfos = JsonUtil.fromJson(result.getResponseBody(), Map.class);
        }
        return messageDaoInfos;
    }

    public List<StoreAddress> parseMessageDaoInfo(String messageDaoInfo) {
        List<StoreAddress> storeAddresses = null;
        if (StringUtils.isNotEmpty(messageDaoInfo)) {
            int index = messageDaoInfo.lastIndexOf(MONGO_STORE_PREFIX);
            if (index < 0) {
                index = messageDaoInfo.lastIndexOf(KAFKA_STORE_PREFIX);
            }
            if (index > 0) {
                String storeUrl = StringUtils.substring(messageDaoInfo, index);
                storeAddresses = parseStoreUrl(storeUrl);
            }
        }
        return storeAddresses;
    }


    public List<StoreAddress> parseStoreUrl(String mongoUrl) {
        List<StoreAddress> storeAddresses = new ArrayList<StoreAddress>();
        final String urlSplit = ",";
        if (StringUtils.isNotBlank(mongoUrl)) {
            String tempArr[] = StringUtils.split(mongoUrl, urlSplit);
            if (tempArr != null) {
                for (String temp : tempArr) {
                    StoreAddress storeAddress = null;
                    if (StringUtils.startsWith(temp, MONGO_STORE_PREFIX)) {
                        String tempStart = temp.substring(MONGO_STORE_PREFIX.length());
                        storeAddress = splitStoreAddress(tempStart);
                    } else if (StringUtils.startsWith(temp, KAFKA_STORE_PREFIX)) {
                        String tempStart = temp.substring(KAFKA_STORE_PREFIX.length());
                        storeAddress = splitStoreAddress(tempStart);
                    } else {
                        if (temp.matches(IP_REGEX)) {
                            storeAddress = splitStoreAddress(temp);
                        }
                    }
                    if (storeAddress != null) {
                        storeAddresses.add(storeAddress);
                    }
                }
            }
        }
        return storeAddresses;
    }

    private StoreAddress splitStoreAddress(String mongoAddr) {
        final String portSplit = ":";
        StoreAddress storeAddress = null;
        if (StringUtils.isBlank(mongoAddr)) {
            return storeAddress;
        }
        String addr[] = StringUtils.split(mongoAddr, portSplit);
        if (addr != null) {
            if (addr.length > 1) {
                storeAddress = new StoreAddress(addr[0], Integer.parseInt(addr[1]));
            } else {
                storeAddress = new StoreAddress(addr[0], 27017);
            }
        }
        return storeAddress;
    }

    class StoreAddress {

        private String host;

        private int port;

        public String getHost() {
            return this.host;
        }

        public int getPort() {
            return this.port;
        }

        public StoreAddress(String host, int port) {
            this.host = host;
            this.port = port;
        }

        @Override
        public String toString() {
            return "MongoAddress [host=" + host + ", port=" + port + "]";
        }

        public boolean equalStoreAddress(StoreAddress address) {
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
