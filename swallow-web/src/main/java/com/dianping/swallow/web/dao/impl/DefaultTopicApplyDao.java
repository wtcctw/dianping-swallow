package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.TopicApplyDao;
import com.dianping.swallow.web.model.resource.TopicApplyResource;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author   mingdongli
 * 15/11/20  上午11:34.
 */
@Component
public class DefaultTopicApplyDao extends AbstractWriteDao implements TopicApplyDao{

    private static final String TOPICAPPLY_COLLECTION = "TOPIC_APPLY_RESOURCE";

    private static final String TOPIC = "topic";

    private static final String CTRATE_DATE = "createTime";

    @Override
    public boolean insert(TopicApplyResource topicApplyResource) {
        try {
            mongoTemplate.save(topicApplyResource, TOPICAPPLY_COLLECTION);
            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("[insert] error when save producer server stats data " + topicApplyResource, e);
            }
        }
        return false;
    }

    @Override
    public long count() {
        Query query = new Query();
        return mongoTemplate.count(query, TOPICAPPLY_COLLECTION);
    }

    @Override
    public TopicApplyResource find(String topic) {
        Query query = new Query(Criteria.where(TOPIC).is(topic));
        TopicApplyResource topicApplyResource = mongoTemplate.findOne(query, TopicApplyResource.class, TOPICAPPLY_COLLECTION);
        return topicApplyResource;
    }

    @Override
    public Pair<Long, List<TopicApplyResource>> findTopicApplyResourcePage(int offset, int limit) {
        Query query = new Query();

        query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Sort.Direction.DESC, CTRATE_DATE)));
        List<TopicApplyResource> topicApplyResource = mongoTemplate.find(query, TopicApplyResource.class, TOPICAPPLY_COLLECTION);

        long size = this.count();
        return new Pair<Long, List<TopicApplyResource>>(size, topicApplyResource);
    }
}
