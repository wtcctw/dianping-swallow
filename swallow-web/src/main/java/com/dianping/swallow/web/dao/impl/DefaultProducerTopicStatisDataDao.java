package com.dianping.swallow.web.dao.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerTopicStatisDataDao;
import com.dianping.swallow.web.dao.impl.AbstractWriteDao;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;
import com.mongodb.WriteResult;

/**
 *
 * @author qiyin
 *
 */
@Service("producerTopicStatisDataDao")
public class DefaultProducerTopicStatisDataDao extends AbstractWriteDao implements ProducerTopicStatisDataDao {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerTopicStatisDataDao.class);

	private static final String TOPICSTATISDATA_COLLECTION = "swallowwebproducertopicstatisdatac";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ProducerTopicStatsData statisData) {
		try {
			mongoTemplate.save(statisData, TOPICSTATISDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("Error when save producer topic statis data " + statisData, e);
		}
		return false;
	}

	@Override
	public boolean update(ProducerTopicStatsData statisData) {
		return insert(statisData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ProducerTopicStatsData.class, TOPICSTATISDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ProducerTopicStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ProducerTopicStatsData topicStatisData = mongoTemplate.findOne(query, ProducerTopicStatsData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public ProducerTopicStatsData findByTimeKey(long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey));
		ProducerTopicStatsData topicStatisData = mongoTemplate.findOne(query, ProducerTopicStatsData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public List<ProducerTopicStatsData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<ProducerTopicStatsData> topicStatisDatas = mongoTemplate.find(query, ProducerTopicStatsData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisDatas;
	}

	public List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).gte(startKey)
				.lte(endKey)).with(new Sort(new Sort.Order(Direction.ASC, TIMEKEY_FIELD)));
		List<ProducerTopicStatsData> topicStatisDatas = mongoTemplate.find(query, ProducerTopicStatsData.class,
				TOPICSTATISDATA_COLLECTION);
		return topicStatisDatas;
	}
}
