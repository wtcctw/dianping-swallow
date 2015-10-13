package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.ApplicationResource;


/**
 * @author mingdongli
 *
 * 2015年9月29日下午2:28:46
 */
public interface ApplicationResourceService {
	
	boolean insert(ApplicationResource applicationResource);

	boolean update(ApplicationResource applicationResource);
	
	int remove(String application);
	
	Pair<Long, List<ApplicationResource>> find(int offset, int limit, String ... application);

	List<ApplicationResource> findByApplication(String ... application);
	
	List<ApplicationResource> findAll(String ... fields);

	Pair<Long, List<ApplicationResource>> findApplicationResourcePage(int offset, int limit);
}
