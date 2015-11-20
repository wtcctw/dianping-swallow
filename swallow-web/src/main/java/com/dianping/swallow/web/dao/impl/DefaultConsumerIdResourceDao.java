package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import jodd.util.StringUtil;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *         <p/>
 *         2015年8月11日上午10:28:12
 */
@Component
public class DefaultConsumerIdResourceDao extends AbstractWriteDao implements ConsumerIdResourceDao {

    private static final String CONSUMERIDRESOURCE_COLLECTION = "CONSUMERID_RESOURCE";

    public static final String CONSUMERID = "consumerId";

    private static final String TOPIC = "topic";

    public static final String CONSUMERIPS = "consumerIpInfos.ip";

    private static final String ACTIVE = "consumerIpInfos.active";

    private static final String DEFAULT = "default";

    private static final int SIZE_THRESHOLD = 100;

    @Override
    public boolean insert(ConsumerIdResource consumerIdResource) {

        try {
            mongoTemplate.save(consumerIdResource, CONSUMERIDRESOURCE_COLLECTION);
            return true;
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("[insert] error when save producer server stats data " + consumerIdResource, e);
            }
        }
        return false;
    }

    @Override
    public boolean update(ConsumerIdResource consumerIdResource) {

        return insert(consumerIdResource);
    }

    @Override
    public int remove(String topic, String consumerid) {

        Query query = new Query(Criteria.where(CONSUMERID).is(consumerid).andOperator(Criteria.where(TOPIC).is(topic)));
        WriteResult result = mongoTemplate.remove(query, ConsumerIdResource.class, CONSUMERIDRESOURCE_COLLECTION);
        return result.getN();
    }

    @Override
    public long count() {

        Query query = new Query();
        return mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);
    }

    @Override
    public ConsumerIdResource findByConsumerIdAndTopic(String topic, String consumerId) {

        Query query = new Query(Criteria.where(CONSUMERID).is(consumerId).andOperator(Criteria.where(TOPIC).is(topic)));
        ConsumerIdResource consumerIdResource = mongoTemplate.findOne(query, ConsumerIdResource.class,
                CONSUMERIDRESOURCE_COLLECTION);

        return consumerIdResource;
    }

    @Override
    public Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdParam consumerIdParam) {

        String topic = consumerIdParam.getTopic();
        String[] topics = topic.split(",");
        int offset = consumerIdParam.getOffset();
        int limit = consumerIdParam.getLimit();

        Query query = new Query();

        List<Criteria> criterias = new ArrayList<Criteria>();
        for (String t : topics) {
            criterias.add(Criteria.where(TOPIC).is(t));
        }

        query.addCriteria(Criteria.where(TOPIC).exists(true)
                .orOperator(criterias.toArray(new Criteria[criterias.size()])));

        Long size = mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);

        query.skip(offset).limit(limit);
        List<ConsumerIdResource> consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class,
                CONSUMERIDRESOURCE_COLLECTION);

        return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResources);
    }

    public List<ConsumerIdResource> findByTopic(String topic){
        Query query = new Query(Criteria.where(TOPIC).is(topic));
        List<ConsumerIdResource> consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class,
                CONSUMERIDRESOURCE_COLLECTION);

        return consumerIdResources;
    }

    @Override
    public Pair<Long, List<ConsumerIdResource>> find(ConsumerIdParam consumerIdParam) {

        String topic = consumerIdParam.getTopic();
        String consumerId = consumerIdParam.getConsumerId();
        String consumerIp = consumerIdParam.getConsumerIp();
        boolean inactive = consumerIdParam.isInactive();

        Query query = new Query();
        String[] topics = null;
        int topicsLength = 0;
        if (StringUtil.isNotBlank(topic)) {
            topics = topic.split(",");
            topicsLength = topics.length;
        }

        if (StringUtil.isNotBlank(consumerId)) {
            query.addCriteria(Criteria.where(CONSUMERID).is(consumerId));
        }
        if (StringUtil.isNotBlank(consumerIp)) {
            query.addCriteria(Criteria.where(CONSUMERIPS).is(consumerIp));
        }

        long size = -1;
        if (!inactive) {
            if (topics != null && topicsLength == 1) {
                query.addCriteria(Criteria.where(TOPIC).is(topics[0]));
            }
            query.addCriteria(Criteria.where(ACTIVE).is(inactive));
            size = mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);
        } else {
            if (topics != null) {
                if (topicsLength <= SIZE_THRESHOLD) {
                    List<Criteria> criterias = new ArrayList<Criteria>();
                    for (String t : topics) {
                        criterias.add(Criteria.where(TOPIC).is(t));
                    }

                    query.addCriteria(Criteria.where(TOPIC).exists(true)
                            .orOperator(criterias.toArray(new Criteria[criterias.size()])));
                }
            }
        }

        if (size < 0) {
            if (topics != null && topicsLength > SIZE_THRESHOLD) {
                size = mongoTemplate.count(new Query(), CONSUMERIDRESOURCE_COLLECTION);
            } else {
                size = mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);
            }
        }

        int offset = consumerIdParam.getOffset();
        int limit = consumerIdParam.getLimit();

        query.skip(offset).limit(limit)
                .with(new Sort(new Sort.Order(Direction.ASC, TOPIC), new Sort.Order(Direction.ASC, CONSUMERID)));
        List<ConsumerIdResource> consumerIdResource = mongoTemplate.find(query, ConsumerIdResource.class,
                CONSUMERIDRESOURCE_COLLECTION);

        return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResource);
    }

    @Override
    public List<ConsumerIdResource> findAll(String... fields) {

        List<ConsumerIdResource> consumerIdResources;

        if (fields.length == 0) {
            consumerIdResources = mongoTemplate.findAll(ConsumerIdResource.class, CONSUMERIDRESOURCE_COLLECTION);
        } else {
            Query query = new Query();
            for (String field : fields) {
                query.fields().include(field);
            }
            consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class, CONSUMERIDRESOURCE_COLLECTION);
        }

        return consumerIdResources;
    }

    @Override
    public ConsumerIdResource findDefault() {

        Query query = new Query(Criteria.where(CONSUMERID).is(DEFAULT).andOperator(Criteria.where(TOPIC).is(DEFAULT)));
        ConsumerIdResource consumerIdResource = mongoTemplate.findOne(query, ConsumerIdResource.class,
                CONSUMERIDRESOURCE_COLLECTION);
        return consumerIdResource;
    }

    @Override
    public Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdParam consumerIdParam) {

        Query query = new Query();
        int offset = consumerIdParam.getOffset();
        int limit = consumerIdParam.getLimit();

        query.skip(offset).limit(limit)
                .with(new Sort(new Sort.Order(Direction.ASC, TOPIC), new Sort.Order(Direction.ASC, CONSUMERID)));
        List<ConsumerIdResource> consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class,
                CONSUMERIDRESOURCE_COLLECTION);
        Long size = this.count();
        return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResources);
    }

    @Override
    public long countInactive() {
        Query query = new Query(Criteria.where(ACTIVE).is(Boolean.FALSE));
        query.fields().include(ACTIVE);
        List<ConsumerIdResource> cnsumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class, CONSUMERIDRESOURCE_COLLECTION);

        long result = 0;
        int size = cnsumerIdResources.size();
        ConsumerIdResource consumerIdResource;
        List<IpInfo> ipInfos;
        int ipInfoSize;
        for (int i = 0; i < size; ++i) {
            consumerIdResource = cnsumerIdResources.get(i);
            ipInfos = consumerIdResource.getConsumerIpInfos();
            ipInfoSize = ipInfos.size();
            for (int j = 0; j < ipInfoSize; ++j) {
                if (!ipInfos.get(j).isActive()) {
                    ++result;
                }
            }
        }

        return result;
    }

}
