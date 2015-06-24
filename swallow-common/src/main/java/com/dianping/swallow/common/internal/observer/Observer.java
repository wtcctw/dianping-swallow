package com.dianping.swallow.common.internal.observer;

/**
 * @author mengwenchao
 *
 * 2015年6月12日 下午2:19:25
 */
public interface Observer{
	
	void update(Observable observable, Object args);
}
