package com.dianping.swallow.common.internal.monitor;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午7:14:08
 */
public interface Mergeable extends Cloneable{

	void merge(Mergeable merge);
	
	Object clone() throws CloneNotSupportedException;
}
