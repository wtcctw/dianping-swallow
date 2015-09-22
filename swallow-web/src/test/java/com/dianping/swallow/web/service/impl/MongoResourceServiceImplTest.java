package com.dianping.swallow.web.service.impl;

import javax.annotation.Resource;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.MongoResourceService;


/**
 * @author mingdongli
 *
 * 2015年9月21日下午12:48:07
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class MongoResourceServiceImplTest {
	
	@Resource(name = "mongoResourceService")
	private MongoResourceService mongoResourceService;
	
	
	@Before
	public void setUp() throws Exception {
		
//		private List<MongoResource> mongoResources = new ArrayList<MongoResource>();
//		MongoResource mongoResource = new MongoResource();
//		mongoResource.setDisk(27.3043003082275391f);
//		mongoResource.setQps(1440);
//		mongoResource.setMongoType(MongoType.GENERAL);
//		
//		mongoResources.add(mongoResource);
//		mongoResource.setDisk(90.4207992553710938f);
//		mongoResource.setQps(895);
//
//		mongoResources.add(mongoResource);
//		mongoResource.setDisk(89.3732986450195312f);
//		mongoResource.setQps(2222);
//
//		mongoResources.add(mongoResource);
//		mongoResource.setDisk(33.5612983703613281f);
//		mongoResource.setQps(4247);
//		mongoResources.add(mongoResource);
		
	}

	@Test
	public void test() {
		
		MongoResource mr =  mongoResourceService.findIdleMongoByType(MongoType.GENERAL);
		Float disk = mr.getDisk();
		System.out.println("disk is " + disk);
		Assert.assertTrue(disk == 27.3043003082275391f);

		mr =  mongoResourceService.findIdleMongoByType(MongoType.PAYMENT);
		disk = mr.getDisk();
		System.out.println("disk is " + disk);
		Assert.assertTrue(disk == 33.5612983703613281f);

		mr =  mongoResourceService.findIdleMongoByType(MongoType.SEARCH);
		disk = mr.getDisk();
		System.out.println("disk is " + disk);
		Assert.assertTrue(disk == 27.3043003082275391f);
	}

}
