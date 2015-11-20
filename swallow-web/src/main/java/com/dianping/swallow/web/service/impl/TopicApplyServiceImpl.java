package com.dianping.swallow.web.service.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.TopicApplyDao;
import com.dianping.swallow.web.model.resource.TopicApplyResource;
import com.dianping.swallow.web.service.TopicApplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author   mingdongli
 * 15/11/20  上午11:45.
 */
@Service("topicApplyService")
public class TopicApplyServiceImpl implements TopicApplyService{

    @Autowired
    private TopicApplyDao topicApplyDao;

    @Override
    public boolean insert(TopicApplyResource topicApplyResource) {
        return topicApplyDao.insert(topicApplyResource);
    }

    @Override
    public List<TopicApplyResource> find(String topic, int offset, int limit) {
        return topicApplyDao.find(topic, offset, limit);
    }

    @Override
    public Pair<Long, List<TopicApplyResource>> findTopicApplyResourcePage(int offset, int limit) {
        return topicApplyDao.findTopicApplyResourcePage(offset, limit);
    }
}
