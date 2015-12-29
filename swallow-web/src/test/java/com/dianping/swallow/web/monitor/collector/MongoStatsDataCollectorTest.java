package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.config.TopicConfig;
import com.dianping.swallow.common.internal.dao.impl.mongodb.MongoCluster;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.structure.StatisData;
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

    private MongoStatsDataCollector mongoStatsDataCollector;

    @Before
    public void setUp() throws Exception {

        mongoStatsDataCollector = new MongoStatsDataCollector();

        List<NavigableMap<Long, StatisData>> mapList = new ArrayList<NavigableMap<Long, StatisData>>();
        NavigableMap<Long, StatisData> lastDatas = new ConcurrentSkipListMap<Long, StatisData>();
        lastDatas.put(123456789L, new StatisData(10L, 10L, 10L, 10L, (byte) 6));
        for(int i = 0; i < 6; ++i){
            mapList.add(lastDatas);
        }

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

        Mockito.doReturn(topics).when(producerDataRetriever).getTopics();

        for (int i = 0; i < topicList.size(); ++i) {
            Mockito.doReturn(mapList.get(i)).when(producerDataRetriever).getLastStatisValue(new CasKeys(ConsumerDataRetrieverWrapper.TOTAL, topicList.get(i)), StatisType.SAVE);
        }

        for (int i = 0; i < topicList.size(); ++i) {
            Mockito.doReturn(topicConfigs.get(i)).when(swallowConfig).getTopicConfig(topicList.get(i));
        }

        Mockito.doReturn(topicConfigs.get(0)).when(swallowConfig).defaultTopicConfig();


        Mockito.doReturn(mongoResourceList).when(mongoResourceService).findAll();

    }

    @Test
    public void testDoCollector() {

        mongoStatsDataCollector.doCollector();
        Map<String, String> topicToMongo = mongoStatsDataCollector.getTopicToMongo();
        Assert.assertEquals(topicToMongo.keySet().size(), 0);
    }
}