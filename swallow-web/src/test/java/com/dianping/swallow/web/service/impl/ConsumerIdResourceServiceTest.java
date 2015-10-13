package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
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
import com.dianping.swallow.web.controller.utils.IpInfoUtils;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao.ConsumerIdParam;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
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
		consumerIdResource.setConsumerId("apollo-message-lmd");
		
		List<String> consumerip = new ArrayList<String>();
		consumerip.add("1.0.0.1");
		consumerip.add("1.0.0.2");
		consumerip.add("1.0.0.3");
		
		List<IpInfo> ipInfo = IpInfoUtils.buildIpInfo(consumerip);
		consumerIdResource.setConsumerIpInfos(ipInfo);
		consumerIdResource.setAlarm(Boolean.TRUE);
		
		return consumerIdResource;
		
	}

	@Test
	public void test() {
		
		ConsumerIdResource consumerIdResource = createConsumerIdResource();
		boolean result = consumerIdResourceService.insert(consumerIdResource);
		Assert.assertTrue(result);
		
		ConsumerIdParam consumerIdQueryDto = new ConsumerIdParam();
		consumerIdQueryDto.setTopic("example1");
		consumerIdQueryDto.setOffset(0);
		consumerIdQueryDto.setLimit(31);
		Pair<Long, List<ConsumerIdResource>> consumerIdResources = consumerIdResourceService.findByTopic(consumerIdQueryDto);
		Assert.assertNotNull(consumerIdResources);
		Assert.assertEquals(consumerIdResources.getFirst(), new Long(1));
		
		consumerIdResource = consumerIdResources.getSecond().get(0);
		consumerIdResource.setTopic("lmdtest");
		consumerIdResource.setConsumerId("consumerid-1");
		
		result = consumerIdResourceService.update(consumerIdResource);
		Assert.assertTrue(result);
		
		consumerIdQueryDto.setTopic("lmdtest");
		consumerIdQueryDto.setConsumerId("consumerid-1");
		consumerIdResource = consumerIdResourceService.find(consumerIdQueryDto).getSecond().get(0);
		Assert.assertNotNull(consumerIdResource);
		
		ConsumerIdResource consumerIdResource1 = createConsumerIdResource();
		consumerIdResource1.setConsumerId("default");
		result = consumerIdResourceService.insert(consumerIdResource1);
		Assert.assertTrue(result);
		
		Pair<Long, List<ConsumerIdResource>> pages = consumerIdResourceService.findConsumerIdResourcePage(consumerIdQueryDto);
		Assert.assertNotNull(pages);
		long size = pages.getFirst();
		Assert.assertEquals(size, 2L);

		int n = consumerIdResourceService.remove("lmdtest", "consumerid-1");
		Assert.assertEquals(n, 1);
		
		n = consumerIdResourceService.remove("example", "default");
		Assert.assertEquals(n, 1);

	}

}
