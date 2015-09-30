package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.ApplicationResource;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午2:12:26
 */
public interface ApplicationResourceDao {
	
	boolean insert(ApplicationResource applicationResource);

	boolean update(ApplicationResource applicationResource);
	
	int remove(String application);
	
	long count();

	Pair<Long, List<ApplicationResource>> find(int offset, int limit, String ... applications);
	
	List<ApplicationResource> findByApplication(String ... applications);
	
	List<ApplicationResource> findAll(String ... fields);

	ApplicationResource findDefault();
	
	Pair<Long, List<ApplicationResource>> findApplicationResourcePage(int offset, int limit);
}
