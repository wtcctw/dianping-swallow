package com.dianping.swallow.common.internal.pool;

/**
 * @author mengwenchao
 *
 * 2015年11月17日 上午10:06:09
 */
public interface ObjectFactory<T> {
	
	T createObject();
	
	Class<T> getObjectClass();

}
