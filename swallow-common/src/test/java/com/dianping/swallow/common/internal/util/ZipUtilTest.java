package com.dianping.swallow.common.internal.util;

import java.io.IOException;

import org.junit.Test;

import com.dianping.swallow.AbstractTest;


/**
 * @author mengwenchao
 *
 * 2015年12月1日 下午4:01:25
 */
public class ZipUtilTest extends AbstractTest{
	
	private int testTimes = 100;
	
	@Test
	public void testZip() throws IOException{
		
		long start = System.currentTimeMillis();
		
		for(int i=0; i < testTimes ; i++){
			
			String content = randomString(1024 * 1024);
			String result = ZipUtil.zip(content);
			logger.info("[testZip][zip ratio]" + (double)content.length()/result.length());
		}
		
		long end  = System.currentTimeMillis();
		
		logger.info("[testZip][average]" + (end - start)/ testTimes);
		
		
	}

}
