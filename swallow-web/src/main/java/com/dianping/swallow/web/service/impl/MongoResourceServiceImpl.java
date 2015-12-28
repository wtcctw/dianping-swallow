package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.common.internal.observer.Observer;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.MongoDao;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MongoResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author mingdongli
 *
 *         2015年9月17日下午8:21:36
 */
@Service("mongoResourceService")
public class MongoResourceServiceImpl extends AbstractSwallowService implements MongoResourceService {

	private static final int BASE_QPX = 5000;

	private static final int MAX_QPX = 7000;

	@Autowired
	private MongoDao mongoDao;

	private List<Observer> observers = new ArrayList<Observer>();

	public void setMongoDao(MongoDao mongoDao) {
		this.mongoDao = mongoDao;
	}

	@Override
	public boolean insert(MongoResource mongoResource) {

		boolean result = mongoDao.insert(mongoResource);
		if(result){
			notifyObserver(mongoResource);
		}
		return result;
	}

	@Override
	public boolean update(MongoResource mongoResource) {

		boolean result =  mongoDao.update(mongoResource);
		if(result){
			notifyObserver(mongoResource);
		}
		return result;
	}

	@Override
	public int remove(String catalop) {

		return mongoDao.remove(catalop);
	}

	@Override
	public MongoResource findByIp(String ip) {

		return mongoDao.findByIp(ip);
	}

	@Override
	public MongoResource findIdleMongoByType(String mongoType) {

		List<MongoResource> mongoResources = mongoDao.findByType(mongoType);
		return loadIdleMongo(mongoResources);

	}

	@Override
	public List<MongoResource> findAll(String... fields) {

		return mongoDao.findAll(fields);
	}

	@Override
	public Pair<Long, List<MongoResource>> findMongoResourcePage(int offset, int limit) {

		return mongoDao.findMongoResourcePage(offset, limit);
	}

	private MongoResource loadIdleMongo(List<MongoResource> list) {

		int baseQps = BASE_QPX;

		while (baseQps <= MAX_QPX) {
			for (MongoResource mongoResource : list) {
				Integer qps = mongoResource.getQps();
				if (qps != null && qps <= baseQps) {
					return mongoResource;
				}
			}
			baseQps += 1000;
		}

		return null;
	}

	private void notifyObserver(MongoResource mongoResource){
		for(Observer observer : observers){
			observer.update(this, mongoResource);
		}
	}

	@Override
	public void addObserver(Observer observer) {

		observers.add(observer);
	}

	@Override
	public void removeObserver(Observer observer) {

		observers.remove(observer);
	}
}
