package com.dianping.swallow.common.internal.config.impl;

import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年6月15日 下午2:45:25
 */
public class LionUtilImplTest {
	
	
	private LionUtilImpl lionUtil = new LionUtilImpl(2L);
	
	@Test
	public void testCfgs(){
		
		System.out.println(lionUtil.getCfgs(""));
		
	}

}
