package com.dianping.swallow.web.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ConsumerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午3:17:42
 */
@Service("consumerServerStatsDataService")
public class ConsumerServerStatsDataServiceImpl implements ConsumerServerStatsDataService {

	@Autowired
	private ConsumerServerStatsDataDao consumerServerStatsDataDao;

	@Override
	public boolean insert(ConsumerServerStatsData serverStatsData) {
		return consumerServerStatsDataDao.insert(serverStatsData);
	}

	@Override
	public List<ConsumerServerStatsData> findSectionData(String ip, long startKey, long endKey) {
		return consumerServerStatsDataDao.findSectionData(ip, startKey, endKey);
	}

	@Override
	public long findQpsByServerIp(String ip, long startKey, long endKey) {
		List<ConsumerServerStatsData> serverStatsDatas = findSectionData(ip, startKey, endKey);
		long totalQps = 0;
		for (ConsumerServerStatsData serverStatsData : serverStatsDatas) {
			totalQps += serverStatsData.getSendQps();
		}
		return totalQps;
	}

	@Override
	public Map<String, StatsDataMapPair> findSectionQpsData(long startKey, long endKey) {
		List<ConsumerServerStatsData> serverStatsDatas = consumerServerStatsDataDao.findSectionData(startKey, endKey);
		Map<String, StatsDataMapPair> serverStatsDataMaps = null;

		if (serverStatsDatas != null) {
			serverStatsDataMaps = new HashMap<String, StatsDataMapPair>();

			for (ConsumerServerStatsData serverStatsData : serverStatsDatas) {

				if (serverStatsDataMaps.containsKey(serverStatsData.getIp())) {

					StatsDataMapPair statsDataResult = serverStatsDataMaps.get(serverStatsData.getIp());
					statsDataResult.getSendStatsData().put(serverStatsData.getTimeKey(), serverStatsData.getSendQps());
					statsDataResult.getAckStatsData().put(serverStatsData.getTimeKey(), serverStatsData.getAckQps());
					serverStatsDataMaps.put(serverStatsData.getIp(), statsDataResult);
				} else {
					StatsDataMapPair statsDataResult = new StatsDataMapPair();
					NavigableMap<Long, Long> sendStatsData = new TreeMap<Long, Long>();
					sendStatsData.put(serverStatsData.getTimeKey(), serverStatsData.getSendQps());
					NavigableMap<Long, Long> ackStatsData = new TreeMap<Long, Long>();
					sendStatsData.put(serverStatsData.getTimeKey(), serverStatsData.getAckQps());
					statsDataResult.setSendStatsData(sendStatsData);
					statsDataResult.setAckStatsData(ackStatsData);
					serverStatsDataMaps.put(serverStatsData.getIp(), statsDataResult);
				}
			}
		}
		return serverStatsDataMaps;
	}

}
