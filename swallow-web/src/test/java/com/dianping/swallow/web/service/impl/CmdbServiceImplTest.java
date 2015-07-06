package com.dianping.swallow.web.service.impl;


import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.HttpService;

/**
 * 
 * @author qiyin
 *
 */
public class CmdbServiceImplTest {
	
	static CmdbServiceImpl cmdbService = null;
	
	@BeforeClass
	public static void beforeClass(){
		cmdbService = new CmdbServiceImpl();
		HttpService httpService = new HttpServiceImpl();
		cmdbService.setHttpService(httpService);
	}

	@Test
	public void getIpDescTest() {
		IPDesc ipDesc = cmdbService.getIpDesc("10.2.9.15");
		Assert.assertEquals("10.2.9.15", ipDesc.getIp());
		Assert.assertEquals("swallow-producer", ipDesc.getName());
		Assert.assertEquals("wenchao.meng@dianping.com", ipDesc.getEmail());
		Assert.assertEquals(null, ipDesc.getOpEmail());
		Assert.assertEquals("范佳星", ipDesc.getOpManager());
		Assert.assertEquals("13472696059", ipDesc.getOpMobile());
		Assert.assertEquals("孟文超", ipDesc.getDpManager());
		Assert.assertEquals("18721147511", ipDesc.getDpMobile());
	}

}
