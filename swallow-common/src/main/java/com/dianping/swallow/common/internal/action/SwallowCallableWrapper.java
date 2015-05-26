package com.dianping.swallow.common.internal.action;

import java.util.concurrent.Callable;

/**
 * @author mengwenchao
 *
 * 2015年5月14日 下午7:49:24
 */
public interface SwallowCallableWrapper<V>{

	
	V doCallable(Callable<V> callable);

}
