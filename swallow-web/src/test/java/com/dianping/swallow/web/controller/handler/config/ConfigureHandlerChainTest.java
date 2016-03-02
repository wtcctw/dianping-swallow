package com.dianping.swallow.web.controller.handler.config;

import com.dianping.swallow.common.internal.config.TOPIC_TYPE;
import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.service.KafkaServerResourceService;
import com.dianping.swallow.web.service.impl.ConsumerServerResourceServiceImpl;
import com.dianping.swallow.web.service.impl.MongoResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mingdongli
 *         15/10/23 下午6:03
 */
public class ConfigureHandlerChainTest extends MockTest {

    @Mock
    private MongoResourceServiceImpl mongoResourceService;

    @Mock
    private KafkaServerResourceService kafkaServerResourceService;

    @Mock
    private ConsumerServerResourceServiceImpl consumerServerResourceService;

    private ConfigureHandlerChain configureHandlerChain = new ConfigureHandlerChain();

    private TopicApplyDto topicApplyDto;

    private boolean testMongo = false;

    @Before
    public void setUp() throws Exception {

        topicApplyDto = new TopicApplyDto();
        topicApplyDto.setAmount(50);
        topicApplyDto.setSize(1);
        topicApplyDto.setTopic("swallow-test");
        topicApplyDto.setApprover("hongjun.zhong");

        MongoResource mongoResource = new MongoResource();
        mongoResource.setIp("1.1.1.1");
        mongoResource.setGroupName("default");

        Pair<String, ResponseStatus> pair = new Pair<String, ResponseStatus>();
        pair.setFirst("2.2.2.2");
        pair.setSecond(ResponseStatus.SUCCESS);

        List<KafkaServerResource> kafkaServerResourceList = new ArrayList<KafkaServerResource>();
        KafkaServerResource kafkaServerResource = new KafkaServerResource();
        kafkaServerResource.setZkServers("1.2.3.4:2181");
        kafkaServerResource.setActive(true);
        kafkaServerResourceList.add(kafkaServerResource);


        ConsumerServerHandler consumerServerHandler = new ConsumerServerHandler();
        MongoServerHandler mongoServerHandler = new MongoServerHandler();
        KafkaServerHandler kafkaServerHandler = new KafkaServerHandler();
        QuoteHandler quoteHandler = new QuoteHandler();

        if (testMongo) {
            topicApplyDto.setType("default");
            configureHandlerChain.addHandler(mongoServerHandler);
        }else{
            topicApplyDto.setType("kafka-default");
            topicApplyDto.setKafkaTopicType(TOPIC_TYPE.DURABLE_FIRST.toString());
            configureHandlerChain.addHandler(kafkaServerHandler);
        }

        configureHandlerChain.addHandler(consumerServerHandler);
        configureHandlerChain.addHandler(quoteHandler);

        kafkaServerHandler.setKafkaServerResourceService(kafkaServerResourceService);
        mongoServerHandler.setMongoResourceService(mongoResourceService);
        consumerServerHandler.setConsumerServerResourceService(consumerServerResourceService);
        Mockito.doReturn(mongoResource).when(mongoResourceService).findIdleMongoByType(topicApplyDto.getType());
        Mockito.doReturn(pair).when(consumerServerResourceService).loadIdleConsumerServer(topicApplyDto.getType());
        Mockito.doReturn(kafkaServerResourceList).when(kafkaServerResourceService).findByGroupName("kafka-default");

    }

    @Test
    public void testMongo() {
        LionConfigureResult lionConfigureResult = new LionConfigureResult();
        configureHandlerChain.handle(topicApplyDto, lionConfigureResult);
        System.out.println(lionConfigureResult.getConsumerServer());
        System.out.println(lionConfigureResult.getStorageServer());
        System.out.println(lionConfigureResult.getSize4SevenDay());
        Assert.assertTrue(lionConfigureResult.getConsumerServer().equals("2.2.2.2"));
        Assert.assertTrue(lionConfigureResult.getStorageServer().equals("mongodb://1.1.1.1"));
        Assert.assertTrue(lionConfigureResult.getSize4SevenDay() == 3500);
    }

    @Test
    public void testKafka() {

        LionConfigureResult lionConfigureResult = new LionConfigureResult();
        configureHandlerChain.handle(topicApplyDto, lionConfigureResult);
        System.out.println(lionConfigureResult.getConsumerServer());
        System.out.println(lionConfigureResult.getStorageServer());
        System.out.println(lionConfigureResult.getTopicType());
        Assert.assertTrue(lionConfigureResult.getConsumerServer().equals("2.2.2.2"));
        Assert.assertTrue(lionConfigureResult.getStorageServer().equals("kafka://1.2.3.4:2181"));
        Assert.assertTrue(lionConfigureResult.getTopicType() == TOPIC_TYPE.DURABLE_FIRST.toString());
    }

    @After
    public void tearDown() throws Exception {

    }
}