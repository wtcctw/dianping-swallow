package com.dianping.swallow.web.dao.impl.stats;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.stats.ProducerTopicStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.mongodb.WriteResult;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:38:59
 */
@Service("producerTopicStatsDataDao")
public class DefaultProducerTopicStatsDataDao extends AbstractStatsDao implements ProducerTopicStatsDataDao {

	private static final String TOPICSTATSDATA_COLLECTION = "producertopicstatsdata";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String TOPICNAME_FIELD = "topicName";

	private static final String ID_FIELD = "id";

	@Override
	public boolean insert(ProducerTopicStatsData topicStatsData) {
		try {
			mongoTemplate.save(topicStatsData, TOPICSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] Error when save producer topic stats data " + topicStatsData, e);
		}
		return false;
	}

	@Override
	public boolean update(ProducerTopicStatsData topicStatsData) {
		return insert(topicStatsData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ProducerTopicStatsData.class, TOPICSTATSDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ProducerTopicStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ProducerTopicStatsData topicStatisData = mongoTemplate.findOne(query, ProducerTopicStatsData.class,
				TOPICSTATSDATA_COLLECTION);
		return topicStatisData;
	}

	@Override
	public List<ProducerTopicStatsData> findByTopic(String topicName) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName));
		List<ProducerTopicStatsData> topicStatsDatas = mongoTemplate.find(query, ProducerTopicStatsData.class,
				TOPICSTATSDATA_COLLECTION);
		return topicStatsDatas;
	}

	@Override
	public List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey) {
		Query query = new Query(Criteria.where(TOPICNAME_FIELD).is(topicName).and(TIMEKEY_FIELD).gte(startKey)
				.lte(endKey)).with(new Sort(new Sort.Order(Direction.ASC, TIMEKEY_FIELD)));
		List<ProducerTopicStatsData> topicStatsDatas = mongoTemplate.find(query, ProducerTopicStatsData.class,
				TOPICSTATSDATA_COLLECTION);
		return topicStatsDatas;
	}

}
