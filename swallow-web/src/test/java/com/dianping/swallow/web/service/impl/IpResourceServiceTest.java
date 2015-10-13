package com.dianping.swallow.web.service.impl;

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
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.service.IpResourceService;

/**
 * @author mingdongli
 *
 *         2015年8月11日上午11:31:36
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:applicationContext.xml" })
@WebAppConfiguration
public class IpResourceServiceTest {

	@Resource(name = "ipResourceService")
	private IpResourceService ipResourceService;

	@Before
	public void setUp() throws Exception {
	}

	private IpResource createIpResource() {

		IpResource ipResource = new IpResource();

		ipResource.setIp("10.1.0.10");
		ipResource.setAlarm(Boolean.TRUE);

		return ipResource;

	}

	@Test
	public void test() {

		IpResource ipResource = createIpResource();
		boolean result = ipResourceService.insert(ipResource);
		Assert.assertTrue(result);

		List<IpResource> ipResources = ipResourceService.findByIp("10.1.0.10");
		Assert.assertNotNull(ipResources);

		ipResource.setIp("127.0.0.1");

		result = ipResourceService.update(ipResource);
		Assert.assertTrue(result);

		Pair<Long, List<IpResource>> pair = new Pair<Long, List<IpResource>>();
		pair = ipResourceService.findByApplication(0, 31, "tuangou");
		Assert.assertNotNull(pair);
		Assert.assertEquals(pair.getFirst(), new Long(1));

		IpResource ipResource1 = createIpResource();
		ipResource1.setIp("default");
		result = ipResourceService.insert(ipResource1);
		Assert.assertTrue(result);

		ipResource1 = ipResourceService.findDefault();
		Assert.assertNotNull(ipResource1);

		int n = ipResourceService.remove("default");
		Assert.assertEquals(n, 1);

		n = ipResourceService.remove("127.0.0.1");
		Assert.assertEquals(n, 1);
	}

}
