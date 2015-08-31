package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.IpQueryDto;
import com.dianping.swallow.web.model.resource.IpResource;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午11:27:36
 */
public interface IpResourceService {

	boolean insert(IpResource ipResource);

	boolean update(IpResource ipResource);
	
	int remove(String ip);
	
	List<IpResource> findByIp(String ... ips);

	Pair<Long, List<IpResource>> findByApplication(IpQueryDto ipQueryDto);
	
	IpResource find(IpQueryDto ipQueryDto);
	
	List<IpResource> findAll(String ... fields);

	IpResource findDefault();
	
	Pair<Long, List<IpResource>> findIpResourcePage(IpQueryDto baseDto);
}
