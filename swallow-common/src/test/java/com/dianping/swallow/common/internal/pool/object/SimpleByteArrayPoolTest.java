package com.dianping.swallow.common.internal.pool.object;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;


/**
 * @author mengwenchao
 *
 * 2015年9月6日 下午4:50:23
 */
public class SimpleByteArrayPoolTest{
	
	private SimpleByteArrayPool pool = new SimpleByteArrayPool();
	
	@Test
	public void testGet(){
		
		int start = 0;
		for(int length : SimpleByteArrayPool.lengths){
			
			for(int i = start + 1;i<=length;i++){
				
				byte [] buff = pool.get(i);
				Assert.assertEquals(length, buff.length);
				pool.release(buff);
				
			}
			
			start = length;
			
		}
	}
	
	@Test
	public void testGetIndex(){
	
		int start = 0;
		for(int i = 0;i < SimpleByteArrayPool.lengths.length ; i++){
			
			int currentLength = SimpleByteArrayPool.lengths[i];
			for(int j = start+1; j <= currentLength; j++){
				Assert.assertEquals(i, pool.getIndex(j));
			}
			start = currentLength;
		}
	}

	
	@Test(expected = IllegalStateException.class)
	public void testException(){
		
		pool.get(10);
		pool.get(10);
		
	}
	
	private ExecutorService executors = Executors.newCachedThreadPool();
	private CountDownLatch latch;

	@Test
	public void testEffi() throws InterruptedException{
		
		
		final int count = 1 << 24;
		final int size = 1024;
		int concurrentCount = 10;
		
		long start = System.currentTimeMillis();
		latch = new CountDownLatch(concurrentCount);
	
		for(int i=0;i< concurrentCount;i++){
			executors.execute(new Runnable(){
	
				@Override
				public void run() {
					try{
						for(int i=0;i<count;i++){
							@SuppressWarnings("unused")
							byte []tmp = new byte[size];
						}
					}finally{
						latch.countDown();
					}
				}
				
			});
		}
		latch.await();
		long end = System.currentTimeMillis();
		System.out.println(end - start);
		
		
		final ThreadLocal<SimpleByteArrayPool> threadLocal = new ThreadLocal<SimpleByteArrayPool>(){
			@Override
			protected SimpleByteArrayPool initialValue() {
				return new SimpleByteArrayPool();
			}
		};
		
		start = System.currentTimeMillis();
		latch = new CountDownLatch(concurrentCount);
		
		for(int i=0;i< concurrentCount;i++){
			executors.execute(new Runnable(){
	
				@Override
				public void run() {
					try{
						for(int i=0;i<count;i++){
							SimpleByteArrayPool pool = threadLocal.get(); 
							byte []tmp = pool.get(size);
							pool.release(tmp);
						}
					}finally{
						latch.countDown();
					}
				}
				
			});
		}
		latch.await();
		end = System.currentTimeMillis();
		System.out.println(end - start);
	}

}
