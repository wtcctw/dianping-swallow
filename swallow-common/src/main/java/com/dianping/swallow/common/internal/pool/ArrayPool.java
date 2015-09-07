package com.dianping.swallow.common.internal.pool;

/**
 * @author mengwenchao
 *
 * 2015年9月6日 下午4:16:18
 */
public interface ArrayPool<T> {
	
	
	T get(int size);
	
	void release(T object);
}
