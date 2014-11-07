package com.dianping.swallow.consumerserver.buffer;

/**
 * 记录每次读取数据的情况，判断下次读取是否必要
 * 取数据策略
 * @author mengwenchao
 *
 * 2014年11月6日 下午12:01:37
 */
public interface RetriveStrategy {

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
	
}
