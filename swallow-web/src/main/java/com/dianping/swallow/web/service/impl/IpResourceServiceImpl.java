package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.IpResourceDao;
import com.dianping.swallow.web.model.resource.IpResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.IpResourceService;

/**
 * @author mingdongli
 *
 *         2015年8月11日上午11:28:39
 */
@Service("ipResourceService")
public class IpResourceServiceImpl extends AbstractSwallowService implements IpResourceService {

	@Autowired
	private IpResourceDao ipResourceDao;

	@Override
	public boolean insert(IpResource ipResource) {

		return ipResourceDao.insert(ipResource);
	}

	@Override
	public boolean update(IpResource ipResource) {

		return ipResourceDao.update(ipResource);
	}

	@Override
	public int remove(String ip) {

		return ipResourceDao.remove(ip);
	}

	@Override
	public Pair<Long, List<IpResource>> findByIp(int offset, int limit, boolean admin, String... ips) {

		return ipResourceDao.findByIp(offset, limit, admin, ips);
	}

	@Override
	public List<IpResource> findByIp(String ip) {
		List<IpResource> ipResources = ipResourceDao.findByIp(ip);

		return ipResources;
	}

	@Override
	public Pair<Long, List<IpResource>> findByApplication(int offset, int limit, String application) {

		return ipResourceDao.findByApplication(offset, limit, application);
	}

	@Override
	public Pair<Long, List<IpResource>> find(int offset, int limit, String application, String... ips) {

		return ipResourceDao.find(offset, limit, application, ips);
	}

	@Override
	public List<IpResource> findAll(String... fields) {

		return ipResourceDao.findAll(fields);
	}

	@Override
	public IpResource findDefault() {

		return ipResourceDao.findDefault();
	}

	@Override
	public Pair<Long, List<IpResource>> findIpResourcePage(int offset, int limit) {

		return ipResourceDao.findIpResourcePage(offset, limit);
	}

	@Override
	public List<IpResource> findByIps(String... ips) {
		return ipResourceDao.findByIps(ips);
	}

}
