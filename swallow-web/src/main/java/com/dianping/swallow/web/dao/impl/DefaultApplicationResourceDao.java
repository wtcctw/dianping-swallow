package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ApplicationResourceDao;
import com.dianping.swallow.web.model.resource.ApplicationResource;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年9月29日下午2:15:59
 */
@Component
public class DefaultApplicationResourceDao extends AbstractWriteDao implements ApplicationResourceDao {

	private static final String APPLICATION_COLLECTION = "APPLICATION_RESOURCE";

	private static final String APPLICATION = "application";

	private static final String DEFAULT = "default";

	@Override
	public boolean insert(ApplicationResource applicationResource) {
		try {
			mongoTemplate.save(applicationResource, APPLICATION_COLLECTION);
			return true;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("[insert] error when save producer server stats data " + applicationResource, e);
			}
		}
		return false;
	}

	@Override
	public boolean update(ApplicationResource applicationResource) {

		return insert(applicationResource);
	}

	@Override
	public int remove(String application) {

		Query query = new Query(Criteria.where(APPLICATION).is(application));
		WriteResult result = mongoTemplate.remove(query, ApplicationResource.class, APPLICATION_COLLECTION);
		return result.getN();
	}

	@Override
	public long count() {

		Query query = new Query();
		return mongoTemplate.count(query, APPLICATION_COLLECTION);
	}

	@Override
	public Pair<Long, List<ApplicationResource>> find(int offset, int limit, String... applications) {
		Query query = new Query();
		List<Criteria> criterias = new ArrayList<Criteria>();
		if (applications.length > 0) {
			for (String app : applications) {
				criterias.add(Criteria.where(APPLICATION).is(app));
			}
			query.addCriteria(Criteria.where(APPLICATION).exists(true)
					.orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}

		long size = mongoTemplate.count(query, APPLICATION_COLLECTION);

		query.skip(offset).limit(limit);
		List<ApplicationResource> applicationResources = mongoTemplate.find(query, ApplicationResource.class,
				APPLICATION_COLLECTION);

		return new Pair<Long, List<ApplicationResource>>(size, applicationResources);
	}

	@Override
	public List<ApplicationResource> findAll(String... fields) {

		List<ApplicationResource> applicationResources = new ArrayList<ApplicationResource>();

		if (fields.length == 0) {
			applicationResources = mongoTemplate.findAll(ApplicationResource.class, APPLICATION_COLLECTION);
		} else {
			Query query = new Query();
			for (String field : fields) {
				query.fields().include(field);
			}
			applicationResources = mongoTemplate.find(query, ApplicationResource.class, APPLICATION_COLLECTION);
		}

		return applicationResources;
	}

	@Override
	public List<ApplicationResource> findByApplication(String... applications) {
		
		Query query = new Query();
		List<Criteria> criterias = new ArrayList<Criteria>();
		if (applications.length > 0) {
			for (String app : applications) {
				criterias.add(Criteria.where(APPLICATION).is(app));
			}
			query.addCriteria(Criteria.where(APPLICATION).exists(true)
					.orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}

		List<ApplicationResource> applicationResources = mongoTemplate.find(query, ApplicationResource.class,
				APPLICATION_COLLECTION);

		return applicationResources;
	}

	@Override
	public Pair<Long, List<ApplicationResource>> findApplicationResourcePage(int offset, int limit) {
		Query query = new Query();

		query.skip(offset).limit(limit);
		List<ApplicationResource> applicationResources = mongoTemplate.find(query, ApplicationResource.class,
				APPLICATION_COLLECTION);
		Long size = this.count();
		return new Pair<Long, List<ApplicationResource>>(size, applicationResources);
	}

}
