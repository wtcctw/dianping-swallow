package com.dianping.swallow.web.alarmer;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


/**
 * 
 * @author qiyin
 *
 *         2015年10月15日 上午11:37:23
 */
public interface TaskManager {

	Future<?> submit(Runnable command);

	ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit);

	ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long period, TimeUnit unit);

}
