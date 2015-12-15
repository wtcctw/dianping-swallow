package com.dianping.swallow.common.internal.observer.impl;

import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.observer.Observer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.LinkedList;
import java.util.List;

/**
 * @author mengwenchao
 *
 * 2015年6月12日 下午2:20:11
 */
public class AbstractObservable implements Observable{
	
	private List<Observer> observers = new LinkedList<Observer>();

	protected Logger logger = LogManager.getLogger(getClass());

	@Override
	public synchronized void addObserver(Observer observer) {

		observers.add(observer);
	}

	@Override
	public synchronized void removeObserver(Observer observer) {
		
		observers.remove(observer);
		
	}
	
	protected synchronized void updateObservers(Object args){
		
		for(Observer observer : observers){
			try{
				observer.update(this, args);
			}catch(Throwable th){
				logger.error("[updateObservers]" + observer , th);
			}
		}
	}

}
