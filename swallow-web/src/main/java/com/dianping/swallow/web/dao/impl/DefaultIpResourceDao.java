package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.IpResourceDao;
import com.dianping.swallow.web.model.resource.IpResource;
import com.mongodb.WriteResult;

/**
 * @author mingdongli
 *
 *         2015年8月11日上午11:19:38
 */
@Component
public class DefaultIpResourceDao extends AbstractWriteDao implements IpResourceDao {

	private static final String IPRESOURCE_COLLECTION = "IP_RESOURCE";

	private static final String IP = "ip";

	private static final String APPLICATION = "application";

	private static final String DEFAULT = "default";

	@Override
	public boolean insert(IpResource ipResource) {

		try {
			mongoTemplate.save(ipResource, IPRESOURCE_COLLECTION);
			return true;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("[insert] error when save producer server stats data " + ipResource, e);
			}
		}
		return false;
	}

	@Override
	public boolean update(IpResource ipResource) {

		return insert(ipResource);
	}

	@Override
	public int remove(String ip) {

		Query query = new Query(Criteria.where(IP).is(ip));
		WriteResult result = mongoTemplate.remove(query, IpResource.class, IPRESOURCE_COLLECTION);
		return result.getN();
	}

	@Override
	public long count() {

		Query query = new Query();
		return mongoTemplate.count(query, IPRESOURCE_COLLECTION);
	}

	@Override
	public Pair<Long, List<IpResource>> findByIp(int offset, int limit, boolean admin, String... ips) {

		List<Criteria> criterias = new ArrayList<Criteria>();
		for (String ip : ips) {
			criterias.add(Criteria.where(IP).is(ip));
		}

		Query query = new Query();
		long size = 0;

		if (admin) {
			size = mongoTemplate.count(query, IPRESOURCE_COLLECTION);
		} else {
			query.addCriteria(Criteria.where(IP).exists(true)
					.orOperator(criterias.toArray(new Criteria[criterias.size()])));
			size = mongoTemplate.count(query, IPRESOURCE_COLLECTION);
		}

		query.skip(offset).limit(limit);
		List<IpResource> ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);
		return new Pair<Long, List<IpResource>>(size, ipResources);
	}

	@Override
	public Pair<Long, List<IpResource>> findByApplication(int offset, int limit, String application) {

		Query query = new Query(Criteria.where(APPLICATION).is(application));
		long size = mongoTemplate.count(query, IPRESOURCE_COLLECTION);

		query.skip(offset).limit(limit);
		List<IpResource> ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);

		return new Pair<Long, List<IpResource>>(size, ipResources);
	}

	@Override
	public Pair<Long, List<IpResource>> find(int offset, int limit, String application, String... ips) {

		Query query = new Query();
		List<Criteria> criterias = new ArrayList<Criteria>();
		if (ips.length > 0) {
			for (String ip : ips) {
				criterias.add(Criteria.where(IP).is(ip));
			}
			query.addCriteria(Criteria.where(IP).exists(true)
					.orOperator(criterias.toArray(new Criteria[criterias.size()])));
		}

		if (StringUtils.isNotBlank(application)) {
			query.addCriteria(Criteria.where(APPLICATION).is(application));
		}

		long size = mongoTemplate.count(query, IPRESOURCE_COLLECTION);

		query.skip(offset).limit(limit);
		List<IpResource> ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);

		return new Pair<Long, List<IpResource>>(size, ipResources);
	}

	@Override
	public List<IpResource> findAll(String... fields) {

		List<IpResource> ipResources = new ArrayList<IpResource>();

		if (fields.length == 0) {
			ipResources = mongoTemplate.findAll(IpResource.class, IPRESOURCE_COLLECTION);
		} else {
			Query query = new Query();
			for (String field : fields) {
				query.fields().include(field);
			}
			ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);
		}

		return ipResources;
	}

	@Override
	public IpResource findDefault() {

		Query query = new Query(Criteria.where(IP).is(DEFAULT));
		IpResource IpResource = mongoTemplate.findOne(query, IpResource.class, IPRESOURCE_COLLECTION);
		return IpResource;
	}

	@Override
	public Pair<Long, List<IpResource>> findIpResourcePage(int offset, int limit) {

		Query query = new Query();

		query.skip(offset).limit(limit);
		List<IpResource> ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);
		Long size = this.count();
		return new Pair<Long, List<IpResource>>(size, ipResources);
	}

	@Override
	public List<IpResource> findByIps(String... ips) {
		List<Criteria> criterias = new ArrayList<Criteria>();
		Query query = new Query();
		List<IpResource> ipResources = null;
		if (ips != null && ips.length > 0) {
			for (String ip : ips) {
				criterias.add(Criteria.where(IP).is(ip));
			}
			query.addCriteria(Criteria.where(IP).exists(true)
					.orOperator(criterias.toArray(new Criteria[criterias.size()])));
			ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);
		}

		return ipResources;
	}

	@Override
	public List<IpResource> findByIp(String ip) {
		if (StringUtils.isBlank(ip)) {
			return null;
		}
		Query query = new Query();
		query.addCriteria(Criteria.where(IP).is(ip));
		List<IpResource> ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);
		return ipResources;
	}

}
