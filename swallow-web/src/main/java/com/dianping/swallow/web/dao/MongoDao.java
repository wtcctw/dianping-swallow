package com.dianping.swallow.web.dao;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.MongoResource;

import java.util.List;


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

	List<MongoResource> findByType(String mongoType);

	List<MongoResource> findAll(String ... fields);

	Pair<Long, List<MongoResource>> findMongoResourcePage(int offset, int limit);
}
