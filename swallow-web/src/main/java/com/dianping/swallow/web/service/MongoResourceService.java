package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;


/**
 * @author mingdongli
 *
 * 2015年9月17日下午8:20:46
 */
public interface MongoResourceService {
	
	boolean insert(MongoResource mongoResource);

	boolean update(MongoResource mongoResource);
	
	int remove(String catalog);
	
	long count();

	MongoResource findByIp(String ip);
	
	MongoResource findByCatalog(String catalog);

	MongoResource findIdleMongoByType(MongoType mongoType);

	List<MongoResource> findAll(String ... fields);

	MongoResource findDefault();
	
	Pair<Long, List<MongoResource>> findMongoResourcePage(int offset, int limit);
}
