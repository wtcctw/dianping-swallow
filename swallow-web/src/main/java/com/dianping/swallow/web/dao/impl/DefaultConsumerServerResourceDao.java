package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ConsumerServerResourceDao;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ServerType;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午6:27:03
 */
@Component
public class DefaultConsumerServerResourceDao extends AbstractWriteDao implements ConsumerServerResourceDao {

	private static final String CONSUMERSERVERRESOURCE_COLLECTION = "CONSUMER_SERVER_RESOURCE";

	private static final String GROUPID = "groupId";

	private static final String TYPE = "type";

	private static final String ACTIVE = "active";

	@Override
	public boolean insert(ConsumerServerResource consumerServerResource) {

		try {
			mongoTemplate.save(consumerServerResource, CONSUMERSERVERRESOURCE_COLLECTION);
			return true;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("[insert] error when save producer server stats data " + consumerServerResource, e);
			}
		}
		return false;
	}

	@Override
	public boolean update(ConsumerServerResource consumerServerResource) {

		return insert(consumerServerResource);
	}

	@Override
	public int remove(String ip) {

		Query query = new Query(Criteria.where(IP).is(ip));
		WriteResult result = mongoTemplate.remove(query, ConsumerServerResource.class,
				CONSUMERSERVERRESOURCE_COLLECTION);
		return result.getN();
	}

	@Override
	public long count() {

		Query query = new Query();
		return mongoTemplate.count(query, CONSUMERSERVERRESOURCE_COLLECTION);
	}

	@Override
	public ConsumerServerResource findByIp(String ip) {

		Query query = new Query(Criteria.where(IP).is(ip));
		ConsumerServerResource consumerServerResource = mongoTemplate.findOne(query, ConsumerServerResource.class,
				CONSUMERSERVERRESOURCE_COLLECTION);
		return consumerServerResource;
	}

	@Override
	public List<ConsumerServerResource> findByGroupId(long groupId) {

		Query query = new Query(Criteria.where(GROUPID).is(groupId));
		List<ConsumerServerResource> consumerServerResources = mongoTemplate.find(query, ConsumerServerResource.class,
				CONSUMERSERVERRESOURCE_COLLECTION);
		return consumerServerResources;
	}

	@Override
	public ConsumerServerResource findDefault() {

		Query query = new Query(Criteria.where(IP).is(DEFAULT));
		ConsumerServerResource consumerServerResource = mongoTemplate.findOne(query, ConsumerServerResource.class,
				CONSUMERSERVERRESOURCE_COLLECTION);
		return consumerServerResource;
	}

	@Override
	public Pair<Long, List<ConsumerServerResource>> findConsumerServerResourcePage(int offset, int limit) {

		Query query = new Query();

		query.skip(offset).limit(limit)
				.with(new Sort(new Sort.Order(Direction.ASC, GROUPID), new Sort.Order(Direction.ASC, HOSTNAME)));
		List<ConsumerServerResource> consumerServerResources = mongoTemplate.find(query, ConsumerServerResource.class,
				CONSUMERSERVERRESOURCE_COLLECTION);
		Long size = this.count();
		return new Pair<Long, List<ConsumerServerResource>>(size, consumerServerResources);
	}

	@Override
	public List<ConsumerServerResource> findAll() {

		return mongoTemplate.findAll(ConsumerServerResource.class, CONSUMERSERVERRESOURCE_COLLECTION);
	}

	@Override
	public ConsumerServerResource loadIdleConsumerServer() {

		Query query = new Query(Criteria.where(TYPE).is(ServerType.MASTER)
				.andOperator(Criteria.where(ACTIVE).is(Boolean.TRUE)));

		query.with(new Sort(new Sort.Order(Direction.ASC, QPS)));
		ConsumerServerResource consumerServerResource = mongoTemplate.findOne(query, ConsumerServerResource.class,
				CONSUMERSERVERRESOURCE_COLLECTION);

		return consumerServerResource;
	}

	@Override
	public int getMaxGroupId() {
		Query query = new Query();

		query.skip(0).limit(1).with(new Sort(new Sort.Order(Direction.DESC, GROUPID)));
		ConsumerServerResource consumerServerResource = mongoTemplate.findOne(query, ConsumerServerResource.class,
				CONSUMERSERVERRESOURCE_COLLECTION);
		if (consumerServerResource == null) {
			return 0;
		}
		return consumerServerResource.getGroupId();
	}

}
