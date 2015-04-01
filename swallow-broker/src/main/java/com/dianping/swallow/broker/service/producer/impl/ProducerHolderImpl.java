package com.dianping.swallow.broker.service.producer.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import com.dianping.swallow.broker.service.producer.ProducerHolder;
import com.dianping.swallow.broker.util.AppUtils;
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
    private static final int DEFAULT_RETRY_TIME = 50;

   private static final Logger   logger                            = LoggerFactory.getLogger(ProducerHolderImpl.class);

    private static final String   SWALLOW_BROKER_PRODUCER_PREFIX = "swallow.broker.producer.";

    private Map<String, Producer> producerMap                    = new HashMap<String, Producer>();

    @Autowired
    private DynamicConfig         dynamicConfig;

    @Autowired
    private NotifyService         notifyService;

    @PostConstruct
    public void init() throws RemoteServiceInitFailedException {
        build();

        //监听lion
        dynamicConfig.addConfigChangeListener(this);

    }

    /**
     * 读取配置项，初始化所有Producer，此初始化方法可被多次调用(不能并发)，所以当配置项所生变化时，可以重新调用该方法即可。
     */
    private void build() throws RemoteServiceInitFailedException {
        logger.info("Building producers...");
        //<topic>,<topic>
        String config = dynamicConfig.get(Constant.PROPERTY_TOPIC);

        //初始化
        Map<String, Producer> map = new HashMap<String, Producer>();
        init(map, config);
        //构造完成，可以替换
        producerMap = map;

        logger.info("Build producers done, All producer's topic:" + AppUtils.highlight(producerMap.keySet().toString()));
    }

    @Override
    public Producer getProducer(String topic) {
        return producerMap.get(topic);
    }

    @Override
    public void onConfigChange(String key, String value) {
        logger.info("Invoke onConfigChange, key='" + key + "', value='" + value + "'");
        key = StringUtils.trim(key);
        if (StringUtils.equals(key, Constant.PROPERTY_TOPIC)) {
            try {
                build();
            } catch (RemoteServiceInitFailedException e) {
                notifyService.alarm("Error initialize producer ", e, true);
            } catch (RuntimeException e) {
                notifyService.alarm("Error initialize producer ", e, true);
            }
        }
    }

    private void init(Map<String, Producer> map, String config) throws RemoteServiceInitFailedException {
        if (StringUtils.isNotBlank(config)) {
            String[] topics = StringUtils.split(config, ';');
            logger.info("Initing producers with topics " + AppUtils.highlight(Arrays.toString(topics)));

            //每个topic创建一个producer(生产者是可以并发使用的)
            if (topics != null) {
                for (String topic : topics) {
                    initProducer(map, topic);
                }
            }
        }

    }

    private void initProducer(Map<String, Producer> map, String topic) throws RemoteServiceInitFailedException {
        //如果key对应的Producer不存在，则可以创建Producer; 已经存在复用，不创建
        Producer producer = producerMap.get(topic);
        if (producer == null) {
            logger.info("Producer with topic " + AppUtils.highlight(topic) + " is not exsits, so create it!");

            //根据topic，获取swallow.broker.producer.<topic>.*配置
            Integer retryTimes = NumberUtils.createInteger(StringUtils.trimToNull(dynamicConfig
                    .get(SWALLOW_BROKER_PRODUCER_PREFIX + topic + ".retryTimes")));
            String mode = StringUtils.trimToNull(dynamicConfig.get(SWALLOW_BROKER_PRODUCER_PREFIX + topic + ".mode"));
            Boolean zipped = BooleanUtils.toBoolean(StringUtils.trimToNull(dynamicConfig
                    .get(SWALLOW_BROKER_PRODUCER_PREFIX + topic + ".zipped")));
            Integer threadPoolSize = NumberUtils.createInteger(StringUtils.trimToNull(dynamicConfig
                    .get(SWALLOW_BROKER_PRODUCER_PREFIX + topic + ".threadPoolSize")));

            ProducerConfig config = new ProducerConfig();
            if (retryTimes != null) {
                config.setSyncRetryTimes(retryTimes);
                config.setAsyncRetryTimes(retryTimes);
            }else{
                config.setSyncRetryTimes(DEFAULT_RETRY_TIME);
                config.setAsyncRetryTimes(DEFAULT_RETRY_TIME);
            }
            if (StringUtils.equalsIgnoreCase(mode, "ASYNC_MODE")) {
                config.setMode(ProducerMode.ASYNC_MODE);
            }else{
                config.setMode(ProducerMode.SYNC_MODE);
            }
            if (zipped != null) {
                config.setZipped(zipped);
            }
            if (threadPoolSize != null) {
                config.setThreadPoolSize(threadPoolSize);
            }

            producer = ProducerFactoryImpl.getInstance().createProducer(Destination.topic(topic), config);

            logger.info("Producer with topic " + AppUtils.highlight(topic) + " is created!");
        }

        map.put(topic, producer);
        logger.info("Added Producer:" + producer);

    }

    @Override
    public Map<String, Producer> getProducerMap() {
        return producerMap;
    }

}
