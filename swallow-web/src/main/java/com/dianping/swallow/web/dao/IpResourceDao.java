package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.IpResource;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午11:14:22
 */
public interface IpResourceDao extends Dao{

	boolean insert(IpResource ipResource);

	boolean update(IpResource ipResource);
	
	long count();

	Pair<Long, List<IpResource>> findByIp(int offset, int limit, boolean admin, String ... ips);

	Pair<Long, List<IpResource>> findByApplication(int offset, int limit, String application);
	
	Pair<Long, List<IpResource>> find(int offset, int limit, String application, String ...ips);
	
	List<IpResource> findAll(String ... fields);

	IpResource findDefault();
	
	Pair<Long, List<IpResource>> findIpResourcePage(int offset, int limit);
	
	List<IpResource> findByIps(String ... ips);
	
	List<IpResource> findByIp(String ip);
	
	IpResource findByIp(String ip, String appName);
}
