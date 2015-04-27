package com.dianping.swallow.test.other;

import org.junit.Test;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;


/**
 * @author mengwenchao
 *
 * 2015年4月27日 上午10:15:13
 */
public class LionTest {

	@Test
	public void testLion() throws LionException{
		
        ConfigCache cc = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
        System.out.println(cc.getProperty("test"));
        System.out.println(cc.getProperty("swallow.monitor.exclude.topic"));

	}
	
}
