package com.dianping.swallow.web.controller.handler.config;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.impl.ConsumerServerResourceServiceImpl;
import com.dianping.swallow.web.service.impl.MongoResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

/**
 * @author mingdongli
 *         15/10/23 下午6:03
 */
public class ConfigureHandlerChainTest extends MockTest {

    @Mock
    private MongoResourceServiceImpl mongoResourceService;

    @Mock
    private ConsumerServerResourceServiceImpl consumerServerResourceService;

    private ConfigureHandlerChain configureHandlerChain = new ConfigureHandlerChain();

    private TopicApplyDto topicApplyDto;

    @Before
    public void setUp() throws Exception {

        topicApplyDto = new TopicApplyDto();
        topicApplyDto.setAmount(50);
        topicApplyDto.setSize(1);
        topicApplyDto.setTopic("swallow-test");
        topicApplyDto.setApprover("hongjun.zhong");
        topicApplyDto.setType(MongoType.GENERAL.toString());

        MongoResource mongoResource = new MongoResource();
        mongoResource.setIp("1.1.1.1");
        mongoResource.setGroupName(MongoType.GENERAL.toString());

        Pair<String, ResponseStatus> pair = new Pair<String, ResponseStatus>();
        pair.setFirst("2.2.2.2");
        pair.setSecond(ResponseStatus.SUCCESS);


        ConsumerServerHandler consumerServerHandler = new ConsumerServerHandler();
        MongoServerHandler mongoServerHandler = new MongoServerHandler();
        QuoteHandler quoteHandler = new QuoteHandler();
        configureHandlerChain.addHandler(mongoServerHandler);
        configureHandlerChain.addHandler(consumerServerHandler);
        configureHandlerChain.addHandler(quoteHandler);

        mongoServerHandler.setMongoResourceService(mongoResourceService);
        consumerServerHandler.setConsumerServerResourceService(consumerServerResourceService);
        Mockito.doReturn(mongoResource).when(mongoResourceService).findIdleMongoByType(MongoType.findByType(topicApplyDto.getType()));
        Mockito.doReturn(pair).when(consumerServerResourceService).loadIdleConsumerServer();

    }

    @Test
    public void test() {
        LionConfigureResult lionConfigureResult = new LionConfigureResult();
        configureHandlerChain.handle(topicApplyDto, lionConfigureResult);
        System.out.println(lionConfigureResult.getConsumerServer());
        System.out.println(lionConfigureResult.getMongoServer());
        System.out.println(lionConfigureResult.getSize4SevenDay());
        Assert.assertTrue(lionConfigureResult.getConsumerServer().equals("2.2.2.2"));
        Assert.assertTrue(lionConfigureResult.getMongoServer().equals("1.1.1.1"));
        Assert.assertTrue(lionConfigureResult.getSize4SevenDay() == 3500);
    }

    @After
    public void tearDown() throws Exception {

    }
}