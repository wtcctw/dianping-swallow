package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.dao.ConsumerServerResourceDao;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ServerResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.ConsumerServerResourceService;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午6:25:07
 */
@Service("consumerServerResourceService")
public class ConsumerServerResourceServiceImpl extends AbstractSwallowService implements ConsumerServerResourceService {

	@Autowired
	private ConsumerServerResourceDao consumerServerResourceDao;
	
	@Override
	public boolean insert(ConsumerServerResource consumerServerResource) {
		
		return consumerServerResourceDao.insert(consumerServerResource);
	}

	@Override
	public boolean update(ConsumerServerResource consumerServerResource) {

		return consumerServerResourceDao.update(consumerServerResource);
	}
	
	@Override
	public int remove(String ip){
		
		return consumerServerResourceDao.remove(ip);
	}

	@Override
	public Pair<Long, List<ConsumerServerResource>> findConsumerServerResourcePage(int offset, int limit) {

		return consumerServerResourceDao.findConsumerServerResourcePage(offset, limit);
	}

	@Override
	public ServerResource findByIp(String ip) {

		return consumerServerResourceDao.findByIp(ip);
	}

	@Override
	public ServerResource findByHostname(String hostname) {

		return consumerServerResourceDao.findByHostname(hostname);
	}

	@Override
	public ServerResource findDefault() {

		return consumerServerResourceDao.findDefault();
	}

}
