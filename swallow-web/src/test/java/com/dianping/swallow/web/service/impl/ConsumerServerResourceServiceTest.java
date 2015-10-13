package com.dianping.swallow.web.service.impl;

import java.util.Date;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.service.ConsumerServerResourceService;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午6:39:52
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ConsumerServerResourceServiceTest {
	
	
	@Resource(name = "consumerServerResourceService")
	private ConsumerServerResourceService consumerServerResourceService;

	@Before
	public void setUp() throws Exception {
	}
	
	
	private ConsumerServerResource createConsumerServerResource(){
		
		ConsumerServerResource consumerServerResource = new ConsumerServerResource();
		
		QPSAlarmSetting qPSAlarmSetting = new QPSAlarmSetting();
		qPSAlarmSetting.setFluctuation(5);
		qPSAlarmSetting.setFluctuationBase(10);
		qPSAlarmSetting.setPeak(50);
		qPSAlarmSetting.setValley(2);

		QPSAlarmSetting sendAlarmSetting = new QPSAlarmSetting();
		sendAlarmSetting.setFluctuation(4);
		sendAlarmSetting.setFluctuationBase(9);
		sendAlarmSetting.setPeak(49);
		sendAlarmSetting.setValley(1);
		
		consumerServerResource.setCreateTime(new Date());
		consumerServerResource.setUpdateTime(new Date());
		
		consumerServerResource.setAckAlarmSetting(qPSAlarmSetting);
		consumerServerResource.setSendAlarmSetting(qPSAlarmSetting);
		
		consumerServerResource.setIp("127.0.0.1");
		consumerServerResource.setHostname("localhost");
		consumerServerResource.setAlarm(Boolean.TRUE);
		
		return consumerServerResource;
		
	}

	@Test
	public void test() {
		
		/*--------------------------------Consumer---------------------------------*/
		ConsumerServerResource consumerServerResource = createConsumerServerResource();
		boolean result = consumerServerResourceService.insert(consumerServerResource);
		Assert.assertTrue(result);
		consumerServerResource.setHostname("10.128.30.19");
		result = consumerServerResourceService.update(consumerServerResource);
		Assert.assertTrue(result);
		ConsumerServerResource consumerServer2 = (ConsumerServerResource) consumerServerResourceService.findByIp("127.0.0.1");
		System.out.println(consumerServer2.toString());
		Assert.assertNotNull(consumerServer2);
		
		ConsumerServerResource defaultConsumerServerResource = createConsumerServerResource();
		defaultConsumerServerResource.setIp("default");
		result = consumerServerResourceService.insert(defaultConsumerServerResource);
		Assert.assertTrue(result);
		ConsumerServerResource consumerServer3 = (ConsumerServerResource) consumerServerResourceService.findByIp("default");
		System.out.println(consumerServer3.toString());
		Assert.assertNotNull(consumerServer3);
		
		int delete = consumerServerResourceService.remove("127.0.0.1");
		Assert.assertEquals(delete, 1);
		
		delete = consumerServerResourceService.remove("default");
		Assert.assertEquals(delete, 1);
	}

}
