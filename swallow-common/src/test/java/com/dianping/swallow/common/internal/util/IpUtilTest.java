package com.dianping.swallow.common.internal.util;


import org.junit.Assert;
import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年5月27日 下午2:25:15
 */
public class IpUtilTest {
	
	
	@Test
	public void testGetIp(){
		
		Assert.assertEquals("127.0.0.1", IPUtil.getIp("127.0.0.1:8080"));
		Assert.assertEquals("127.0.0.1", IPUtil.getIp("127.0.0.1"));
		Assert.assertEquals(null, IPUtil.getIp(null));

	}

}
