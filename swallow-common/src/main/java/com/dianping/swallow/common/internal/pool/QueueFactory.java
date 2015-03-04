package com.dianping.swallow.common.internal.pool;

import java.util.concurrent.BlockingQueue;

/**
 * @author mengwenchao
 *
 * 2015年3月2日 下午6:02:41
 */
public interface QueueFactory {
	
	BlockingQueue<Runnable>  createQueue();
}
