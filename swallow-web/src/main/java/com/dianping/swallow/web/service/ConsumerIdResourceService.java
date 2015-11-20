package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao.ConsumerIdParam;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午10:29:38
 */
public interface ConsumerIdResourceService {

	boolean insert(ConsumerIdResource consumerIdResource);

	boolean update(ConsumerIdResource consumerIdResource);

	int remove(String topic, String consumerid);
	
	Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdParam consumerIdParam);

	List<ConsumerIdResource> findByTopic(String topic);
	
	Pair<Long, List<ConsumerIdResource>> find(ConsumerIdParam  consumerIdParam);
	
	List<ConsumerIdResource> findAll(String ...fields );

	Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdParam consumerIdParam);
	
	ConsumerIdResource buildConsumerIdResource(String topic, String consumerId);

	ConsumerIdResource findByConsumerIdAndTopic(String topic, String consumerId);
	
	long countInactive();
}
