package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.lion.client.ConfigChange;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.model.resource.TopicResource;


/**
 * @author mingdongli
 *
 * 2015年8月18日下午7:12:18
 */
public interface TopicResourceService extends ConfigChange{
	
	boolean insert(TopicResource topicResource);

	boolean update(TopicResource topicResource);

	int remove(String topic);

	Pair<Long, List<TopicResource>> findByTopics(TopicQueryDto topicQueryDto);

	TopicResource findByTopic(String topic);
	
	Pair<Long, List<TopicResource>> findByServer(TopicQueryDto topicQueryDto);

	Pair<Long, List<TopicResource>> find(TopicQueryDto topicQueryDto);

	TopicResource findDefault();
	
	List<TopicResource> findAll();
	
	Pair<Long, List<TopicResource>> findTopicResourcePage(TopicQueryDto topicQueryDto);
	
	Map<String, Set<String>> loadCachedTopicToWhiteList();

	Map<String, Set<String>> loadCachedTopicToConsumerServer();
	
	TopicResource buildTopicResource(String topic);
}
