package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;


/**
 * @author mingdongli
 *
 * 2015年9月17日下午8:04:41
 */
public interface MongoDao extends Dao{

	boolean insert(MongoResource mongoResource);

	boolean update(MongoResource mongoResource);
	
	int remove(String catalog);
	
	long count();

	MongoResource findByIp(String ip); 

	MongoResource findByCatalog(String catalog);
	
	List<MongoResource> findByType(MongoType mongoType);

	List<MongoResource> findAll(String ... fields);

	MongoResource findDefault();
	
	Pair<Long, List<MongoResource>> findMongoResourcePage(int offset, int limit);
}
