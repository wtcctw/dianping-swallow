package com.dianping.swallow.web.util;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 
 * @author qiyin
 *
 *         2015年10月9日 上午10:59:58
 */
public class ScheduledExecutorServiceTest {

	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(2,
			ThreadFactoryUtils.getThreadFactory("TEST"));

	public static void main(String[] args) {
		System.out.println("isShutdown: " + scheduled.isShutdown() + "  isTerminated: " + scheduled.isTerminated());
		scheduled.scheduleAtFixedRate(new Runnable(){

			@Override
			public void run() {
				scheduledTask();
			}
			
		}, 0, 10, TimeUnit.SECONDS);
		
		System.out.println("isShutdown: " + scheduled.isShutdown() + "  isTerminated: " + scheduled.isTerminated());
		try {
			Thread.sleep(15*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		scheduled.shutdown();
		scheduled.shutdownNow();
		scheduled.shutdown();
		scheduled.shutdown();
		System.out.println("isShutdown: " + scheduled.isShutdown() + "  isTerminated: " + scheduled.isTerminated());
		try {
			Thread.sleep(15*1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("isShutdown: " + scheduled.isShutdown() + "  isTerminated: " + scheduled.isTerminated());
	}

	public static void scheduledTask() {
		System.out.println("do scheduled task.");

	}

}
