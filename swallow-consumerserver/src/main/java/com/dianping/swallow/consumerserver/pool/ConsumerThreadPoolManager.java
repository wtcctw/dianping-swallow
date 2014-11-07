package com.dianping.swallow.consumerserver.pool;

import java.util.concurrent.ExecutorService;

import com.dianping.swallow.common.internal.lifecycle.Disposable;

/**
 * @author mengwenchao
 *
 * 2014年11月5日 下午4:27:25
 */
public interface ConsumerThreadPoolManager extends Disposable{
	
	/**
	 * 处理greet，ack等事件
	 * @return
	 */
	ExecutorService getServiceHandlerThreadPool();
	
	/**
	 * 读取mongo数据线程池
	 * @return
	 */
	ExecutorService getRetrieverThreadPool();
	

	/**
	 * 读取发送消息线程池
	 * @return
	 */
	ExecutorService getSendMessageThreadPool();
}
