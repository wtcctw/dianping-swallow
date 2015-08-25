package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
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
	
	List<ConsumerIdResource> findByConsumerId(String consumerid);

	Pair<Long, List<ConsumerIdResource>> findByTopic(TopicQueryDto topicQueryDto);
	
	 Pair<Long, List<ConsumerIdResource>> findByConsumerIp(TopicQueryDto  topicQueryDto);

	ConsumerIdResource find(String topic, String consumerid);

	ConsumerIdResource findDefault();
	
	Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(BaseDto baseDto);
}
