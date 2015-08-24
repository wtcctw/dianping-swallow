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

import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.ProducerServerResourceService;

/**
 * @author mingdongli
 *
 * 2015年8月10日下午5:40:28
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ProducerServerResourceServiceTest {

	@Resource(name = "producerServerResourceService")
	private ProducerServerResourceService producerServerResourceService;

	
	@Before
	public void setUp() throws Exception {
		
	}
	
	private ProducerServerResource createProducerServerResource(){
	
		ProducerServerResource producerServerResource = new ProducerServerResource();
		
		QPSAlarmSetting qPSAlarmSetting = new QPSAlarmSetting();
		qPSAlarmSetting.setFluctuation(5);
		qPSAlarmSetting.setFluctuationBase(50);
		qPSAlarmSetting.setPeak(40);
		qPSAlarmSetting.setValley(4);
		
		producerServerResource.setCreateTime(new Date());
		producerServerResource.setUpdateTime(new Date());
		
		producerServerResource.setSendAlarmSetting(qPSAlarmSetting);
		producerServerResource.setIp("127.0.0.1");
		producerServerResource.setHostname("localhost");
		
		List<String> topicWhiteList = new ArrayList<String>();
		topicWhiteList.add("consumeri-01");
		topicWhiteList.add("consumeri-02");
		topicWhiteList.add("consumeri-03");
		
		producerServerResource.setTopicWhiteList(topicWhiteList);
		
		return producerServerResource;
		
	}

	@Test
	public void test() {
		
		/*--------------------------------Producer---------------------------------*/
		ProducerServerResource producerServerResource = createProducerServerResource();
		boolean result = producerServerResourceService.insert(producerServerResource);
		Assert.assertTrue(result);
		producerServerResource.setHostname("10.128.30.19");
		result = producerServerResourceService.update(producerServerResource);
		Assert.assertTrue(result);
		ProducerServerResource producerServer1 = (ProducerServerResource) producerServerResourceService.findByHostname("10.128.30.19");
		System.out.println(producerServer1.toString());
		Assert.assertNotNull(producerServer1);
		ProducerServerResource producerServer2 = (ProducerServerResource) producerServerResourceService.findByIp("127.0.0.1");
		System.out.println(producerServer2.toString());
		Assert.assertNotNull(producerServer2);
		
		ProducerServerResource defaultProducerServerResource = createProducerServerResource();
		defaultProducerServerResource.setIp("default");
		result = producerServerResourceService.insert(defaultProducerServerResource);
		Assert.assertTrue(result);
		ProducerServerResource producerServer3 = (ProducerServerResource) producerServerResourceService.findByIp("default");
		System.out.println(producerServer3.toString());
		Assert.assertNotNull(producerServer3);
		
		
		int delete = producerServerResourceService.remove("127.0.0.1");
		Assert.assertEquals(delete, 1);

		delete = producerServerResourceService.remove("default");
		Assert.assertEquals(delete, 1);
		
		
	}

}
