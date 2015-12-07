package com.dianping.swallow.web.service.impl;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerTopicStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午3:17:56
 */
@Service("producerTopicStatsDataService")
public class ProducerTopicStatsDataServiceImpl implements ProducerTopicStatsDataService {

	@Autowired
	private ProducerTopicStatsDataDao producerTopicStatsDataDao;

	@Override
	public boolean insert(ProducerTopicStatsData topicStatsData) {
		return producerTopicStatsDataDao.insert(topicStatsData);
	}

	@Override
	public boolean insert(List<ProducerTopicStatsData> topicStatsDatas) {
		return producerTopicStatsDataDao.insert(topicStatsDatas);
	}

	@Override
	public boolean removeLessThanTimeKey(long timeKey) {
		return producerTopicStatsDataDao.removeLessThanTimeKey(timeKey);
	}

	@Override
	public List<ProducerTopicStatsData> findByTopic(String topicName, int offset, int limit) {
		return producerTopicStatsDataDao.findByTopic(topicName, offset, limit);
	}

	@Override
	public List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey) {
		return producerTopicStatsDataDao.findSectionData(topicName, startKey, endKey);
	}

	@Override
	public NavigableMap<Long, Long> findSectionQpsData(String topicName, long startKey, long endKey) {
		List<ProducerTopicStatsData> topicStatsDatas = producerTopicStatsDataDao.findSectionData(topicName, startKey,
				endKey);
		NavigableMap<Long, Long> topicStatsDataMap = null;
		if (topicStatsDatas != null) {
			topicStatsDataMap = new TreeMap<Long, Long>();
			for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
				topicStatsDataMap.put(topicStatsData.getTimeKey(), topicStatsData.getQps());
			}
		}
		return topicStatsDataMap;
	}

	@Override
	public NavigableMap<Long, Long> findSectionDelayData(String topicName, long startKey, long endKey) {
		List<ProducerTopicStatsData> topicStatsDatas = producerTopicStatsDataDao.findSectionData(topicName, startKey,
				endKey);
		NavigableMap<Long, Long> topicStatsDataMap = null;
		if (topicStatsDatas != null) {
			topicStatsDataMap = new TreeMap<Long, Long>();
			for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
				topicStatsDataMap.put(topicStatsData.getTimeKey(), topicStatsData.getDelay());
			}
		}
		return topicStatsDataMap;
	}

	@Override
	public ProducerTopicStatsData findOneByTopicAndTime(String topicName, long startKey, long endKey, boolean isGt) {
		return producerTopicStatsDataDao.findOneByTopicAndTime(topicName, startKey, endKey, isGt);
	}

}
