package com.dianping.swallow.web.dao.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jodd.util.StringUtil;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.TopicResourceDao;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;

/**
 * @author mingdongli
 *
 *         2015年8月10日下午7:26:04
 */
@Component
public class DefaultTopicResourceDao extends AbstractWriteDao implements TopicResourceDao {

	private static final String TOPICRESOURCE_COLLECTION = "TOPIC_RESOURCE";

	private static final String TOPIC = "topic";

	private static final String ID = "id";

	private static final String PEODUCERIPS = "producerIpInfos.ip";

	private static final String ACTIVE = "producerIpInfos.active";

	private static final String ADMINISTRATOR = "administrator";

	private static final String DEFAULT = "default";

	@Override
	public boolean insert(TopicResource topicResource) {

		try {
			mongoTemplate.save(topicResource, TOPICRESOURCE_COLLECTION);
			return true;
		} catch (Exception e) {
			if (logger.isErrorEnabled()) {
				logger.error("[insert] error when save producer server stats data " + topicResource, e);
			}
		}
		return false;
	}

	@Override
	public boolean update(TopicResource topicResource) {

		return insert(topicResource);
	}

	@Override
	public long count() {

		Query query = new Query();
		return mongoTemplate.count(query, TOPICRESOURCE_COLLECTION);
	}

	@Override
	public TopicResource findByTopic(String topic) {

		Query query = new Query(Criteria.where(TOPIC).is(topic));
		TopicResource topicResource = mongoTemplate.findOne(query, TopicResource.class, TOPICRESOURCE_COLLECTION);
		return topicResource;
	}

	@Override
	public Pair<Long, List<TopicResource>> find(int offset, int limit, String topic, String producerIp, boolean inactive) {

		Query query = new Query();

		long size = -1;
		if (StringUtil.isNotBlank(topic)) {
			String[] topics = topic.split(",");
			int length = topics.length;
			if (length > 1) {
				size = length;
				Arrays.sort(topics);
				int rightIndex = (int) Math.min(length, offset + limit);
				List<Criteria> criterias = new ArrayList<Criteria>();
				for (int i = offset; i < rightIndex; ++i) {
					criterias.add(Criteria.where(TOPIC).is(topics[i]));
				}

				query.addCriteria(Criteria.where(TOPIC).exists(true)
						.orOperator(criterias.toArray(new Criteria[criterias.size()])));
			} else {
				query.addCriteria(Criteria.where(TOPIC).is(topic));
			}
		}
		if (StringUtil.isNotBlank(producerIp)) {
			query.addCriteria(Criteria.where(PEODUCERIPS).is(producerIp));
		}

		if(!inactive){
			query.addCriteria(Criteria.where(ACTIVE).is(inactive));
			size = mongoTemplate.count(query, TOPICRESOURCE_COLLECTION);
		}

		List<TopicResource> topicResources = mongoTemplate.find(query, TopicResource.class, TOPICRESOURCE_COLLECTION);

		query.skip(offset).limit(limit);
		if (size < 0) {
			size = mongoTemplate.count(query, TOPICRESOURCE_COLLECTION);
		}

		return new Pair<Long, List<TopicResource>>(size, topicResources);

	}

	@Override
	public Pair<Long, List<TopicResource>> findByTopics(int offset, int limit, String... topics) {

		List<Criteria> criterias = new ArrayList<Criteria>();
		for (String t : topics) {
			criterias.add(Criteria.where(TOPIC).is(t));
		}

		Query query = new Query();
		query.addCriteria(Criteria.where(TOPIC).exists(true)
				.orOperator(criterias.toArray(new Criteria[criterias.size()])));

		long size = mongoTemplate.count(query, TOPICRESOURCE_COLLECTION);

		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, TOPIC)));
		List<TopicResource> topicResources = mongoTemplate.find(query, TopicResource.class, TOPICRESOURCE_COLLECTION);
		return new Pair<Long, List<TopicResource>>(size, topicResources);
	}

	@Override
	public TopicResource findById(String id) {

		Query query = new Query(Criteria.where(ID).is(id));
		TopicResource topicResource = mongoTemplate.findOne(query, TopicResource.class, TOPICRESOURCE_COLLECTION);
		return topicResource;
	}

	@Override
	public TopicResource findDefault() {

		Query query = new Query(Criteria.where(TOPIC).is(DEFAULT));
		TopicResource topicResource = mongoTemplate.findOne(query, TopicResource.class, TOPICRESOURCE_COLLECTION);
		return topicResource;
	}

	@Override
	public List<TopicResource> findAll() {

		List<TopicResource> topicResource = mongoTemplate.findAll(TopicResource.class, TOPICRESOURCE_COLLECTION);
		return topicResource;
	}

	@Override
	public Pair<Long, List<TopicResource>> findTopicResourcePage(int offset, int limit) {

		Query query = new Query();

		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, TOPIC)));
		List<TopicResource> topicResource = mongoTemplate.find(query, TopicResource.class, TOPICRESOURCE_COLLECTION);

		long size = this.count();
		return new Pair<Long, List<TopicResource>>(size, topicResource);
	}

	@Override
	public Pair<Long, List<TopicResource>> findByServer(int offset, int limit, String producerIp) {

		Query query = new Query(Criteria.where(PEODUCERIPS).is(producerIp));

		Long size = mongoTemplate.count(query, TOPICRESOURCE_COLLECTION);

		query.skip(offset).limit(limit);
		List<TopicResource> topicResource = mongoTemplate.find(query, TopicResource.class, TOPICRESOURCE_COLLECTION);

		return new Pair<Long, List<TopicResource>>(size, topicResource);

	}

	@Override
	public Pair<Long, List<TopicResource>> findByAdministrator(int offset, int limit, String administrator) {

		Query query = new Query(Criteria.where(ADMINISTRATOR).regex(".*" + administrator + ".*"));

		Long size = mongoTemplate.count(query, TOPICRESOURCE_COLLECTION);

		query.skip(offset).limit(limit);
		List<TopicResource> topicResource = mongoTemplate.find(query, TopicResource.class, TOPICRESOURCE_COLLECTION);

		return new Pair<Long, List<TopicResource>>(size, topicResource);

	}

	@Override
	public long countInactive() {
		Query query = new Query(Criteria.where(ACTIVE).is(Boolean.FALSE));
		query.fields().include(ACTIVE);
		List<TopicResource> topicResources =  mongoTemplate.find(query, TopicResource.class, TOPICRESOURCE_COLLECTION);
		
		long result = 0;
		int size = topicResources.size();
		TopicResource topicResource = null;
		List<IpInfo> ipInfos = null;
		int ipInfoSize = 0;
		for(int i = 0; i < size; ++i){
			topicResource = topicResources.get(i);
			ipInfos = topicResource.getProducerIpInfos();
			ipInfoSize = ipInfos.size();
			for(int j = 0; j < ipInfoSize; ++j){
				if(!ipInfos.get(j).isActive()){
					++result;
				}
			}
		}
		
		return result;
	}

}
