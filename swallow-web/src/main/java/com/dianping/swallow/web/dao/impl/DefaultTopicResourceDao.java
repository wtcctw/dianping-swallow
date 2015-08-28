package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.controller.dto.TopicQueryDto;
import com.dianping.swallow.web.dao.TopicResourceDao;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.mongodb.WriteResult;


/**
 * @author mingdongli
 *
 * 2015年8月10日下午7:26:04
 */
@Component
public class DefaultTopicResourceDao extends AbstractWriteDao implements TopicResourceDao{
	
	private static final String TOPICRESOURCE_COLLECTION = "TOPIC_RESOURCE";
	
	private static final String TOPIC = "topic";

	private static final String ID = "id";
	
	private static final String PEODUCERIPS = "producerIps";

	private static final String DEFAULT = "default";

	@Override
	public boolean insert(TopicResource topicResource) {

		try {
			mongoTemplate.save(topicResource, TOPICRESOURCE_COLLECTION);
			return true;
		} catch (Exception e) {
			if(logger.isErrorEnabled()){
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
	public int remove(String topic) {

		Query query = new Query(Criteria.where(TOPIC).is(topic));
		WriteResult result = mongoTemplate.remove(query, TopicResource.class, TOPICRESOURCE_COLLECTION);
		return result.getN();
	}
	
	@Override
	public long count() {
		
		Query query = new Query();
		return mongoTemplate.count(query, TOPICRESOURCE_COLLECTION);
	}

	@Override
	public TopicResource findByTopic(String topic) {

		Query query = new Query(Criteria.where(TOPIC).is(topic));
		TopicResource topicResource = mongoTemplate.findOne(query, TopicResource.class,
				TOPICRESOURCE_COLLECTION);
		return topicResource;
	}
	
	@Override
	public TopicResource findById(String id) {

		Query query = new Query(Criteria.where(ID).is(id));
		TopicResource topicResource = mongoTemplate.findOne(query, TopicResource.class,
				TOPICRESOURCE_COLLECTION);
		return topicResource;
	}

	@Override
	public TopicResource findDefault() {

		Query query = new Query(Criteria.where(TOPIC).is(DEFAULT));
		TopicResource topicResource = mongoTemplate.findOne(query, TopicResource.class,
				TOPICRESOURCE_COLLECTION);
		return topicResource;
	}
	
	@Override
	public List<TopicResource> findAll(){
		
		List<TopicResource> topicResource = mongoTemplate.findAll(TopicResource.class,
				TOPICRESOURCE_COLLECTION);
		return topicResource;
	}

	@Override
	public Pair<Long, List<TopicResource>> findTopicResourcePage(TopicQueryDto topicQueryDto) {

		Query query = new Query();
		int offset = topicQueryDto.getOffset();
		int limit = topicQueryDto.getLimit();
		
		query.skip(offset).limit(limit).with(new Sort(new Sort.Order(Direction.ASC, TOPIC)));
		List<TopicResource> topicResource = mongoTemplate.find(query, TopicResource.class,
				TOPICRESOURCE_COLLECTION);
		
		long size = this.count();
		return new Pair<Long, List<TopicResource>>(size, topicResource);
	}
	
	@Override
	public Pair<Long, List<TopicResource>> findByServer(TopicQueryDto  topicQueryDto){
		
		int offset = topicQueryDto.getOffset();
		int limit = topicQueryDto.getLimit();
		String producerServer = topicQueryDto.getProducerServer();
		Query query = new Query(Criteria.where(PEODUCERIPS).is(producerServer));
		
		Long size = mongoTemplate.count(query, TOPICRESOURCE_COLLECTION);
		
		query.skip(offset).limit(limit);
		List<TopicResource> topicResource = mongoTemplate.find(query, TopicResource.class,
				TOPICRESOURCE_COLLECTION);

		return new Pair<Long, List<TopicResource>>(size, topicResource);
		
	}

}
