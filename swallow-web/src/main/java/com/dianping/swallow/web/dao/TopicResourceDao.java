package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.TopicResource;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午7:19:24
 */
public interface TopicResourceDao extends Dao{

	boolean insert(TopicResource topicResource);

	boolean update(TopicResource topicResource);

	int remove(String topic);
	
	long count();

	Pair<Long, List<TopicResource>> find(int offset, int limit, String topic, String producerI);

	Pair<Long, List<TopicResource>> findByServer(int offset, int limit, String producerIp);
	
	Pair<Long, List<TopicResource>> findByAdministrator(int offset, int limit, String administrator);
	
	Pair<Long, List<TopicResource>> findByTopics(int offset, int limit, String ... topics);

	TopicResource findByTopic(String topic);

	TopicResource findById(String id);

	TopicResource findDefault();
	
	List<TopicResource> findAll();
	
	Pair<Long, List<TopicResource>> findTopicResourcePage(int offset, int limti);
}
