package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.MongoDao;
import com.dianping.swallow.web.model.resource.MongoResource;
import com.dianping.swallow.web.model.resource.MongoType;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年9月17日下午8:09:10
 */
@Component
public class DefaultMongoDao extends AbstractWriteDao implements MongoDao {

	private static final String MONGORESOURCE_COLLECTION = "MONGO_RESOURCE";

	private static final String CATALOG = "catalog";

	public static final String TYPE = "mongoType";

	public static final String IP = "ip";

	public static final String DISK = "disk";

	public static final String QPS = "qps";

	public static final String DEFAULT = "Default";

	@Override
	public boolean insert(MongoResource mongoResource) {
		try {
			mongoTemplate.save(mongoResource, MONGORESOURCE_COLLECTION);
			return true;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("[insert] error when save mongo data " + mongoResource, e);
			}
		}
		return false;
	}

	@Override
	public boolean update(MongoResource mongoResource) {

		return insert(mongoResource);
	}

	@Override
	public int remove(String catalog) {
		Query query = new Query(Criteria.where(CATALOG).is(catalog));
		WriteResult result = mongoTemplate.remove(query, MongoResource.class, MONGORESOURCE_COLLECTION);
		return result.getN();
	}

	@Override
	public long count() {
		Query query = new Query();
		return mongoTemplate.count(query, MONGORESOURCE_COLLECTION);
	}

	@Override
	public MongoResource findByIp(String ip) {

		Query query = new Query(Criteria.where(IP).is(ip).andOperator(Criteria.where(CATALOG).ne(DEFAULT)));
		MongoResource mongoResource = mongoTemplate.findOne(query, MongoResource.class, MONGORESOURCE_COLLECTION);
		return mongoResource;
	}

	@Override
	public List<MongoResource> findByType(MongoType mongoType) {

		Query query = new Query(Criteria.where(TYPE).is(mongoType));
		List<MongoResource> mongoResource = mongoTemplate.find(query, MongoResource.class, MONGORESOURCE_COLLECTION);
		return mongoResource;
	}

	@Override
	public List<MongoResource> findAll(String... fields) {
		List<MongoResource> mongoResources = new ArrayList<MongoResource>();

		if (fields.length == 0) {
			mongoResources = mongoTemplate.findAll(MongoResource.class, MONGORESOURCE_COLLECTION);
		} else {
			Query query = new Query();
			for (String field : fields) {
				query.fields().include(field);
			}
			mongoResources = mongoTemplate.find(query, MongoResource.class, MONGORESOURCE_COLLECTION);
		}

		return mongoResources;
	}

	@Override
	public Pair<Long, List<MongoResource>> findMongoResourcePage(int offset, int limit) {
		Query query = new Query();

		query.skip(offset)
				.limit(limit)
				.with(new Sort(new Sort.Order(Direction.ASC, TYPE), new Sort.Order(Direction.ASC, DISK),
						new Sort.Order(Direction.ASC, QPS), new Sort.Order(Direction.ASC, CATALOG)));
		List<MongoResource> ipResources = mongoTemplate.find(query, MongoResource.class, MONGORESOURCE_COLLECTION);
		Long size = this.count();
		return new Pair<Long, List<MongoResource>>(size, ipResources);
	}

}
