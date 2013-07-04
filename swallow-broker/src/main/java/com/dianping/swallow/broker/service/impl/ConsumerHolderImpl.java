package com.dianping.swallow.broker.service.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.broker.conf.Constant;
import com.dianping.swallow.broker.monitor.NotifyService;
import com.dianping.swallow.broker.service.ConsumerHolder;
import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.consumer.ConsumerConfig;

@Service
public class ConsumerHolderImpl implements ConsumerHolder, ConfigChangeListener {

    private static final String         SWALLOW_BROKER_CONSUMER_PREFIX = "swallow.broker.consumer.";

    private static final Logger         LOG                            = LoggerFactory
                                                                               .getLogger(ConsumerHolderImpl.class);

    private Map<String, ConsumerBroker> consumerBrokerMap              = new ConcurrentHashMap<String, ConsumerBroker>();

    @Autowired
    private DynamicConfig               dynamicConfig;

    @Autowired
    private NotifyService               notifyService;

    @PostConstruct
    public void init() throws RemoteServiceInitFailedException {
        //获取<topic>配置项
        String topicStr = dynamicConfig.get(Constant.PROPERTY_TOPIC);
        //初始化所有topic对应的consumer
        init(topicStr);

        LOG.info("consumerBrokerMap is " + consumerBrokerMap);

    }

    private void init(String topicStr) {
        //获取<topic>配置项
        String[] topics = StringUtils.split(topicStr.trim(), ';');
        LOG.info("Initing consumers with topics(" + Arrays.toString(topics) + ")");

        //每个topic创建一个consumer
        if (topics != null) {
            for (String topic : topics) {
                initializeConsumerBroker(topic);
            }
        }
    }

    @Override
    public void start() {
        for (ConsumerBroker consumerBroker : consumerBrokerMap.values()) {
            if (!consumerBroker.isActive()) {
                consumerBroker.start();
                LOG.info("Started ConsumerBroker:" + consumerBroker);
            }
        }
    }

    @Override
    public void close() {
        for (ConsumerBroker consumerBroker : consumerBrokerMap.values()) {
            if (consumerBroker.isActive()) {
                consumerBroker.close();
                LOG.info("Closed ConsumerBroker:" + consumerBroker);
            }
        }
    }

    @Override
    public void onConfigChange(String key, String value) {
        if (StringUtils.equals(key, Constant.PROPERTY_TOPIC)) {
            try {
                init(value);
                start();
            } catch (RuntimeException e) {
                notifyService.alarm("Error when initialize ConsumerBrokers ", e, true);
            }
            LOG.info("consumerBrokerMap is " + consumerBrokerMap);
        }
    }

    private void initializeConsumerBroker(String topic) {
        //根据topic，获取swallow.broker.consumer.<topic>.*配置
        String consumerId = StringUtils.trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic
                + ".consumerId"));
        String url = StringUtils.trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic + ".url"));
        String key = topic + consumerId + url;

        //该配置不存在，则可以创建ConsumerWrap; 已经存在则不创建
        if (!consumerBrokerMap.containsKey(key)) {
            Integer threadPoolSize = NumberUtils.createInteger(StringUtils.trimToNull(dynamicConfig
                    .get(SWALLOW_BROKER_CONSUMER_PREFIX + topic + ".config.threadPoolSize")));
            Integer delayBaseOnBackoutMessageException = NumberUtils.createInteger(StringUtils.trimToNull(dynamicConfig
                    .get(SWALLOW_BROKER_CONSUMER_PREFIX + topic + ".config.delayBaseOnBackoutMessageException")));
            Integer delayUpperboundOnBackoutMessageException = NumberUtils.createInteger(StringUtils
                    .trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic
                            + ".config.delayUpperboundOnBackoutMessageException")));
            Integer retryCountOnBackoutMessageException = NumberUtils.createInteger(StringUtils
                    .trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic
                            + ".config.retryCountOnBackoutMessageException")));

            ConsumerConfig consumerConfig = new ConsumerConfig();
            consumerConfig.setDelayBaseOnBackoutMessageException(delayBaseOnBackoutMessageException);
            consumerConfig.setDelayUpperboundOnBackoutMessageException(delayUpperboundOnBackoutMessageException);
            consumerConfig.setRetryCountOnBackoutMessageException(retryCountOnBackoutMessageException);
            consumerConfig.setThreadPoolSize(threadPoolSize);

            ConsumerBroker consumerBroker = new ConsumerBroker(topic, consumerId, url, consumerConfig);
            consumerBroker.setNotifyService(notifyService);

            consumerBrokerMap.put(key, consumerBroker);
            LOG.info("Added ConsumerBroker:" + consumerBroker);
        }
    }

    @Override
    public Map<String, ConsumerBroker> getConsumerBrokerMap() {
        return consumerBrokerMap;
    }

}
