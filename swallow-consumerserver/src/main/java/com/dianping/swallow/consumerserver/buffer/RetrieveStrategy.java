package com.dianping.swallow.consumerserver.buffer;

/**
 * 记录每次读取数据的情况，判断下次读取是否必要
 * 取数据策略
 * @author mengwenchao
 *
 * 2014年11月6日 下午12:01:37
 */
public interface RetrieveStrategy {

	/**
	 * 判断此次是否需要取数据
	 * @return
	 */
	boolean isRetrieve();
	
	/**
	 * 记录本次数据读取数量
	 * @param count
	 */
	void retrieved(int count);
	
	void increaseMessageCount();
	
	void increaseMessageCount(int count);

	void decreaseMessageCount();

	void decreaseMessageCount(int count);
	
	int messageCount();
	
	/**
	 *任务开始 
	 */
	void beginRetrieve();
	
	/**
	 * 任务结束
	 */
	void endRetrieve();
	
	void offerNewTask();
	boolean canPutNewTask();
}
