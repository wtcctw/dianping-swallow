package com.dianping.swallow.common.internal.lifecycle;

/**
 * @author mengwenchao
 *
 * 2015年6月19日 下午6:43:59
 */
public interface Ordered {
	
	public static final int FIRST = Integer.MIN_VALUE;
	
	public static final int LAST = Integer.MAX_VALUE;

	int getOrder();
}
