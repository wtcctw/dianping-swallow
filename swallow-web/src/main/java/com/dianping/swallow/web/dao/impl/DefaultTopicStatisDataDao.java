package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.TopicStatisData;
import com.mongodb.WriteResult;

@Service("topicStatisDataDao")
public class DefaultTopicStatisDataDao extends AbstractWriteDao implements TopicStatisDataDao {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultTopicStatisDataDao.class);

	private static final String TOPICSTATISDATA_COLLECTION = "swallowwebtopicstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";
	
	private static final String TOPICNAME_FIELD = "topicName";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(TopicStatisData statisData) {
		try {
			mongoTemplate.save(statisData, TOPICSTATISDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(TopicStatisData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, TopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public TopicStatisData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		TopicStatisData topicStatisData = mongoTemplate.findOne(query, TopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public TopicStatisData findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		TopicStatisData topicStatisData = mongoTemplate.findOne(query, TopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public List<TopicStatisData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<TopicStatisData> topicStatisDatas = mongoTemplate.find(query, TopicStatisData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisDatas;
	}

}
