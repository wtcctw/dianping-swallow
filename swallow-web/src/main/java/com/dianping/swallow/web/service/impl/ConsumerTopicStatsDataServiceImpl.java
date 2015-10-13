package com.dianping.swallow.web.service.impl;

import java.util.List;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerTopicStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService.StatsDataMapPair;

/**
 * 
 * @author qiyin
 *
 *         2015年8月24日 下午1:03:08
 */
@Service("consumerTopicStatsDataService")
public class ConsumerTopicStatsDataServiceImpl implements ConsumerTopicStatsDataService {

	@Autowired
	private ConsumerTopicStatsDataDao consumerTopicStatsDataDao;

	@Override
	public boolean insert(ConsumerTopicStatsData topicStatsData) {
		return consumerTopicStatsDataDao.insert(topicStatsData);
	}

	@Override
	public StatsDataMapPair findSectionQpsData(String topicName, long startKey, long endKey) {
		List<ConsumerTopicStatsData> topicStatsDatas = consumerTopicStatsDataDao.findSectionData(topicName, startKey,
				endKey);
		StatsDataMapPair statsDataResult = null;
		if (topicStatsDatas != null && !topicStatsDatas.isEmpty()) {
			statsDataResult = new StatsDataMapPair();
			NavigableMap<Long, Long> sendStatsData = new TreeMap<Long, Long>();
			NavigableMap<Long, Long> ackStatsData = new TreeMap<Long, Long>();
			for (ConsumerTopicStatsData topicStatsData : topicStatsDatas) {
				sendStatsData.put(topicStatsData.getTimeKey(), topicStatsData.getSendQps());
				ackStatsData.put(topicStatsData.getTimeKey(), topicStatsData.getAckQps());
				statsDataResult.setSendStatsData(sendStatsData);
				statsDataResult.setAckStatsData(ackStatsData);
			}
		}
		return statsDataResult;
	}

	@Override
	public StatsDataMapPair findSectionDelayData(String topicName, long startKey, long endKey) {
		List<ConsumerTopicStatsData> topicStatsDatas = consumerTopicStatsDataDao.findSectionData(topicName, startKey,
				endKey);
		StatsDataMapPair statsDataResult = null;
		if (topicStatsDatas != null && !topicStatsDatas.isEmpty()) {
			statsDataResult = new StatsDataMapPair();
			NavigableMap<Long, Long> sendStatsData = new TreeMap<Long, Long>();
			NavigableMap<Long, Long> ackStatsData = new TreeMap<Long, Long>();
			for (ConsumerTopicStatsData topicStatsData : topicStatsDatas) {
				sendStatsData.put(topicStatsData.getTimeKey(), topicStatsData.getSendDelay());
				ackStatsData.put(topicStatsData.getTimeKey(), topicStatsData.getAckDelay());
				statsDataResult.setSendStatsData(sendStatsData);
				statsDataResult.setAckStatsData(ackStatsData);
			}
		}
		return statsDataResult;
	}

}
