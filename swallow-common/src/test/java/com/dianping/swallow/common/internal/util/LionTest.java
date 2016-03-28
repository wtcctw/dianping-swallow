package com.dianping.swallow.common.internal.util;

import org.junit.Test;

import com.dianping.lion.Environment;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;

/**
 * @author mengwenchao
 *
 * 2015年11月4日 下午7:10:19
 */
public class LionTest implements ConfigChange {
	
	
	@Test
	public void testLion() throws InterruptedException{
		
		Environment.setSwimlaneFallback(false);
		ConfigCache configCache = ConfigCache.getInstance();

		System.out.println(configCache.getProperty("swallow.topiccfg.LoadTestTopic-0"));
		
		configCache.addChange(this);
	}

	@Override
	public void onChange(String arg0, String arg1) {
		System.out.println(arg0 + ":" + arg1);
		
	}
}
