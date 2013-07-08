package com.dianping.swallow.broker.service.consumer.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.broker.conf.Constant;
import com.dianping.swallow.broker.monitor.NotifyService;
import com.dianping.swallow.broker.service.consumer.ConsumerHolder;
import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.consumer.ConsumerConfig;

//TODO:状态监控页面
@Service
public class ConsumerHolderImpl implements ConsumerHolder, ConfigChangeListener {

    private static final String         SWALLOW_BROKER_CONSUMER_PREFIX = "swallow.broker.consumer.";

    private static final Logger         LOG                            = LoggerFactory
                                                                               .getLogger(ConsumerHolderImpl.class);

    private Map<String, ConsumerBroker> consumerBrokerMap              = new HashMap<String, ConsumerBroker>();

    private Set<String>                 topics                         = new HashSet<String>();

    @Autowired
    private DynamicConfig               dynamicConfig;

    @Autowired
    private NotifyService               notifyService;

    @PostConstruct
    public void init() {
        build();

        //监听lion
        dynamicConfig.addConfigChangeListener(this);

        start();
    }

    /**
     * 读取配置项，初始化所有ConsumerBroker，此初始化方法可被多次调用(不能并发)，所以当配置项所生变化时，可以重新调用该方法即可。
     */
    private void build() {
        LOG.info("Building consumerBrokers...");

        Map<String, ConsumerBroker> map = new HashMap<String, ConsumerBroker>();

        //获取<topic>配置项
        String topicStr = dynamicConfig.get(Constant.PROPERTY_TOPIC);

        //初始化所有topic对应的consumer
        init(map, topicStr);

        //新的map替换旧的map
        Map<String, ConsumerBroker> oldMap = consumerBrokerMap;
        consumerBrokerMap = map;

        //对于被删除的consumerBroker(需要将它停掉)
        for (String newKey : map.keySet()) {
            oldMap.remove(newKey);//移除新的map中存在的key，剩下是则是被删除的key
        }
        for (ConsumerBroker toBeStopConsumerBroker : oldMap.values()) {
            toBeStopConsumerBroker.close();
            LOG.info(toBeStopConsumerBroker + " is closed.");
        }

        LOG.info("Build done, consumerBrokerMap is " + consumerBrokerMap);
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

    @PreDestroy
    @Override
    public void close() {
        for (ConsumerBroker consumerBroker : consumerBrokerMap.values()) {
            if (consumerBroker.isActive()) {
                consumerBroker.close();
                LOG.info("Closed ConsumerBroker:" + consumerBroker);
            }
        }
    }

    /**
     * 监听swallow.broker.topic和swallow.broker.consumer.<topic>.consumerId参数
     */
    @Override
    public void onConfigChange(String key, String value) {
        LOG.info("Invoke onConfigChange, key='" + key + "', value='" + value + "'");

        key = StringUtils.trim(key);

        boolean needReInit = false;

        //看看是否是Topic配置项变化，或者是否是swallow.broker.consumer.<topic>.consumerId配置项变化。
        //如果是，则需要重新初始化
        if (StringUtils.equals(key, Constant.PROPERTY_TOPIC)) { //key是否是swallow.broker.topic
            needReInit = true;
        } else {
            for (String topic : topics) {//key是否是swallow.broker.consumer.<topic>
                if (StringUtils.equals(key, getTopicConsumerPropertyName(topic))) {
                    needReInit = true;
                    break;
                }
            }
            if (!needReInit) {
                for (String consumerKey : consumerBrokerMap.keySet()) {//是否是swallow.broker.consumer.<topic>.<consumerId>.<url>
                    if (StringUtils.equals(key, getTopicConsumerNumPropertyName(consumerKey, "url"))) {
                        //找到该ConsumerBroker，修改其url
                        ConsumerBroker consumerBroker = consumerBrokerMap.get(consumerKey);
                        consumerBroker.setUrl(value);
                        LOG.info("ConsumerBroker(" + consumerKey + ")'s url changed to: " + value);
                        needReInit = false;
                        break;
                    }
                }
            }
        }

        if (needReInit) {
            try {
                build();
                //lion的配置修改，可能会新增ConsumerBroker，所以需要触发启动
                start();
            } catch (RuntimeException e) {
                notifyService.alarm("Error when initialize ConsumerBrokers ", e, true);
            }
        }
    }

    private void init(Map<String, ConsumerBroker> map, String topicStr) {
        if (StringUtils.isNotBlank(topicStr)) {
            String[] topics = StringUtils.split(topicStr.trim(), ';');
            LOG.info("Initing consumers with topics(" + Arrays.toString(topics) + ")");

            //每个topic创建一个consumer
            if (topics != null) {
                for (String topic : topics) {
                    //将topic存起来
                    this.topics.add(topic);
                    //每个topic初始化其consumer
                    initConsumerBrokers(map, topic);
                }
            }

        }
    }

    /**
     * 初始化某个topic下的所有ConsumerBroker
     * 
     * @param map
     */
    private void initConsumerBrokers(Map<String, ConsumerBroker> map, String topic) {
        String consumerIdsStr = dynamicConfig.get(getTopicConsumerPropertyName(topic));

        if (StringUtils.isNotBlank(consumerIdsStr)) {
            String[] splits = StringUtils.split(consumerIdsStr, ';');
            if (splits != null) {
                for (String split : splits) {
                    initConsumerBroker(map, topic, split);
                }
            }
        }
    }

    /**
     * 初始化某个ConsumerBroker
     * 
     * @param map
     */
    private void initConsumerBroker(Map<String, ConsumerBroker> map, String topic, String config) {
        String[] consumerIdAndNum = StringUtils.split(config, ',');
        Validate.isTrue(consumerIdAndNum != null && consumerIdAndNum.length == 2, "Error config :" + config);

        String consumerId = consumerIdAndNum[0];
        String num = consumerIdAndNum[1];

        String key = getConsumerKey(topic, consumerId, num);

        //如果key对应的ConsumerBroker不存在，则可以创建ConsumerBroker; 已经存在复用，不创建
        ConsumerBroker consumerBroker = consumerBrokerMap.get(key);
        if (consumerBroker == null) {
            LOG.info("ConsumerBroker with key '" + key + "' is not exsits, so create it!");

            String url = StringUtils.trimToNull(dynamicConfig.get(getTopicConsumerNumPropertyName(topic, consumerId,
                    num, "url")));
            Integer threadPoolSize = NumberUtils.createInteger(StringUtils.trimToNull(dynamicConfig
                    .get(getTopicConsumerNumPropertyName(topic, consumerId, num, "threadPoolSize"))));
            Integer delayBaseOnBackoutMessageException = NumberUtils
                    .createInteger(StringUtils.trimToNull(dynamicConfig.get(getTopicConsumerNumPropertyName(topic,
                            consumerId, num, "delayBaseOnBackoutMessageException"))));
            Integer delayUpperboundOnBackoutMessageException = NumberUtils.createInteger(StringUtils
                    .trimToNull(dynamicConfig.get(getTopicConsumerNumPropertyName(topic, consumerId, num,
                            "delayUpperboundOnBackoutMessageException"))));
            Integer retryCountOnBackoutMessageException = NumberUtils.createInteger(StringUtils
                    .trimToNull(dynamicConfig.get(getTopicConsumerNumPropertyName(topic, consumerId, num,
                            "retryCountOnBackoutMessageException"))));

            Validate.isTrue(StringUtils.isNotBlank(url), "Url(" + url + ") is blank!");

            ConsumerConfig consumerConfig = new ConsumerConfig();
            if (delayBaseOnBackoutMessageException != null) {
                consumerConfig.setDelayBaseOnBackoutMessageException(delayBaseOnBackoutMessageException);
            }
            if (delayUpperboundOnBackoutMessageException != null) {
                consumerConfig.setDelayUpperboundOnBackoutMessageException(delayUpperboundOnBackoutMessageException);
            }
            if (retryCountOnBackoutMessageException != null) {
                consumerConfig.setRetryCountOnBackoutMessageException(retryCountOnBackoutMessageException);
            }
            if (threadPoolSize != null) {
                consumerConfig.setThreadPoolSize(threadPoolSize);
            }
            consumerBroker = new ConsumerBroker(topic, consumerId, url, consumerConfig);
            consumerBroker.setNotifyService(notifyService);
            LOG.info("ConsumerBroker with key '" + key + "' is created!");
        }

        map.put(key, consumerBroker);
        LOG.info("Added ConsumerBroker:" + consumerBroker);
    }

    //如swallow.broker.consumer.example
    private String getTopicConsumerPropertyName(String topic) {
        return StringUtils.trimToNull(SWALLOW_BROKER_CONSUMER_PREFIX + topic);
    }

    //如swallow.broker.consumer.example.swallow-broker.1.url
    private String getTopicConsumerNumPropertyName(String topic, String consumerId, String num, String suffix) {
        return getTopicConsumerNumPropertyName(getConsumerKey(topic, consumerId, num), suffix);
    }

    //如swallow.broker.consumer.example.swallow-broker.1.url
    private String getTopicConsumerNumPropertyName(String comsunerKey, String suffix) {
        return SWALLOW_BROKER_CONSUMER_PREFIX + comsunerKey + "." + suffix;
    }

    private String getConsumerKey(String topic, String consumerId, String num) {
        return topic + "." + consumerId + "." + num;
    }

    @Override
    public Map<String, ConsumerBroker> getConsumerBrokerMap() {
        return consumerBrokerMap;
    }

}
