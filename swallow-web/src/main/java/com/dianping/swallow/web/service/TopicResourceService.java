package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.model.resource.TopicResource;


/**
 * @author mingdongli
 *
 * 2015年8月18日下午7:12:18
 */
public interface TopicResourceService {
	
	boolean insert(TopicResource topicResource);

	boolean update(TopicResource topicResource);

	int remove(String topic);

	TopicResource findByTopic(String topic);

	TopicResource findDefault();
	
	Pair<Long, List<TopicResource>> findTopicResourcePage(TopicQueryDto topicQueryDto);
}
