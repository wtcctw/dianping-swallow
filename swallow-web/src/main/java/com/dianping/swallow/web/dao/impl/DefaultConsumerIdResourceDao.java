package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.BaseDto;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.mongodb.WriteResult;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午10:28:12
 */
@Component
public class DefaultConsumerIdResourceDao extends AbstractWriteDao implements ConsumerIdResourceDao {

	private static final String CONSUMERIDRESOURCE_COLLECTION = "CONSUMERID_RESOURCE";

	private static final String CONSUMERID = "consumerId";

	private static final String TOPIC = "topic";
	
	private static final String CONSUMERIP = "consumerIp";

	private static final String DEFAULT = "default";

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
	public List<ConsumerIdResource> findByConsumerId(String consumerid) {

		Query query = new Query(Criteria.where(CONSUMERID).is(consumerid));
		List<ConsumerIdResource> consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);

		return consumerIdResources;
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findByTopic(TopicQueryDto topicQueryDto) {
		
		String topic = topicQueryDto.getTopic();
		int offset = topicQueryDto.getOffset();
		int limit = topicQueryDto.getLimit();
		
		Query query = new Query(Criteria.where(TOPIC).is(topic));
		
		Long size = mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);
		
		query.skip(offset).limit(limit);
		List<ConsumerIdResource> consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);

		return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResources);
	}

	@Override
	public ConsumerIdResource find(String topic, String consumerid) {

		Query query = new Query(Criteria.where(TOPIC).is(topic).andOperator(Criteria.where(CONSUMERID).is(consumerid)));
		ConsumerIdResource consumerIdResource = mongoTemplate.findOne(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);
		
		return consumerIdResource;
	}

	@Override
	public ConsumerIdResource findDefault() {

		Query query = new Query(Criteria.where(CONSUMERID).is(DEFAULT));
		ConsumerIdResource consumerIdResource = mongoTemplate.findOne(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);
		return consumerIdResource;
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(BaseDto baseDto) {

		Query query = new Query();
		int offset = baseDto.getOffset();
		int limit = baseDto.getLimit();
		
		query.skip(offset).limit(limit);
		List<ConsumerIdResource> consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);
		Long size = this.count();
		return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResources);
	}
	
	@Override
	public Pair<Long, List<ConsumerIdResource>> findByConsumerIp(TopicQueryDto  topicQueryDto){
		
		int offset = topicQueryDto.getOffset();
		int limit = topicQueryDto.getLimit();
		String consumerIp = topicQueryDto.getConsumerIp();
		Query query = new Query(Criteria.where(CONSUMERIP).is(consumerIp));
		
		Long size = mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);
		
		query.skip(offset).limit(limit);
		List<ConsumerIdResource> consumerIdResource = mongoTemplate.find(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);

		return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResource);
		
	}

}
