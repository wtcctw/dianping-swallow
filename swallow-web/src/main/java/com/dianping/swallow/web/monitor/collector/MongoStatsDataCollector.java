package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.config.impl.AbstractSwallowServerConfig;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoCluster;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.observer.Observer;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.StatisData;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.stats.MongoStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.service.MongoResourceService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author   mingdongli
 * 15/12/23  下午3:56.
 */
@Component
public class MongoStatsDataCollector extends AbstractRealTimeCollector implements MonitorDataListener, Observer, ApplicationContextAware {

    @Autowired
    private ProducerDataRetriever producerDataRetriever;

    @Resource(name = "swallowServerConfig")
    private SwallowConfig swallowConfig;

    @Resource(name = "mongoResourceService")
    private MongoResourceService mongoResourceService;

    private ApplicationContext applicationContext;

    /* 数据库读取的是3个ip */
    private Map<String, String> ipToCatalog = new ConcurrentHashMap<String, String>();

    /* 这里的mongo是swallowConfig中从lion读取的，只有两个ip */
    private Map<String, String> topicToMongo = new ConcurrentHashMap<String, String>();

    private Map<MongoStatsDataKey, MongoStatsDataContainer> mongoStatsDataMap = new ConcurrentHashMap<MongoStatsDataKey, MongoStatsDataContainer>();

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
        collectorName = getClass().getSimpleName();
        producerDataRetriever.registerListener(this);
        swallowConfig.addObserver(this);

