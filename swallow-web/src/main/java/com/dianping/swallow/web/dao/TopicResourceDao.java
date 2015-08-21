package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
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

	TopicResource findByTopic(String topic);

	TopicResource findById(String id);

	TopicResource findDefault();
	
	Pair<Long, List<TopicResource>> findTopicResourcePage(TopicQueryDto topicQueryDto);
}
