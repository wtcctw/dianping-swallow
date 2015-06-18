package com.dianping.swallow.common.internal.observer.impl;

import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.observer.Observer;

/**
 * @author mengwenchao
 *
 * 2015年6月12日 下午2:20:11
 */
public class AbstractObservable implements Observable{
	
	private List<Observer> observers = new LinkedList<Observer>();

	protected Logger logger = Logger.getLogger(getClass());

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
