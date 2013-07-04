package com.dianping.swallow.broker.service.impl;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.broker.conf.Constant;
import com.dianping.swallow.broker.monitor.NotifyService;
import com.dianping.swallow.broker.service.ProducerHolder;
import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.producer.exceptions.RemoteServiceInitFailedException;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerMode;
import com.dianping.swallow.producer.impl.ProducerFactoryImpl;

@Service
public class ProducerHolderImpl implements ProducerHolder, ConfigChangeListener {
    private static final Logger   LOG                            = LoggerFactory.getLogger(ProducerHolderImpl.class);

    private static final String   SWALLOW_BROKER_PRODUCER_PREFIX = "swallow.broker.consumer.";

    private Map<String, Producer> producerMap                    = new ConcurrentHashMap<String, Producer>();

    @Autowired
    private DynamicConfig         dynamicConfig;

    @Autowired
    private NotifyService         notifyService;

    @PostConstruct
    public void init() throws RemoteServiceInitFailedException {
        //<topic>,<topic>
        String config = dynamicConfig.get(Constant.PROPERTY_TOPIC);

        //初始化
        init(config);

        LOG.info("All producer's topic:" + producerMap.keySet());

        //监听lion
        dynamicConfig.addConfigChangeListener(this);

    }

    private void initializeProducer(String topic) throws RemoteServiceInitFailedException {
        //该配置不存在，则可以创建ConsumerWrap; 已经存在则不创建
        if (!producerMap.containsKey(topic)) {
            //根据topic，获取swallow.broker.producer.<topic>.*配置
            Integer retryTimes = NumberUtils.createInteger(StringUtils.trimToNull(dynamicConfig
                    .get(SWALLOW_BROKER_PRODUCER_PREFIX + topic + ".config.retryTimes")));
            String mode = StringUtils.trimToNull(dynamicConfig.get(SWALLOW_BROKER_PRODUCER_PREFIX + topic
                    + ".config.mode"));
            Boolean zipped = BooleanUtils.toBoolean(StringUtils.trimToNull(dynamicConfig
                    .get(SWALLOW_BROKER_PRODUCER_PREFIX + topic + ".config.zipped")));
            Integer threadPoolSize = NumberUtils.createInteger(StringUtils.trimToNull(dynamicConfig
                    .get(SWALLOW_BROKER_PRODUCER_PREFIX + topic + ".config.threadPoolSize")));

            ProducerConfig config = new ProducerConfig();
            if (retryTimes != null) {
                config.setSyncRetryTimes(retryTimes);
                config.setAsyncRetryTimes(retryTimes);
            }
            if (StringUtils.equalsIgnoreCase(mode, "SYNC_MODE")) {
                config.setMode(ProducerMode.SYNC_MODE);
            }
            if (zipped != null) {
                config.setZipped(zipped);
            }
            if (threadPoolSize != null) {
                config.setThreadPoolSize(threadPoolSize);
            }

            Producer producer = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topic), config);

            producerMap.put(topic, producer);

            LOG.info("Added producer(topic=" + topic + ")");
        }

    }

    @Override
    public Producer getProducer(String topic) {
        return producerMap.get(topic);
    }

    @Override
    public void onConfigChange(String key, String value) {
        if (StringUtils.equals(key, Constant.PROPERTY_TOPIC)) {
            try {
                init(value);
            } catch (RemoteServiceInitFailedException e) {
                notifyService.alarm("Error initialize producer ", e, true);
            } catch (RuntimeException e) {
                notifyService.alarm("Error initialize producer ", e, true);
            }
            LOG.info("All producer's topic:" + producerMap.keySet());
        }
    }

    private void init(String config) throws RemoteServiceInitFailedException {
        String[] topics = StringUtils.split(config, ';');
        LOG.info("Initing producers with topics(" + Arrays.toString(topics) + ")");

        //每个topic创建一个producer(生产者是可以并发使用的)
        if (topics != null) {
            for (String topic : topics) {
                initializeProducer(topic);
            }
        }

    }

}
