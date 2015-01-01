package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.MongoDao;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.MongoResourceService;

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

	public void setMongoDao(MongoDao mongoDao) {
		this.mongoDao = mongoDao;
	}

	@Override
	public boolean insert(MongoResource mongoResource) {

		return mongoDao.insert(mongoResource);
	}

	@Override
	public boolean update(MongoResource mongoResource) {

		return mongoDao.update(mongoResource);
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

}