        List<MongoResource> mongoResources = mongoResourceService.findAll();
        for (MongoResource mr : mongoResources) {
            updateIpToCatalog(mr);
        }
        mongoResourceService.addObserver(this);
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
            lastData = producerDataRetriever.getMaxData(new CasKeys(ConsumerDataRetrieverWrapper.TOTAL, topic), StatisType.SAVE);
            if (lastData != null && !lastData.isEmpty()) {
                String mongoIp = topicToMongo.get(topic);
                if (StringUtils.isBlank(mongoIp)) {
                    TopicConfig topicConfig = swallowConfig.getTopicConfig(topic);

                    if (topicConfig == null || (topicConfig != null && StringUtils.isBlank(topicConfig.getStoreUrl()))) {
                        addToDefaultMongoIp(topic, lastData);
                    } else {
                        mongoIp = doExtractMongoIp(topicConfig);

                        if (StringUtils.isNotBlank(mongoIp)) {
                            topicToMongo.put(topic, mongoIp);
                            addMongoStatsData(mongoIp, lastData);
                        } else {
                            addToDefaultMongoIp(topic, lastData);
                        }
                        
                    }

                } else {
                    addMongoStatsData(mongoIp, lastData);
                }
            }
        }

        Set<MongoStatsDataKey> mongoStatsDataKeys = mongoStatsDataMap.keySet();
        for (MongoStatsDataKey msdk : mongoStatsDataKeys) {
            MongoStatsDataContainer mongoStatsDataContainer = mongoStatsDataMap.get(msdk);
            if (mongoStatsDataContainer.isUpToMaxSize() && mongoStatsDataContainer.isEmpty()) {
                mongoStatsDataMap.remove(msdk);
                continue;
            }
            mongoStatsDataContainer.store();
        }

    }

    private void addMongoStatsData(String mongoIp, NavigableMap<Long, StatisData> lastData) {

        MongoStatsDataKey mongoStatsDataKey;
        try {
            mongoStatsDataKey = generateMongoStatsDataKey(mongoIp);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return;
        }

        MongoStatsDataContainer mongoStatsDataContainer = mongoStatsDataMap.get(mongoStatsDataKey);
        if (mongoStatsDataContainer == null) {
            mongoStatsDataContainer = applicationContext.getBean(MongoStatsDataContainer.class);
            mongoStatsDataMap.put(mongoStatsDataKey, mongoStatsDataContainer);
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

        if (observable instanceof MongoResourceService) {
            MongoResource mongoResource = (MongoResource) rawArgs;
            updateIpToCatalog(mongoResource);
            return;
        }

        AbstractSwallowServerConfig.SwallowConfigArgs args = (AbstractSwallowServerConfig.SwallowConfigArgs) rawArgs;

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

    private void createOrUpdateTopicToMongo(AbstractSwallowServerConfig.SwallowConfigArgs args) {

        String ip = extractMongoIp(args);
        if (StringUtils.isNotBlank(ip)) {
            topicToMongo.put(args.getTopic(), ip);
        } else {
            topicToMongo.put(args.getTopic(), loadDefaultConfigIp());
        }
    }

    private void addToDefaultMongoIp(String topic, NavigableMap<Long, StatisData> lastData) {
        String defaultIp = loadDefaultConfigIp();
        topicToMongo.put(topic, defaultIp);
        addMongoStatsData(defaultIp, lastData);
    }

    private String loadDefaultConfigIp() {
        TopicConfig topicConfig = swallowConfig.defaultTopicConfig();
        String defaultIp = doExtractMongoIp(topicConfig);
        if (StringUtils.isBlank(defaultIp)) {
            throw new RuntimeException("swallow.topiccfg.default 没有配置");
        }
        return defaultIp;
    }

    private void removeFromTopicToMongo(AbstractSwallowServerConfig.SwallowConfigArgs args) {

        String ip = extractMongoIp(args);
        if (StringUtils.isNotBlank(ip)) {
            topicToMongo.remove(args.getTopic());
        }
    }

    private String extractMongoIp(AbstractSwallowServerConfig.SwallowConfigArgs args) {

        String topic = args.getTopic();
        TopicConfig topicConfig = swallowConfig.getTopicConfig(topic);
        return doExtractMongoIp(topicConfig);
    }

    private String doExtractMongoIp(TopicConfig topicConfig) {

        if (topicConfig == null) {
            return StringUtils.EMPTY;
        }

        String storeUrl = topicConfig.getStoreUrl();
        if (StringUtils.isNotBlank(storeUrl) && storeUrl.startsWith(MongoCluster.schema)) {
            return storeUrl.substring(MongoCluster.schema.length()).trim();
        }

        return StringUtils.EMPTY;

    }

    public Map<MongoStatsDataKey, NavigableMap<Long, Long>> retrieveAllQpx(QPX qpx) {

        Map<MongoStatsDataKey, NavigableMap<Long, Long>> result = new HashMap<MongoStatsDataKey, NavigableMap<Long, Long>>();
        for (Map.Entry<MongoStatsDataKey, MongoStatsDataContainer> entry : mongoStatsDataMap.entrySet()) {
            MongoStatsDataKey mongoStatsDataKey = entry.getKey();
            MongoStatsDataContainer mongoStatsDataContainer = entry.getValue();
            NavigableMap<Long, Long> mongoQpx = mongoStatsDataContainer.retrieve(qpx);
            result.put(mongoStatsDataKey, mongoQpx);
        }

        return result;
    }

    public MongoStatsDataKey generateMongoStatsDataKey(String ip) throws NullPointerException {

        List<String> candidates = new ArrayList<String>();
        for (String key : ipToCatalog.keySet()) {
            if (key.startsWith(ip)) {
                candidates.add(key);
            }
        }

        String maxLengthIp;

        if (candidates.size() == 1) {
            maxLengthIp = candidates.get(0);
        } else {
            maxLengthIp = chooseMaxIPs(candidates);

        }

        if (maxLengthIp == null) {
            throw new NullPointerException("No match mongo for [" + ip + "] in ipToCatalog");
        }
        String catalog = ipToCatalog.get(maxLengthIp);
        return new MongoStatsDataKey(maxLengthIp, catalog);
    }

    private String chooseMaxIPs(List<String> candidates) {

        String maxLengthIp = null;
        int maxLength = -1;
        int length;

        for (String ip : candidates) {
            length = ip.split(",").length;
            if (length > maxLength) {
                maxLength = length;
                maxLengthIp = ip;
            }
        }

        return maxLengthIp;
    }

    public Map<MongoStatsDataKey, MongoStatsDataContainer> getMongoStatsDataMap() {
        return mongoStatsDataMap;
    }

    public Map<String, String> getTopicToMongo() {
        return topicToMongo;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {

        this.applicationContext = applicationContext;
    }

    public void setProducerDataRetriever(ProducerDataRetriever producerDataRetriever) {
        this.producerDataRetriever = producerDataRetriever;
    }

    public void setSwallowConfig(SwallowConfig swallowConfig) {
        this.swallowConfig = swallowConfig;
    }

    public void setMongoResourceService(MongoResourceService mongoResourceService) {
        this.mongoResourceService = mongoResourceService;
    }

    public void setIpToCatalog(Map<String, String> ipToCatalog) {
        this.ipToCatalog = ipToCatalog;
    }

    private void updateIpToCatalog(MongoResource mr) {
        String ip = mr.getIp();
        if (StringUtils.isNotBlank(ip)) {
            ipToCatalog.put(ip, mr.getCatalog());
        }
    }

    public static class MongoStatsDataKey {

        private String ip;

        private String catalog;

        public MongoStatsDataKey() {

        }

        public MongoStatsDataKey(String ip, String catalog) {
            this.ip = ip;
            this.catalog = catalog;
        }


        public String getIp() {
            return ip;
        }

        public String getCatalog() {
            return catalog;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MongoStatsDataKey that = (MongoStatsDataKey) o;

            if (ip != null ? !ip.equals(that.ip) : that.ip != null) return false;
            return !(catalog != null ? !catalog.equals(that.catalog) : that.catalog != null);

        }

        @Override
        public int hashCode() {
            int result = ip != null ? ip.hashCode() : 0;
            result = 31 * result + (catalog != null ? catalog.hashCode() : 0);
            return result;
        }
    }
}
