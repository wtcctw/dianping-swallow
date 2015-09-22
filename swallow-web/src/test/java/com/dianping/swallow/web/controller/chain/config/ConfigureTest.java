package com.dianping.swallow.web.controller.chain.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.chain.config.Configure.ConfigureResult;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.impl.ConsumerServerResourceServiceImpl;
import com.dianping.swallow.web.service.impl.MongoResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月21日下午8:21:14
 */
public class ConfigureTest extends MockTest{

	@Mock
	private MongoResourceServiceImpl mongoResourceService;

	@Mock
	private ConsumerServerResourceServiceImpl consumerServerResourceService;

	private MongoConfigure configure;

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
		mongoResource.setMongoType(MongoType.GENERAL);
		
		Pair<String, ResponseStatus> pair = new Pair<String, ResponseStatus>();
		pair.setFirst("2.2.2.2");
		pair.setSecond(ResponseStatus.SUCCESS);
		
		
		QuoteConfigure quoteConfigure = new QuoteConfigure();
		ConsumerServerConfigure consumerServerConfigure = new ConsumerServerConfigure(quoteConfigure);
		MongoConfigure mongoConfigure = new MongoConfigure(consumerServerConfigure);
		
		mongoConfigure.setMongoResourceService(mongoResourceService);
		consumerServerConfigure.setConsumerServerResourceService(consumerServerResourceService);
		configure = mongoConfigure;
		Mockito.doReturn(mongoResource).when(mongoResourceService).findIdleMongoByType(MongoType.findByType(topicApplyDto.getType()));
		Mockito.doReturn(pair).when(consumerServerResourceService).loadIdleConsumerServer();
	}

	@Test
	public void test() {
		ConfigureResult configureResult = new ConfigureResult();
		configure.buildConfigure(topicApplyDto, configureResult);
		Assert.assertTrue(configureResult.getResponseStatus() == ResponseStatus.SUCCESS);
		Assert.assertTrue(configureResult.getConsumerServer().equals("2.2.2.2"));
		Assert.assertTrue(configureResult.getMongoServer().equals("1.1.1.1"));
		Assert.assertTrue(configureResult.getSize4servenday() == 3500);
	}

}
