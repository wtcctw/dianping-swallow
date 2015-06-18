package com.dianping.swallow.common.internal.observer;

/**
 * @author mengwenchao
 *
 * 2015年6月12日 下午2:19:37
 */
public interface Observable {

	void addObserver(Observer observer);
	
	void removeObserver(Observer observer);
	
}
