package com.dianping.swallow.web.dao.impl.stats;

import java.util.List;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.stats.ProducerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.mongodb.WriteResult;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午2:38:54
 */
@Service("producerServerStatsDataDao")
public class DefaultProducerServerStatsDataDao extends AbstractStatsDao implements ProducerServerStatsDataDao {

	private static final String PRODUCERSERVERSTATSDATA_COLLECTION = "producerserverstatsdata";

	private static final String TIMEKEY_FIELD = "timeKey";

	private static final String ID_FIELD = "id";

	private static final String IP_FIELD = "ip";

	@Override
	public boolean insert(ProducerServerStatsData serverStatsData) {
		try {
			mongoTemplate.save(serverStatsData, PRODUCERSERVERSTATSDATA_COLLECTION);
			return true;
		} catch (Exception e) {
			logger.error("[insert] error when save producer server stats data " + serverStatsData, e);
		}
		return false;
	}

	@Override
	public boolean update(ProducerServerStatsData serverStatsData) {
		return insert(serverStatsData);
	}

	@Override
	public int deleteById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		WriteResult result = mongoTemplate.remove(query, ProducerServerStatsData.class,
				PRODUCERSERVERSTATSDATA_COLLECTION);
		return result.getN();
	}

	@Override
	public ProducerServerStatsData findById(String id) {
		Query query = new Query(Criteria.where(ID_FIELD).is(id));
		ProducerServerStatsData serverStatisData = mongoTemplate.findOne(query, ProducerServerStatsData.class,
				PRODUCERSERVERSTATSDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public ProducerServerStatsData findByTimeKey(String ip, long timeKey) {
		Query query = new Query(Criteria.where(TIMEKEY_FIELD).is(timeKey).and(IP_FIELD).is(ip));
		ProducerServerStatsData serverStatisData = mongoTemplate.findOne(query, ProducerServerStatsData.class,
				PRODUCERSERVERSTATSDATA_COLLECTION);
		return serverStatisData;
	}

	@Override
	public List<ProducerServerStatsData> findSectionData(String ip, long startKey, long endKey) {
		Query query = new Query(Criteria.where(IP_FIELD).is(ip).and(TIMEKEY_FIELD).gte(startKey).lte(endKey));
		List<ProducerServerStatsData> serverStatisDatas = mongoTemplate.find(query, ProducerServerStatsData.class,
				PRODUCERSERVERSTATSDATA_COLLECTION);
		return serverStatisDatas;
	}

}
