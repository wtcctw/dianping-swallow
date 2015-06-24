package com.dianping.swallow.test.other;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;


/**
 * @author mengwenchao
 *
 * 2015年4月27日 上午10:15:13
 */
public class LionTest implements ConfigChange {

	@Test
	public void testLion() throws LionException, InterruptedException{
		
        ConfigCache cc = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
        System.out.println(cc.getProperty("swallow.topiccfg.topic1"));
        
        cc.addChange(this);

        
        
        TimeUnit.SECONDS.sleep(1000);
	}

	@Override
	public void onChange(String key, String value) {
		System.out.println(key + ":" + value);
	}
	
}
