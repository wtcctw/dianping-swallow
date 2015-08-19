package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.dao.TopicResourceDao;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.TopicResourceService;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午7:34:53
 */
@Service("topicResourceService")
public class TopicResourceServiceImpl extends AbstractSwallowService implements TopicResourceService {

	@Autowired
	private TopicResourceDao topicResourceDao;
	
	@Override
	public boolean insert(TopicResource topicResource) {

		return topicResourceDao.insert(topicResource);
	}

	@Override
	public boolean update(TopicResource topicResource) {

		return topicResourceDao.update(topicResource);
	}

	@Override
	public int remove(String topic) {

		return topicResourceDao.remove(topic);
	}

	@Override
	public TopicResource findByTopic(String topic) {

		return topicResourceDao.findByTopic(topic);
	}

	@Override
	public TopicResource findDefault() {

		return topicResourceDao.findDefault();
	}

	@Override
	public Pair<Long, List<TopicResource>> findTopicResourcePage(TopicQueryDto topicQueryDto) {

		return topicResourceDao.findTopicResourcePage(topicQueryDto);
	}

}
