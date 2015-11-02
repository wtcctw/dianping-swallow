package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ApplicationResourceDao;
import com.dianping.swallow.web.model.resource.ApplicationResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.ApplicationResourceService;

/**
 * @author mingdongli
 *
 *         2015年9月29日下午2:33:52
 */
@Service("applicationResourceService")
public class ApplicationResourceServiceImpl extends AbstractSwallowService implements ApplicationResourceService {

	@Autowired
	private ApplicationResourceDao applicationResourceDao;

	@Override
	public boolean insert(ApplicationResource applicationResource) {

		return applicationResourceDao.insert(applicationResource);
	}

	@Override
	public boolean update(ApplicationResource applicationResource) {
		String appName = applicationResource.getApplication();
		List<ApplicationResource> appResources = applicationResourceDao.findByApplication(appName);
		if (appResources != null && !appResources.isEmpty()) {
			applicationResource.setId(appResources.get(0).getId());
			return applicationResourceDao.update(applicationResource);
		} else {
			return insert(applicationResource);
		}
	}

	@Override
	public int remove(String application) {

		return applicationResourceDao.remove(application);
	}

	@Override
	public Pair<Long, List<ApplicationResource>> find(int offset, int limit, String... applications) {

		return applicationResourceDao.find(offset, limit, applications);
	}

	@Override
	public List<ApplicationResource> findByApplication(String... applications) {

		return applicationResourceDao.findByApplication(applications);
	}

	@Override
	public List<ApplicationResource> findAll(String... fields) {

		return applicationResourceDao.findAll(fields);
	}

	@Override
	public Pair<Long, List<ApplicationResource>> findApplicationResourcePage(int offset, int limit) {

		return applicationResourceDao.findApplicationResourcePage(offset, limit);
	}

}
