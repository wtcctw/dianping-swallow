package com.dianping.swallow.test.load;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年7月21日 上午10:12:31
 */
public class BitMarkerTest {
	
	private final int total = 1000000;
	
	private final AtomicInteger count = new AtomicInteger();
	
	private final int concurrentCount = 10;
	
	private final ExecutorService executors = Executors.newFixedThreadPool(concurrentCount);
	
	@Test
	public void testCurrentBitMarker() throws InterruptedException{
		
		final BitMarker marker = new BitMarker();

		for(int i=0;i<concurrentCount;i++){
			executors.execute(new Runnable() {
				
				@Override
				public void run() {
					while(true){
						
						int current = count.incrementAndGet();
						if(current > total){
							break;
						}
						
						marker.mark(current);
					}
				}
			});
		}

		executors.shutdown();
		executors.awaitTermination(30, TimeUnit.SECONDS);

		Assert.assertEquals(total, marker.noRepetCount());
		Assert.assertEquals(total, marker.realCount());
		
	}

}
