package com.dianping.swallow.web.alarmer.impl;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.dianping.swallow.web.MockTest;
import com.dianping.swallow.web.alarmer.impl.MongoConfigAlarmer.MongoAddress;

public class MongoConfigAlarmerTest extends MockTest {

	@Before
	public void before() throws Exception {
	}

	@Test
	public void parseMongoUrlTest() {
		String mongoUrl = "mongodb://10.1.115.11:27018,10.1.115.12:27018";
		MongoConfigAlarmer alamerAlarmer = new MongoConfigAlarmer();
		List<MongoAddress> addresses = alamerAlarmer.parseMongoUrl(mongoUrl);
		Assert.assertTrue(addresses.get(0).getHost().equals("10.1.115.11"));

		Assert.assertTrue(addresses.get(0).getPort() == 27018);
		Assert.assertTrue(addresses.get(1).getHost().equals("10.1.115.12"));

		Assert.assertTrue(addresses.get(1).getPort() == 27018);
	}

}
