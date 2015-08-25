package com.dianping.swallow.test.other;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.internal.config.LionUtil;
import com.dianping.swallow.common.internal.config.impl.LionUtilImpl;


/**
 * @author mengwenchao
 *
 * 2015年4月27日 上午10:15:13
 */
public class LionTest implements ConfigChange {

	@Test
	public void testLion() throws LionException, InterruptedException{
		
        final ConfigCache cc = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
        
        cc.addChange(this);

        
        new Thread(new Runnable(){

			@Override
			public void run() {
				while(true){
					System.out.println("==" + cc.getProperty("swallow.topiccfg.topic1") + "==");
					try {
						TimeUnit.SECONDS.sleep(5);
					} catch (InterruptedException e) {
					}
				}
			}
        }).start();
        
        TimeUnit.SECONDS.sleep(1000);
	}

	@Override
	public void onChange(String key, String value) {
		System.out.println(key + ":" + value);
	}

	
	@Test
	public void testGet() throws InterruptedException{
		
		ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
		final LionUtil util = new LionUtilImpl();
		
		ses.scheduleWithFixedDelay(new Runnable() {
			
			@Override
			public void run() {
				
				System.out.println("==" + util.getCfgs("swallow.topiccfg.topic").keySet() + "==");
			}
		}, 0, 5, TimeUnit.SECONDS);
		
		TimeUnit.SECONDS.sleep(1000);
	}
}
