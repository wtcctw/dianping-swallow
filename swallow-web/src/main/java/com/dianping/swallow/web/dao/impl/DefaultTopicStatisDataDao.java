package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.TopicStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.TopicStatsData;
import com.mongodb.WriteResult;

@Service("topicStatisDataDao")
public class DefaultTopicStatisDataDao extends AbstractWriteDao implements TopicStatisDataDao {
	
	private static final Logger logger = LoggerFactory.getLogger(DefaultTopicStatisDataDao.class);

	private static final String TOPICSTATISDATA_COLLECTION = "swallowwebtopicstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";
	
	private static final String TOPICNAME_FIELD = "topicName";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(TopicStatsData statisData) {
		try {
			mongoTemplate.save(statisData, TOPICSTATISDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save topic " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(TopicStatsData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, TopicStatsData.class, TOPICSTATISDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public TopicStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		TopicStatsData topicStatisData = mongoTemplate.findOne(query, TopicStatsData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public TopicStatsData findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		TopicStatsData topicStatisData = mongoTemplate.findOne(query, TopicStatsData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public List<TopicStatsData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<TopicStatsData> topicStatisDatas = mongoTemplate.find(query, TopicStatsData.class, TOPICSTATISDATA_COLLECTION);
		return topicStatisDatas;
	}

}
