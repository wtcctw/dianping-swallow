package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
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
	
	List<IpResource> findByIp(String ip);

	Pair<Long, List<IpResource>> findByIpType(IpQueryDto ipQueryDto);

	IpResource findDefault();
	
	Pair<Long, List<IpResource>> findIpResourcePage(BaseDto baseDto);
}
