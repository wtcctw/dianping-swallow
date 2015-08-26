package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.controller.dto.ConsumerIdQueryDto;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.ConsumerIdResourceService;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午10:34:28
 */
@Service("consumerIdResourceService")
public class ConsumerIdResourceServiceImpl extends AbstractSwallowService implements ConsumerIdResourceService {

	@Autowired
	private ConsumerIdResourceDao consumerIdResourceDao;
	
	@Override
	public boolean insert(ConsumerIdResource consumerIdResource) {

		return consumerIdResourceDao.insert(consumerIdResource);
	}

	@Override
	public boolean update(ConsumerIdResource consumerIdResource) {

		return consumerIdResourceDao.update(consumerIdResource);
	}

	@Override
	public int remove(String topic, String consumerid) {

		return consumerIdResourceDao.remove(topic, consumerid);
	}

	@Override
	public List<ConsumerIdResource> findByConsumerId(String consumerid) {

		return consumerIdResourceDao.findByConsumerId(consumerid);
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdQueryDto consumerIdQueryDto) {

		return consumerIdResourceDao.findByTopic(consumerIdQueryDto);
	}

	@Override
	public ConsumerIdResource find(String topic, String consumerid) {

		return consumerIdResourceDao.find(topic, consumerid);
	}
	
	@Override
	public List<ConsumerIdResource> findAll(String ...fields ){
		
		return consumerIdResourceDao.findAll(fields);
	}

	@Override
	public ConsumerIdResource findDefault() {

		return consumerIdResourceDao.findDefault();
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdQueryDto consumerIdQueryDto) {

		return consumerIdResourceDao.findConsumerIdResourcePage(consumerIdQueryDto);
	}
	
	@Override
	public  Pair<Long, List<ConsumerIdResource>> findByConsumerIp(ConsumerIdQueryDto consumerIdQueryDto){
		
		return consumerIdResourceDao.findByConsumerIp(consumerIdQueryDto);
	}

}
