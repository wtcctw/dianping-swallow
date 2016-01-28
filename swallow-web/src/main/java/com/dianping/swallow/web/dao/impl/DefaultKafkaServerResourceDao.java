package com.dianping.swallow.web.dao.impl;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.KafkaServerResourceDao;
import com.dianping.swallow.web.model.resource.KafkaServerResource;
import com.mongodb.WriteResult;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Author   mingdongli
 * 16/1/28  下午2:59.
 */
@Component
public class DefaultKafkaServerResourceDao extends AbstractWriteDao implements KafkaServerResourceDao {

    private static final String KAFKARESOURCE_COLLECTION = "KAFKA_RESOURCE";

    public static final String GROUPNAME = "groupName";

    private static final String GROUPID = "groupId";

    public static final String IP = "ip";

    @Override
    public boolean insert(KafkaServerResource kafkaServerResource) {
        try {
            mongoTemplate.save(kafkaServerResource, KAFKARESOURCE_COLLECTION);
            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("[insert] error when save kafka data " + kafkaServerResource, e);
            }
        }
        return false;
    }

    @Override
    public boolean update(KafkaServerResource kafkaServerResource) {
        return insert(kafkaServerResource);
    }

    @Override
    public int remove(String ip) {
        Query query = new Query(Criteria.where(IP).is(ip));
        WriteResult result = mongoTemplate.remove(query, KafkaServerResource.class, KAFKARESOURCE_COLLECTION);
        return result.getN();
    }

    @Override
    public long count() {
        Query query = new Query();
        return mongoTemplate.count(query, KAFKARESOURCE_COLLECTION);
    }

    @Override
    public KafkaServerResource findByIp(String ip) {
        Query query = new Query(Criteria.where(IP).is(ip));
        KafkaServerResource kafkaServerResource = mongoTemplate.findOne(query, KafkaServerResource.class, KAFKARESOURCE_COLLECTION);
        return kafkaServerResource;
    }

    @Override
    public List<KafkaServerResource> findByGroupName(String groupName) {
        Query query = new Query(Criteria.where(GROUPNAME).is(groupName));
        List<KafkaServerResource> kafkaServerResources = mongoTemplate.find(query, KafkaServerResource.class, KAFKARESOURCE_COLLECTION);
        return kafkaServerResources;
    }

    @Override
    public List<KafkaServerResource> findByGroupId(long groupId) {

        Query query = new Query(Criteria.where(GROUPID).is(groupId));
        List<KafkaServerResource> kafkaServerResources = mongoTemplate.find(query, KafkaServerResource.class, KAFKARESOURCE_COLLECTION);
        return kafkaServerResources;
    }

    @Override
    public List<KafkaServerResource> findAll() {
        return mongoTemplate.findAll(KafkaServerResource.class, KAFKARESOURCE_COLLECTION);
    }

    @Override
    public Pair<Long, List<KafkaServerResource>> findKafkaServerResourcePage(int offset, int limit) {
        Query query = new Query();

        query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Sort.Direction.ASC, IP)));
        List<KafkaServerResource> ipResources = mongoTemplate.find(query, KafkaServerResource.class, KAFKARESOURCE_COLLECTION);
        Long size = this.count();
        return new Pair<Long, List<KafkaServerResource>>(size, ipResources);
    }

    @Override
    public int getMaxGroupId() {
        Query query = new Query();

        query.skip(0).limit(1).with(new Sort(new Sort.Order(Sort.Direction.DESC, GROUPID)));
        KafkaServerResource kafkaServerResource = mongoTemplate.findOne(query, KafkaServerResource.class,
                KAFKARESOURCE_COLLECTION);
        if (kafkaServerResource == null) {
            return 0;
        }
        return kafkaServerResource.getGroupId();
    }
}
