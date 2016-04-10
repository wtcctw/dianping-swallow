package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoCluster;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.StatisData;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.service.MongoResourceService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.context.ApplicationContext;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Author   mingdongli
 * 15/12/29  上午11:42.
 */
public class MongoStatsDataCollectorTest extends MockTest {

    @Mock
    private MongoResourceService mongoResourceService;

    @Mock
    private ProducerDataRetriever producerDataRetriever;

    @Mock
    private SwallowConfig swallowConfig;

    @Mock
    private ApplicationContext applicationContext;

    private MongoStatsDataCollector mongoStatsDataCollector;

    @Before
    public void setUp() throws Exception {

        mongoStatsDataCollector = new MongoStatsDataCollector();

        NavigableMap<Long, StatisData> lastDatas = new ConcurrentSkipListMap<Long, StatisData>();
        lastDatas.put(123456789L, new StatisData(10L, 10L, 10L, 10L,10L,10L, (byte) 6));

        Map<String, String> ipToCatalog = new HashMap<String, String>();
        ipToCatalog.put("10.1.101.155:21017,10.1.101.157:27017,10.2.15.25:27017","缓存消息队列");
        ipToCatalog.put("10.1.115.11:27018,10.1.115.12:27018,10.2.15.28:27018","Swallow01消息队列");
        ipToCatalog.put("10.1.6.31:21018,10.1.6.32:27018,10.2.15.26:27018","团购消息队列");
        ipToCatalog.put("10.1.6.186:27017,10.1.6.188:27017,10.2.15.27:27017","交易消息队列");
        ipToCatalog.put("10.3.10.44:27017,10.3.10.48:27017,10.2.15.27:27018","交易消息队列02");
        ipToCatalog.put("10.1.101.155:27018,10.1.101.157:27018,10.2.15.25:27018","下单消息队列");
        ipToCatalog.put("10.1.115.11:27017,10.1.115.12:27017,10.2.15.28:27017","搜索消息队列");
        mongoStatsDataCollector.setIpToCatalog(ipToCatalog);

        List<MongoResource> mongoResourceList = new ArrayList<MongoResource>();
        MongoResource mongoResource = new MongoResource();
        mongoResource.setIp("10.1.101.155:21017,10.1.101.157:27017");
        mongoResource.setCatalog("缓存消息队列");
        mongoResourceList.add(mongoResource);

        mongoResource = new MongoResource();
        mongoResource.setIp("10.1.115.11:27018,10.1.115.12:27018");
        mongoResource.setCatalog("Swallow01消息队列");
        mongoResourceList.add(mongoResource);

        mongoResource = new MongoResource();
        mongoResource.setIp("10.1.6.31:21018,10.1.6.31:27018");
        mongoResource.setCatalog("团购消息队列");
        mongoResourceList.add(mongoResource);

        mongoResource = new MongoResource();
        mongoResource.setIp("10.1.6.186:27017,10.1.6.188:27017");
        mongoResource.setCatalog("交易消息队列");
        mongoResourceList.add(mongoResource);

        mongoResource = new MongoResource();
        mongoResource.setIp("10.3.10.44:27017,10.3.10.48:27017");
        mongoResource.setCatalog("交易消息队列02");
        mongoResourceList.add(mongoResource);

        mongoResource = new MongoResource();
        mongoResource.setIp("10.1.101.155:27018,10.1.101.157:27018");
        mongoResource.setCatalog("下单消息队列");
        mongoResourceList.add(mongoResource);

        mongoResource = new MongoResource();
        mongoResource.setIp("10.1.115.11:27017,10.1.115.12:27017");
        mongoResource.setCatalog("搜索消息队列");
        mongoResourceList.add(mongoResource);

        Set<String> topics = new HashSet<String>();

        topics.add("example");
        topics.add("LoadTestTopic-0");

        topics.add("LoadTestTopic-1");
        topics.add("LoadTestTopic-2");
        topics.add("LoadTestTopic-3");
        topics.add("LoadTestTopic-4");

        List<String> topicList = new ArrayList<String>();
        for (String topic : topics) {
            topicList.add(topic);
        }

        List<TopicConfig> topicConfigs = new ArrayList<TopicConfig>();
        topicConfigs.add(new TopicConfig(MongoCluster.schema + "10.1.101.155:21017,10.1.101.157:27017", 1, 10));
        topicConfigs.add(new TopicConfig(MongoCluster.schema + "10.1.115.11:27018,10.1.115.12:27018", 1, 10));
        topicConfigs.add(new TopicConfig(MongoCluster.schema + "10.1.6.31:21018,10.1.6.31:27018", 1, 10));
        topicConfigs.add(new TopicConfig(MongoCluster.schema + "10.1.6.186:27017,10.1.6.188:27017", 1, 10));
        topicConfigs.add(new TopicConfig(MongoCluster.schema + "10.3.10.44:27017,10.3.10.48:27017", 1, 10));
        topicConfigs.add(new TopicConfig(MongoCluster.schema + "10.1.101.155:27018,10.1.101.157:27018", 1, 10));

        mongoStatsDataCollector.setProducerDataRetriever(producerDataRetriever);
        mongoStatsDataCollector.setMongoResourceService(mongoResourceService);
        mongoStatsDataCollector.setSwallowConfig(swallowConfig);
        mongoStatsDataCollector.setApplicationContext(applicationContext);

        Mockito.doReturn(topics).when(producerDataRetriever).getTopics();
        Mockito.doReturn(new MongoStatsDataContainerImpl()).when(applicationContext).getBean(MongoStatsDataContainer.class);

        List<CasKeys> casKeyses = new ArrayList<CasKeys>();

        for (int i = 0; i < topicList.size(); ++i) {
            casKeyses.add(new CasKeys(ConsumerDataRetrieverWrapper.TOTAL, topicList.get(i)));
        }
        for (int i = 0; i < topicList.size(); ++i) {
            Mockito.doReturn(lastDatas).when(producerDataRetriever).getMaxData(casKeyses.get(i), StatisType.SAVE);
        }


        for (int i = 0; i < topicList.size(); ++i) {
            Mockito.doReturn(topicConfigs.get(i)).when(swallowConfig).getTopicConfig(topicList.get(i));
        }

        Mockito.doReturn(topicConfigs.get(0)).when(swallowConfig).defaultTopicConfig();


        Mockito.doReturn(mongoResourceList).when(mongoResourceService).findAll();

    }

    //在store处会抛异常
    @Test(expected = NullPointerException.class)
    public void testDoCollector() {

        mongoStatsDataCollector.doCollector();
        Map<String, String> topicToMongo = mongoStatsDataCollector.getTopicToMongo();
        for(Map.Entry<String, String> entry : topicToMongo.entrySet()){
            System.out.println(entry.getKey() + " -> " + entry.getValue());
        }

        Assert.assertEquals(topicToMongo.keySet().size(), 6);
    }
}