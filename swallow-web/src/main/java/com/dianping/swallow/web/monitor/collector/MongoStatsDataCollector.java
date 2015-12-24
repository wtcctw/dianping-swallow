package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowConfig;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.observer.Observer;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.structure.StatisData;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.stats.MongoStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author   mingdongli
 * 15/12/23  下午3:56.
 */
@Component
public class MongoStatsDataCollector extends AbstractRealTimeCollector implements MonitorDataListener, Observer, ApplicationContextAware {

    private static final String MONGO_PREFIX = "mongodb://";

    @Autowired
    private ProducerDataRetriever producerDataRetriever;

    @Resource(name = "swallowConfig")
    private SwallowConfig swallowConfig;

    private ApplicationContext applicationContext;

    private Map<String, String> topicToMongo = new ConcurrentHashMap<String, String>();

    private Map<String, MongoStatsDataContainer> mongoStatsDataMap = new ConcurrentHashMap<String, MongoStatsDataContainer>();

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
        collectorName = getClass().getSimpleName();
        producerDataRetriever.registerListener(this);
        swallowConfig.addObserver(this);
    }

    @Override
    public void doCollector() {

        if (logger.isInfoEnabled()) {
            logger.info("[doCollector] start collect MongoStatsData.");
        }

        NavigableMap<Long, StatisData> lastData;
        Set<String> topics = producerDataRetriever.getTopics();
        for (String topic : topics) {
            if (ConsumerDataRetrieverWrapper.TOTAL.equalsIgnoreCase(topic)) {
                continue;
            }
            lastData = producerDataRetriever.getLastStatisValue(new CasKeys(ConsumerDataRetrieverWrapper.TOTAL, topic), StatisType.SAVE);
            if (lastData != null && !lastData.isEmpty()) {
                String mongoIp = topicToMongo.get(topic);
                if (StringUtils.isBlank(mongoIp)) {
                    TopicConfig topicConfig = swallowConfig.getTopicConfig(topic);

                    if (topicConfig == null || (topicConfig != null && StringUtils.isBlank(topicConfig.getStoreUrl()))) {
                        topicToMongo.put(topic, loadDefaultConfigIp());
                        addMongoStatsData(mongoIp, lastData);
                    } else {
                        mongoIp = doExtractMongoIp(topicConfig);
                        if (StringUtils.isNotBlank(mongoIp)) {
                            topicToMongo.put(topic, mongoIp);
                            addMongoStatsData(mongoIp, lastData);
                        }
                    }

                } else {
                    addMongoStatsData(mongoIp, lastData);
                }
            }
        }

        Set<String> ips = mongoStatsDataMap.keySet();
        for (String ip : ips) {
            MongoStatsDataContainer mongoStatsDataContainer = mongoStatsDataMap.get(ip);
            if (mongoStatsDataContainer.isUpToMaxSize() && mongoStatsDataContainer.isEmpty()) {
                mongoStatsDataMap.remove(ip);
                continue;
            }
            mongoStatsDataContainer.store();
        }

    }

    private void addMongoStatsData(String mongoIp, NavigableMap<Long, StatisData> lastData) {

        MongoStatsDataContainer mongoStatsDataContainer = mongoStatsDataMap.get(mongoIp);
        if (mongoStatsDataContainer == null) {
            mongoStatsDataContainer = applicationContext.getBean(MongoStatsDataContainer.class);
            mongoStatsDataMap.put(mongoIp, mongoStatsDataContainer);
        }
        Long time = lastData.firstKey();
        StatisData statisData = lastData.get(time);
        Long count = statisData.getCount();
        Byte interval = statisData.getIntervalCount();
        mongoStatsDataContainer.add(time, new MongoStatsData(mongoIp, count, interval, time));
    }

    @Override
    public void achieveMonitorData() {

        executor.submit(new Runnable() {
            @Override
            public void run() {
                SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, collectorName + "-MongoMonitor");
                catWrapper.doAction(new SwallowAction() {
                    @Override
                    public void doAction() throws SwallowException {
                        doCollector();
                    }
                });
            }
        });

    }

    @Override
    public void update(Observable observable, Object rawArgs) {

        AbstractSwallowConfig.SwallowConfigArgs args = (AbstractSwallowConfig.SwallowConfigArgs) rawArgs;

        if (logger.isInfoEnabled()) {
            logger.info("[update]" + args);
        }

        switch (args.getItem()) {

            case TOPIC_STORE:

                switch (args.getBehavior()) {

                    case ADD:
                    case UPDATE:
                        createOrUpdateTopicToMongo(args);
                        break;
                    case DELETE:
                        removeFromTopicToMongo(args);
                        break;
                    default:
                        logger.warn("[update][unknown behavior]" + args.getBehavior());
                }
                break;
            default:
                logger.warn("[update][unknown item]" + args);
        }
    }

    private void createOrUpdateTopicToMongo(AbstractSwallowConfig.SwallowConfigArgs args) {

        String ip = extractMongoIp(args);
        if (StringUtils.isNotBlank(ip)) {
            topicToMongo.put(args.getTopic(), ip);
        } else {
            topicToMongo.put(args.getTopic(), loadDefaultConfigIp());
        }
    }

    private String loadDefaultConfigIp() {
        TopicConfig topicConfig = swallowConfig.defaultTopicConfig();
        String defaultIp = doExtractMongoIp(topicConfig);
        if (StringUtils.isBlank(defaultIp)) {
            throw new RuntimeException("swallow.topiccfg.default 没有配置");
        }
        return defaultIp;
    }

    private void removeFromTopicToMongo(AbstractSwallowConfig.SwallowConfigArgs args) {

        String ip = extractMongoIp(args);
        if (StringUtils.isNotBlank(ip)) {
            topicToMongo.remove(ip);
        }
    }

    private String extractMongoIp(AbstractSwallowConfig.SwallowConfigArgs args) {

        String topic = args.getTopic();
        TopicConfig topicConfig = swallowConfig.getTopicConfig(topic);
        return doExtractMongoIp(topicConfig);
    }

    private String doExtractMongoIp(TopicConfig topicConfig) {

        if (topicConfig == null) {
            return StringUtils.EMPTY;
        }

        String storeUrl = topicConfig.getStoreUrl();
        if (StringUtils.isNotBlank(storeUrl) && storeUrl.startsWith(MONGO_PREFIX)) {
            String ipAddrs = storeUrl.substring(MONGO_PREFIX.length());

            if (ipAddrs.indexOf(":") != -1) {
                return ipAddrs.split(":")[0];
            } else {
                return ipAddrs;
            }
        }

        return StringUtils.EMPTY;

    }

    public Map<String, NavigableMap<Long, Long>> retrieveAllQpx(QPX qpx) {

        Map<String, NavigableMap<Long, Long>> result = new HashMap<String, NavigableMap<Long, Long>>();
        for (Map.Entry<String, MongoStatsDataContainer> entry : mongoStatsDataMap.entrySet()) {
            String ip = entry.getKey();
            MongoStatsDataContainer mongoStatsDataContainer = entry.getValue();
            NavigableMap<Long, Long> mongoQpx = mongoStatsDataContainer.retrieve(qpx);
            result.put(ip, mongoQpx);
        }

        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }
}
