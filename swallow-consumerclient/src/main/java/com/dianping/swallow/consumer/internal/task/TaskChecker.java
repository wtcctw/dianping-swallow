package com.dianping.swallow.consumer.internal.task;

/**
 * @author mengwenchao
 *
 * 2015年3月30日 下午5:37:21
 */
public interface TaskChecker extends Runnable{

	void addTask(ConsumerTask task);
	
	void removeTask(ConsumerTask task);

	void close();

	int getAlertCount();
}
