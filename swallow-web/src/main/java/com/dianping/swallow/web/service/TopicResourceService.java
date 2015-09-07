package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dianping.lion.client.ConfigChange;
import com.dianping.swallow.web.common.Pair;
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

	Pair<Long, List<TopicResource>> findByTopics(int offset, int limit, String ... topics);

	TopicResource findByTopic(String topic);
	
	Pair<Long, List<TopicResource>> findByServer(int offset, int limit, String producerIp);

	Pair<Long, List<TopicResource>> findByAdministrator(int offset, int limit, String administrator);

	Pair<Long, List<TopicResource>> find(int offset, int limit, String topic, String producerIp);

	TopicResource findDefault();
	
	List<TopicResource> findAll();
	
	Pair<Long, List<TopicResource>> findTopicResourcePage(int offset, int limit);
	
	Map<String, Set<String>> loadCachedTopicToWhiteList();

	Map<String, Set<String>> loadCachedTopicToConsumerServer();
	
	TopicResource buildTopicResource(String topic);
}
