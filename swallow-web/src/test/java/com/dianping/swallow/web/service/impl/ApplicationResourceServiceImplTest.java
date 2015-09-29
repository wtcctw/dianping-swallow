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
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.resource.ApplicationResource;
import com.dianping.swallow.web.service.ApplicationResourceService;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午2:37:58
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class ApplicationResourceServiceImplTest {
	
	@Resource(name = "applicationResourceService")
	private ApplicationResourceService applicationResourceService;

	@Before
	public void setUp() throws Exception {
		
	}
	
	private ApplicationResource createApplicationResource() {

		ApplicationResource applicationResource = new ApplicationResource();
		IPDesc iPDesc = new IPDesc();

		iPDesc.setCreateTime(new Date());
		iPDesc.setEmail("lmdyyh@163.com");
		iPDesc.setDpMobile("18795858599");
		iPDesc.setOpEmail("jiaxin.fan@dianping.com");
		iPDesc.setName("tuangou");

		applicationResource.setiPDesc(iPDesc);
		
		applicationResource.setApplication("tuangou");

		return applicationResource;

	}

	@Test
	public void test() {
		
		ApplicationResource applicationResource = createApplicationResource();
		boolean result = applicationResourceService.insert(applicationResource);
		Assert.assertTrue(result);

		Pair<Long, List<ApplicationResource>> applicationResources = applicationResourceService.find(0, 31, "tuangou");
		Assert.assertTrue(applicationResources.getFirst() == 1);

		applicationResource = applicationResources.getSecond().get(0);
		
		applicationResource.setApplication("waimai");
		result = applicationResourceService.update(applicationResource);
		Assert.assertTrue(result);

		int n = applicationResourceService.remove("waimai");
		Assert.assertEquals(n, 1);

	}

}
