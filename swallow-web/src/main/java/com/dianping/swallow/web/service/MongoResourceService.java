package com.dianping.swallow.web.service;

import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.MongoResource;

import java.util.List;


/**
 * @author mingdongli
 *
 * 2015年9月17日下午8:20:46
 */
public interface MongoResourceService extends Observable {
	
	boolean insert(MongoResource mongoResource);

	boolean update(MongoResource mongoResource);
	
	int remove(String catalog);
	
	MongoResource findByIp(String ip);
	
	MongoResource findIdleMongoByType(String mongoType);

	List<MongoResource> findAll(String ... fields);

	Pair<Long, List<MongoResource>> findMongoResourcePage(int offset, int limit);
}
