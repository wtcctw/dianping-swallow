package com.dianping.swallow.common.internal.dao.impl;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.common.internal.dao.DAO;

/**
 * 支持数据库迁移，在迁移过程中，写入新的db；读取数据需要等待老的db消费完毕
 * @author mengwenchao
 *
 * 2015年11月3日 下午3:30:26
 */
public class ExchangeDaoContainer<T extends DAO<?>> extends AbstractDaoContainer<T> implements Runnable{
	
	private final T oldDao, newDao;
	
	private volatile boolean changeToNew = false;
	
	private int timeoutSeconds = -1;
	
	@SuppressWarnings("unused")
	private ScheduledExecutorService scheduled;

	public ExchangeDaoContainer(T oldDao, T newDao){
		
		this.oldDao = oldDao;
		this.newDao = newDao;
	}

	public ExchangeDaoContainer(T oldDao, T newDao, ScheduledExecutorService scheduled, int timeoutSeconds){
		
		this.oldDao = oldDao;
		this.newDao = newDao;
		this.scheduled = scheduled;
		
		this.timeoutSeconds = timeoutSeconds;
		this.scheduled = scheduled;
		if(logger.isInfoEnabled()){
			logger.info("[ExchangeDaoContainer][begin schedule]" + oldDao + "->" + newDao);
		}
		scheduled.schedule(this, timeoutSeconds, TimeUnit.SECONDS);
	}


	
	@Override
	public T getDao(boolean isRead) {
		
		if(!isRead){
			return newDao;
		}
		
		if(changeToNew){
			return newDao;
		}
		
		return oldDao;
	}
	
	
	public void markUseNew(){
		this.changeToNew = true;
	}

	@Override
	public void run() {
		
		if(logger.isInfoEnabled()){
			logger.info("[run][changeToNew]" + oldDao + "->" + newDao + "," + timeoutSeconds);
		}
		changeToNew = true;
	}

	
	@Override
	public String toString() {
		return oldDao + "=>" + newDao + "," + changeToNew;
	}
}
