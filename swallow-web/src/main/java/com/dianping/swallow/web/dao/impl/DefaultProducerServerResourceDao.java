package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ProducerServerResourceDao;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午4:33:53
 */
@Component
public class DefaultProducerServerResourceDao extends AbstractWriteDao implements ProducerServerResourceDao {

	private static final String PRODUCERSERVERRESOURCE_COLLECTION = "PRODUCER_SERVER_RESOURCE";

	@Override
	public boolean insert(ProducerServerResource producerServerResource) {

		try {
			mongoTemplate.save(producerServerResource, PRODUCERSERVERRESOURCE_COLLECTION);
			return true;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("[insert] error when save producer server stats data " + producerServerResource, e);
			}
		}
		return false;
	}

	@Override
	public boolean update(ProducerServerResource producerServerResource) {

		return insert(producerServerResource);
	}

	@Override
	public int remove(String ip) {

		Query query = new Query(Criteria.where(IP).is(ip));
		WriteResult result = mongoTemplate.remove(query, ProducerServerResource.class,
				PRODUCERSERVERRESOURCE_COLLECTION);
		return result.getN();
	}

	@Override
	public ProducerServerResource findByIp(String ip) {

		Query query = new Query(Criteria.where(IP).is(ip));
		ProducerServerResource producerServerResource = mongoTemplate.findOne(query, ProducerServerResource.class,
				PRODUCERSERVERRESOURCE_COLLECTION);
		return producerServerResource;
	}

	@Override
	public ProducerServerResource findByHostname(String hostname) {

		Query query = new Query(Criteria.where(HOSTNAME).is(hostname));
		ProducerServerResource producerServerResource = mongoTemplate.findOne(query, ProducerServerResource.class,
				PRODUCERSERVERRESOURCE_COLLECTION);
		return producerServerResource;
	}

	@Override
	public Pair<Long, List<ProducerServerResource>> findProducerServerResourcePage(int offset, int limit) {

		Query query = new Query();
		
		query.skip(offset).limit(limit);
		List<ProducerServerResource> producerServerResources = mongoTemplate.find(query, ProducerServerResource.class,
				PRODUCERSERVERRESOURCE_COLLECTION);
		Long size = this.count();
		return new Pair<Long, List<ProducerServerResource>>(size, producerServerResources);
	}

	@Override
	public long count() {

		Query query = new Query();
		return mongoTemplate.count(query, PRODUCERSERVERRESOURCE_COLLECTION);
	}

	@Override
	public ProducerServerResource findDefault() {

		Query query = new Query(Criteria.where(IP).is(DEFAULT));
		ProducerServerResource producerServerResource = mongoTemplate.findOne(query, ProducerServerResource.class,
				PRODUCERSERVERRESOURCE_COLLECTION);
		return producerServerResource;
	}

}
