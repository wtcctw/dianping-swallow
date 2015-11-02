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

import com.dianping.swallow.web.controller.utils.IpInfoUtils;
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.TopicResourceService;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午7:38:34
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class TopicResourceServiceTest {
	
	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Before
	public void setUp() throws Exception {
	}
	
	private TopicResource createTopicResource(){
		
		TopicResource topicResource = new TopicResource();
		
		ProducerBaseAlarmSetting producerBaseAlarmSetting = new ProducerBaseAlarmSetting();
		producerBaseAlarmSetting.setDelay(5000);
		QPSAlarmSetting qPSAlarmSetting = new QPSAlarmSetting();
		qPSAlarmSetting.setFluctuation(7);
		qPSAlarmSetting.setFluctuationBase(50);
		qPSAlarmSetting.setPeak(50);
		qPSAlarmSetting.setValley(3);
		producerBaseAlarmSetting.setQpsAlarmSetting(qPSAlarmSetting);
		
		topicResource.setProducerAlarmSetting(producerBaseAlarmSetting);
		
		topicResource.setCreateTime(new Date());
		topicResource.setUpdateTime(new Date());
		topicResource.setTopic("example");
		topicResource.setAdministrator("mingdong.li");
		topicResource.setConsumerAlarm(Boolean.FALSE);
		topicResource.setProducerAlarm(Boolean.TRUE);
		
		List<String> comsumerids = new ArrayList<String>();
		comsumerids.add("consumeri-01");
		comsumerids.add("consumeri-02");
		comsumerids.add("consumeri-03");
		
		List<String> producerServer = new ArrayList<String>();
		producerServer.add("1.0.0.1");
		producerServer.add("1.0.0.2");
		producerServer.add("1.0.0.3");
		
		List<IpInfo> producerIpInfo = IpInfoUtils.buildIpInfo(producerServer);
		topicResource.setProducerIpInfos(producerIpInfo);
		
		return topicResource;
		
	}

	@Test
	public void test() {
		
		TopicResource topicResource = createTopicResource();
		boolean result = topicResourceService.insert(topicResource);
		Assert.assertTrue(result);
		
		TopicResource producerServer1 =  topicResourceService.findByTopic("example");
		System.out.println("_id is " + producerServer1.getId());
		System.out.println(producerServer1.toString());
		Assert.assertNotNull(producerServer1);
		
		producerServer1.setTopic("example2");
		result = topicResourceService.update(producerServer1);
		Assert.assertTrue(result);
		
		TopicResource defaultTopicResource = createTopicResource();
		defaultTopicResource.setTopic("default");
		result = topicResourceService.insert(defaultTopicResource);
		Assert.assertTrue(result);
		TopicResource producerServer3 = topicResourceService.findByTopic("default");
		System.out.println(producerServer3.toString());
		Assert.assertNotNull(producerServer3);
		
	}

}
