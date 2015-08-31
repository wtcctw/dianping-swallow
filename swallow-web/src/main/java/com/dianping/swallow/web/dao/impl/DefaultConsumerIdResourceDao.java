package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import jodd.util.StringUtil;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.ConsumerIdQueryDto;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年8月11日上午10:28:12
 */
@Component
public class DefaultConsumerIdResourceDao extends AbstractWriteDao implements ConsumerIdResourceDao {

	private static final String CONSUMERIDRESOURCE_COLLECTION = "CONSUMERID_RESOURCE";

	private static final String CONSUMERID = "consumerId";

	private static final String TOPIC = "topic";

	private static final String CONSUMERIPS = "consumerIps";

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
	public Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdQueryDto consumerIdQueryDto) {

		String topic = consumerIdQueryDto.getTopic();
		int offset = consumerIdQueryDto.getOffset();
		int limit = consumerIdQueryDto.getLimit();

		Query query = new Query(Criteria.where(TOPIC).is(topic));

		Long size = mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);

		query.skip(offset).limit(limit);
		List<ConsumerIdResource> consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);

		return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResources);
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> find(ConsumerIdQueryDto consumerIdQueryDto) {

		String topic = consumerIdQueryDto.getTopic();
		String consumerId = consumerIdQueryDto.getConsumerId();
		String consumerIp = consumerIdQueryDto.getConsumerIp();

		Query query = new Query();

		if (StringUtil.isNotBlank(topic)) {
			query.addCriteria(Criteria.where(TOPIC).is(topic));
		}
		if (StringUtil.isNotBlank(consumerId)) {
			query.addCriteria(Criteria.where(CONSUMERID).is(consumerId));
		}
		if (StringUtil.isNotBlank(consumerIp)) {
			query.addCriteria(Criteria.where(CONSUMERIPS).is(consumerIp));
		}

		List<ConsumerIdResource> consumerIdResource = mongoTemplate.find(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);

		int offset = consumerIdQueryDto.getOffset();
		int limit = consumerIdQueryDto.getLimit();

		query.skip(offset).limit(limit);
		long size = mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);

		return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResource);
	}

	@Override
	public List<ConsumerIdResource> findAll(String... fields) {

		List<ConsumerIdResource> consumerIdResources = new ArrayList<ConsumerIdResource>();

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

		Query query = new Query(Criteria.where(CONSUMERID).is(DEFAULT));
		ConsumerIdResource consumerIdResource = mongoTemplate.findOne(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);
		return consumerIdResource;
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdQueryDto consumerIdQueryDto) {

		Query query = new Query();
		int offset = consumerIdQueryDto.getOffset();
		int limit = consumerIdQueryDto.getLimit();

		query.skip(offset).limit(limit);
		List<ConsumerIdResource> consumerIdResources = mongoTemplate.find(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);
		Long size = this.count();
		return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResources);
	}

	@Override
	public Pair<Long, List<ConsumerIdResource>> findByConsumerIp(ConsumerIdQueryDto consumerIdQueryDto) {

		int offset = consumerIdQueryDto.getOffset();
		int limit = consumerIdQueryDto.getLimit();
		String consumerIp = consumerIdQueryDto.getConsumerIp();
		Query query = new Query(Criteria.where(CONSUMERIPS).is(consumerIp));

		Long size = mongoTemplate.count(query, CONSUMERIDRESOURCE_COLLECTION);

		query.skip(offset).limit(limit);
		List<ConsumerIdResource> consumerIdResource = mongoTemplate.find(query, ConsumerIdResource.class,
				CONSUMERIDRESOURCE_COLLECTION);

		return new Pair<Long, List<ConsumerIdResource>>(size, consumerIdResource);

	}

}
