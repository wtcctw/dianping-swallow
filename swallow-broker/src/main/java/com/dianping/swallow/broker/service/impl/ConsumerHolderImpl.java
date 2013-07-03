package com.dianping.swallow.broker.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.broker.monitor.NotifyService;
import com.dianping.swallow.broker.service.ConsumerHolder;
import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;

@Service
public class ConsumerHolderImpl implements ConsumerHolder, ConfigChangeListener {
    private static final String         SWALLOW_BROKER_CONSUMER_RECEIVERS = "swallow.broker.consumerReceivers";

    private static final Logger         LOG                               = LoggerFactory
                                                                                  .getLogger(ConsumerHolderImpl.class);

    private static final int            LENGTH                            = 3;                                              //表示<topic>,<cid>,<url>是3个元素

    private Map<String, ConsumerBroker> consumerBrokerMap                 = new ConcurrentHashMap<String, ConsumerBroker>();

    @Autowired
    private DynamicConfig               dynamicConfig;

    @Autowired
    private NotifyService               notifyService;

    @PostConstruct
    public void init() throws RemoteServiceInitFailedException {
        //获取<topic>,<cid>,<url>;<topic>,<cid>,<url>;配置项
        String config = dynamicConfig.get(SWALLOW_BROKER_CONSUMER_RECEIVERS);

        //初始化
        init(config);
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

    static class ConsumerBrokerConfig {

        String url;
        String consumerId;
        String topic;

        public ConsumerBrokerConfig(String topic, String consumerId, String url) {
            super();
            this.url = url;
            this.consumerId = consumerId;
            this.topic = topic;
        }
    }

    @Override
    public void onConfigChange(String key, String value) {
        if (StringUtils.equals(key, SWALLOW_BROKER_CONSUMER_RECEIVERS)) {
            try {
                init(value);
                start();
            } catch (RuntimeException e) {
                notifyService.alarm("Error when initialize ConsumerBrokers ", e, true);
            }
        }
    }

    private void init(String configStr) {
        //解析配置
        List<ConsumerBrokerConfig> configs = parseConfig(configStr);
        LOG.info("initing ConsumerBrokers with config:" + configs);

        //每个配置项创建一个ConsumerWrap,然后启动BrokerConsumer（BrokerConsumer负责启动所有consumer,接受消息，并发给url）
        if (configs != null) {
            for (ConsumerBrokerConfig config : configs) {
                initializeConsumerBroker(config);
            }
        }
        LOG.info("ConsumerBrokers inited");
    }

    private void initializeConsumerBroker(ConsumerBrokerConfig config) {
        ConsumerBroker consumerBroker = new ConsumerBroker(config.topic, config.consumerId, config.url);
        consumerBroker.setNotifyService(notifyService);

        String key = config.topic + config.consumerId + config.url;
        if (!consumerBrokerMap.containsKey(key)) {
            consumerBrokerMap.put(key, consumerBroker);
            LOG.info("Added ConsumerBroker:" + consumerBroker);
        }
    }

    private List<ConsumerBrokerConfig> parseConfig(String configStr) {
        String[] configStrSplit = StringUtils.split(configStr, ';');
        List<ConsumerBrokerConfig> configs = new ArrayList<ConsumerBrokerConfig>();
        if (configStrSplit != null) {
            for (String receiverStr : configStrSplit) {
                String[] receiverStrSplit = StringUtils.split(receiverStr, ',');
                if (receiverStrSplit != null && receiverStrSplit.length == LENGTH) {
                    ConsumerBrokerConfig config = new ConsumerBrokerConfig(receiverStrSplit[0], receiverStrSplit[1],
                            receiverStrSplit[2]);
                    configs.add(config);
                }
            }
        }
        return configs;
    }

    @Override
    public Map<String, ConsumerBroker> getConsumerBrokerMap() {
        return consumerBrokerMap;
    }

}
