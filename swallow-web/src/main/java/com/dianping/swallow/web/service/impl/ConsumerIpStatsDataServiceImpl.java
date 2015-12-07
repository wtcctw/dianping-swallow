package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerIpStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年9月15日 下午5:49:43
 */
@Service("consumerIpStatsDataService")
public class ConsumerIpStatsDataServiceImpl implements ConsumerIpStatsDataService {

	@Autowired
	private ConsumerIpStatsDataDao consumerIpStatsDataDao;

	@Override
	public boolean insert(ConsumerIpStatsData ipStatsData) {
		return consumerIpStatsDataDao.insert(ipStatsData);
	}
	
	@Override
	public boolean insert(List<ConsumerIpStatsData> ipStatsDatas) {
		return consumerIpStatsDataDao.insert(ipStatsDatas);
	}

	@Override
	public boolean removeLessThanTimeKey(long timeKey) {
		return consumerIpStatsDataDao.removeLessThanTimeKey(timeKey);
	}

	@Override
	public List<ConsumerIpStatsData> find(String topicName, String consumerId, String ip, long startKey, long endKey) {
		return consumerIpStatsDataDao.find(topicName, consumerId, ip, startKey, endKey);
	}

	@Override
	public ConsumerIpQpsPair findAvgQps(String topicName, String consumerId, String ip, long startKey, long endKey) {
		List<ConsumerIpStatsData> ipStatsDatas = consumerIpStatsDataDao.find(topicName, consumerId, ip, startKey,
				endKey);
		long sendQps = 0L, ackQps = 0L;
		if (ipStatsDatas == null || ipStatsDatas.size() == 0) {
			return new ConsumerIpQpsPair(sendQps, ackQps);
		}
		int size = ipStatsDatas.size();
		for (ConsumerIpStatsData ipStatsData : ipStatsDatas) {
			sendQps += ipStatsData.getSendQps();
			ackQps += ipStatsData.getAckQps();
		}
		return new ConsumerIpQpsPair(sendQps / size, ackQps / size);
	}
}
