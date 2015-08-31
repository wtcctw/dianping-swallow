package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.ConsumerIdQueryDto;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午10:07:00
 */
public interface ConsumerIdResourceDao extends Dao{

	boolean insert(ConsumerIdResource consumerIdResource);

	boolean update(ConsumerIdResource consumerIdResource);
	
	int remove(String topic, String consumerid);
	
	long count();

	List<ConsumerIdResource> findByConsumerId(String consumerid);

	Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdQueryDto consumerIdQueryDto);
	
	 Pair<Long, List<ConsumerIdResource>> findByConsumerIp(ConsumerIdQueryDto consumerIdQueryDto);

	 Pair<Long, List<ConsumerIdResource>> find(ConsumerIdQueryDto  consumerIdQueryDto);
	
	List<ConsumerIdResource> findAll(String ...fields );

	ConsumerIdResource findDefault();
	
	Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdQueryDto consumerIdQueryDto);
}
