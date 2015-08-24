package com.dianping.swallow.web.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.service.ConsumerIdResourceService;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午10:37:39
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ConsumerIdResourceServiceTest {
	
	@Resource(name = "consumerIdResourceService")
	private ConsumerIdResourceService consumerIdResourceService;

	@Before
	public void setUp() throws Exception {
	}
	
	private ConsumerIdResource createConsumerIdResource(){
		
		ConsumerIdResource consumerIdResource = new ConsumerIdResource();
		
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = new ConsumerBaseAlarmSetting();
		QPSAlarmSetting sendQPSAlarmSetting = new QPSAlarmSetting();
		sendQPSAlarmSetting.setFluctuation(5);
		sendQPSAlarmSetting.setFluctuationBase(50);
		sendQPSAlarmSetting.setPeak(50);
		sendQPSAlarmSetting.setValley(5);
		
		QPSAlarmSetting ackQPSAlarmSetting = new QPSAlarmSetting();
		ackQPSAlarmSetting.setFluctuation(4);
		ackQPSAlarmSetting.setFluctuationBase(49);
		ackQPSAlarmSetting.setPeak(49);
		ackQPSAlarmSetting.setValley(4);
		
		consumerBaseAlarmSetting.setSendQpsAlarmSetting(sendQPSAlarmSetting);
		consumerBaseAlarmSetting.setAckQpsAlarmSetting(ackQPSAlarmSetting);
		
		consumerIdResource.setConsumerAlarmSetting(consumerBaseAlarmSetting);
		consumerIdResource.setCreateTime(new Date());
		consumerIdResource.setTopic("example");
		consumerIdResource.setConsumerId("consumerid-0");
		
		consumerIdResource.setTopic("example");
		consumerIdResource.setConsumerId("consumerid-0");
		
		return consumerIdResource;
		
	}

	@Test
	public void test() {
		
		ConsumerIdResource consumerIdResource = createConsumerIdResource();
		boolean result = consumerIdResourceService.insert(consumerIdResource);
		Assert.assertTrue(result);
		
		Pair<Long, List<ConsumerIdResource>> consumerIdResources = consumerIdResourceService.findByTopic(new TopicQueryDto("example"));
		Assert.assertNotNull(consumerIdResources);
		Assert.assertEquals(consumerIdResources.getFirst(), new Long(1));
		
		consumerIdResource = consumerIdResources.getSecond().get(0);
		consumerIdResource.setTopic("lmdtest");
		consumerIdResource.setConsumerId("consumerid-1");
		
		result = consumerIdResourceService.update(consumerIdResource);
		Assert.assertTrue(result);
		
		consumerIdResource = consumerIdResourceService.find("lmdtest", "consumerid-1");
		Assert.assertNotNull(consumerIdResource);
		
		ConsumerIdResource consumerIdResource1 = createConsumerIdResource();
		consumerIdResource1.setConsumerId("default");
		result = consumerIdResourceService.insert(consumerIdResource1);
		Assert.assertTrue(result);
		
		Pair<Long, List<ConsumerIdResource>> pages = consumerIdResourceService.findConsumerIdResourcePage(new  BaseDto(0, 31));
		Assert.assertNotNull(pages);
		long size = pages.getFirst();
		Assert.assertEquals(size, 2L);
		
		consumerIdResource1 = consumerIdResourceService.findDefault();
		Assert.assertNotNull(consumerIdResource1);
		
		int n = consumerIdResourceService.remove("example", "default");
		Assert.assertEquals(n, 1);

		n = consumerIdResourceService.remove("lmdtest", "consumerid-1");
		Assert.assertEquals(n, 1);
	}

}
