package com.dianping.swallow.common.internal.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

import com.dianping.swallow.AbstractTest;

/**
 * @author mengwenchao
 *
 * 2015年5月27日 下午2:25:15
 */
public class IpUtilTest extends AbstractTest{
	
	
	@Test
	public void testGetIp(){
		
		Assert.assertEquals("127.0.0.1", IPUtil.getIp("127.0.0.1:8080"));
		Assert.assertEquals("127.0.0.1", IPUtil.getIp("127.0.0.1"));
		Assert.assertEquals(null, IPUtil.getIp(null));

	}

	@Test
	public void testGetSimpleLogIp(){
		
		Assert.assertEquals("0.1:8080", IPUtil.simpleLogIp("127.0.0.1:8080"));;
		Assert.assertEquals("0.1:8080", simpleLogIp1("127.0.0.1:8080"));;
		
		final int count = 1 << 25;
		Long time1 = System.currentTimeMillis();
		for(int i=0;i<count;i++){
			IPUtil.simpleLogIp("127.0.0.1:8080");
		}
		Long time2 = System.currentTimeMillis();
		
		if(logger.isInfoEnabled()){
			logger.info("simpleLogIp:" + (time2 - time1));
		}
		for(int i=0;i<count;i++){
			simpleLogIp1("127.0.0.1:8080");
		}
		Long time3 = System.currentTimeMillis();
		if(logger.isInfoEnabled()){
			logger.info("simpleLogIp1:" + (time3 - time2));
		}
	}
	
	
	   private static final Pattern simpleLogIpPattern = Pattern.compile("\\d+\\.\\d+\\.(.*)");

	   public static String simpleLogIp1(String ipPort) {
		   
		   if(ipPort == null){
			   return null;
		   }
		   ipPort = ipPort.trim();
		   Matcher matcher = simpleLogIpPattern.matcher(ipPort);
		   if(!matcher.matches()){
			   return ipPort;
		   }
		   return matcher.group(1);
	   }

}
