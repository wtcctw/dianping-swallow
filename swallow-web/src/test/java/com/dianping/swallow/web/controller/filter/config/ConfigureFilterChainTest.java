package com.dianping.swallow.web.controller.filter.config;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.result.ConfigureFilterResult;
import com.dianping.swallow.web.controller.filter.result.LionConfigure;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.impl.ConsumerServerResourceServiceImpl;
import com.dianping.swallow.web.service.impl.MongoResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 * 2015年9月24日下午2:49:37
 */
public class ConfigureFilterChainTest extends MockTest{

	@Mock
	private MongoResourceServiceImpl mongoResourceService;

	@Mock
	private ConsumerServerResourceServiceImpl consumerServerResourceService;

	private ConfigureFilterChain lionFilterChain = new ConfigureFilterChain(); 

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
		
		
		MongoConfigureFilter mongoLionFilter = new MongoConfigureFilter();
		ConsumerServerConfigureFilter consumerServerLionFilter = new ConsumerServerConfigureFilter();
		QuoteConfigureFilter quoteLionFilter = new QuoteConfigureFilter();
		lionFilterChain.addFilter(mongoLionFilter);
		lionFilterChain.addFilter(consumerServerLionFilter);
		lionFilterChain.addFilter(quoteLionFilter);
		mongoLionFilter.setMongoResourceService(mongoResourceService);
		consumerServerLionFilter.setConsumerServerResourceService(consumerServerResourceService);
		Mockito.doReturn(mongoResource).when(mongoResourceService).findIdleMongoByType(MongoType.findByType(topicApplyDto.getType()));
		Mockito.doReturn(pair).when(consumerServerResourceService).loadIdleConsumerServer();
	}

	@Test
	public void test() {
		ConfigureFilterResult lionFilterResult = new ConfigureFilterResult();
		LionConfigure lionConfigure = new LionConfigure();
		lionFilterResult.setLionConfigure(lionConfigure);
		lionFilterChain.doFilter(topicApplyDto, lionFilterResult, lionFilterChain);
		Assert.assertTrue(lionFilterResult.getStatus() == 0);
		Assert.assertTrue(lionFilterResult.getLionConfigure().getConsumerServer().equals("2.2.2.2"));
		Assert.assertTrue(lionFilterResult.getLionConfigure().getMongoServer().equals("1.1.1.1"));
		Assert.assertTrue(lionFilterResult.getLionConfigure().getSize4sevenday() == 3500);
	}

}
