package com.dianping.swallow.common.internal.action;

import java.util.concurrent.Callable;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午5:06:41
 */
public interface SwallowCallableWrapper<V> {

	
	V doCallable(Callable<V> callable) throws Exception;
}
