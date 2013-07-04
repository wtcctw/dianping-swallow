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
        String consumerIdsStr = StringUtils.trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic
                + ".consumerId"));
        String[] splits = StringUtils.split(consumerIdsStr, ';');
        if (splits != null) {
            for (String split : splits) {
                String[] consumerIdAndNum = StringUtils.split(split, ',');
                String consumerId = consumerIdAndNum[0];
                String num = consumerIdAndNum[1];

                String key = topic + consumerId + num;

                //该配置不存在，则可以创建ConsumerWrap; 已经存在则不创建
                if (!consumerBrokerMap.containsKey(key)) {
                    String url = StringUtils.trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic + "."
                            + consumerId + "." + num + ".url"));
                    Integer threadPoolSize = NumberUtils.createInteger(StringUtils.trimToNull(dynamicConfig
                            .get(SWALLOW_BROKER_CONSUMER_PREFIX + topic + "." + consumerId + "." + num
                                    + ".threadPoolSize")));
                    Integer delayBaseOnBackoutMessageException = NumberUtils.createInteger(StringUtils
                            .trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic + "." + consumerId
                                    + "." + num + ".delayBaseOnBackoutMessageException")));
                    Integer delayUpperboundOnBackoutMessageException = NumberUtils.createInteger(StringUtils
                            .trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic + "." + consumerId
                                    + "." + num + ".delayUpperboundOnBackoutMessageException")));
                    Integer retryCountOnBackoutMessageException = NumberUtils.createInteger(StringUtils
                            .trimToNull(dynamicConfig.get(SWALLOW_BROKER_CONSUMER_PREFIX + topic + "." + consumerId
                                    + "." + num + ".retryCountOnBackoutMessageException")));

                    ConsumerConfig consumerConfig = new ConsumerConfig();
                    if (delayBaseOnBackoutMessageException != null) {
                        consumerConfig.setDelayBaseOnBackoutMessageException(delayBaseOnBackoutMessageException);
                    }
                    if (delayUpperboundOnBackoutMessageException != null) {
                        consumerConfig
                                .setDelayUpperboundOnBackoutMessageException(delayUpperboundOnBackoutMessageException);
                    }
                    if (retryCountOnBackoutMessageException != null) {
                        consumerConfig.setRetryCountOnBackoutMessageException(retryCountOnBackoutMessageException);
                    }
                    if (threadPoolSize != null) {
                        consumerConfig.setThreadPoolSize(threadPoolSize);
                    }

                    ConsumerBroker consumerBroker = new ConsumerBroker(topic, consumerId, url, consumerConfig);
                    consumerBroker.setNotifyService(notifyService);

                    consumerBrokerMap.put(key, consumerBroker);
                    LOG.info("Added ConsumerBroker:" + consumerBroker);
                }
            }
        }
    }

    @Override
    public Map<String, ConsumerBroker> getConsumerBrokerMap() {
        return consumerBrokerMap;
    }

}
