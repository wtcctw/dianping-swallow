package com.dianping.swallow.common.internal.util;

import org.junit.Test;

import com.dianping.swallow.AbstractTest;

/**
 * @author mengwenchao
 *
 * 2015年12月1日 下午2:30:56
 */
public class CommonUtilsTest extends AbstractTest{
	
	@Test
	public void testRealCpuCount(){
		
		if(logger.isInfoEnabled()){
			logger.info(String.valueOf(CommonUtils.getCpuCount()));
		}
	}

}
