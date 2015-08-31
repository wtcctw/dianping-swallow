package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.IpQueryDto;
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

	private static final String APPLICATION = "iPDesc.name";

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
	public List<IpResource> findByIp(String ... ips) {

		List<Criteria> criterias = new ArrayList<Criteria>();
		for (String ip : ips) {
			criterias.add(Criteria.where(IP).is(ip));
		}

		Query query = new Query();
		query.addCriteria(Criteria.where(IP).exists(true)
				.orOperator(criterias.toArray(new Criteria[criterias.size()])));
		
		List<IpResource> ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);
		return ipResources;
	}

	@Override
	public Pair<Long, List<IpResource>> findByApplication(IpQueryDto ipQueryDto) {

		Query query = new Query(Criteria.where(APPLICATION).is(ipQueryDto.getApplication()));
		List<IpResource> ipResources = mongoTemplate.find(query, IpResource.class, IPRESOURCE_COLLECTION);
		long size = mongoTemplate.count(query, IPRESOURCE_COLLECTION);

		return new Pair<Long, List<IpResource>>(size, ipResources);
	}
	
	@Override
	public IpResource find(IpQueryDto ipQueryDto) {

		String ip = ipQueryDto.getIp();
		String application = ipQueryDto.getApplication();
		
		Query query = new Query(Criteria.where(IP).is(ip).andOperator(Criteria.where(APPLICATION).is(application)));
		IpResource ipResource = mongoTemplate.findOne(query, IpResource.class, IPRESOURCE_COLLECTION);
		
		return ipResource;
	}

	@Override
	public List<IpResource> findAll(String ... fields) {
		
		List<IpResource> ipResources = new ArrayList<IpResource>();
		
		if(fields.length == 0){
			ipResources = mongoTemplate.findAll(IpResource.class,
					IPRESOURCE_COLLECTION);
		}else{
			Query query = new Query();
			for(String field : fields){
				query.fields().include(field);
			}
			ipResources = mongoTemplate.find(query, IpResource.class,
					IPRESOURCE_COLLECTION);
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
	public Pair<Long, List<IpResource>> findIpResourcePage(IpQueryDto ipQueryDto) {

		Query query = new Query();
		int offset = ipQueryDto.getOffset();
		int limit = ipQueryDto.getLimit();
		
		query.skip(offset).limit(limit);
		List<IpResource> ipResources = mongoTemplate.find(query, IpResource.class,
				IPRESOURCE_COLLECTION);
		Long size = this.count();
		return new Pair<Long, List<IpResource>>(size, ipResources);
	}

}
