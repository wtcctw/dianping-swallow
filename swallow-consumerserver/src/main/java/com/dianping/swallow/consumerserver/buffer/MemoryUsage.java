package com.dianping.swallow.consumerserver.buffer;

/**
 * @author mengwenchao
 *
 * 2015年3月4日 上午10:12:55
 */
public interface MemoryUsage {
	
	void setLimit(long bytes);
	
	long getLimit();

	void increase(long bytes);
	
	void decrease(long bytes);
	
	boolean isFull();
}
