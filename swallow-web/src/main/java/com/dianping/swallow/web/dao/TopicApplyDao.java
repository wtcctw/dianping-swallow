package com.dianping.swallow.web.dao;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.TopicApplyResource;

import java.util.List;

/**
 * Author   mingdongli
 * 15/11/20  上午11:14.
 */
public interface TopicApplyDao extends Dao{

    boolean insert(TopicApplyResource topicApplyResource);

    long count();

    List<TopicApplyResource> find(String topic, int offset, int limit);

    Pair<Long, List<TopicApplyResource>> findTopicApplyResourcePage(int offset, int limit);
}
