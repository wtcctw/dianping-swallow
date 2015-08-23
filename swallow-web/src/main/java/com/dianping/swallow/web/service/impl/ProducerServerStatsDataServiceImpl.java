package com.dianping.swallow.web.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午3:17:48
 */
@Service("producerServerStatsDataService")
public class ProducerServerStatsDataServiceImpl implements ProducerServerStatsDataService {

	@Autowired
	private ProducerServerStatsDataDao producerServerStatsDataDao;

	@Override
	public boolean insert(ProducerServerStatsData serverStatsData) {
		return producerServerStatsDataDao.insert(serverStatsData);
	}

	@Override
	public boolean update(ProducerServerStatsData serverStatsData) {
		return producerServerStatsDataDao.update(serverStatsData);
	}

	@Override
	public int deleteById(String id) {
		return producerServerStatsDataDao.deleteById(id);
	}

	@Override
	public ProducerServerStatsData findById(String id) {
		return producerServerStatsDataDao.findById(id);
	}

	@Override
	public ProducerServerStatsData findByTimeKey(String ip, long timeKey) {
		return producerServerStatsDataDao.findByTimeKey(ip, timeKey);
	}

	@Override
	public List<ProducerServerStatsData> findSectionData(String ip, long startKey, long endKey) {
		return producerServerStatsDataDao.findSectionData(ip, startKey, endKey);
	}

	@Override
	public Map<String, NavigableMap<Long, Long>> findSectionQpsData(long startKey, long endKey) {
		List<ProducerServerStatsData> serverStatsDatas = producerServerStatsDataDao.findSectionData(startKey, endKey);
		Map<String, NavigableMap<Long, Long>> serverStatsDataMaps = null;
		if (serverStatsDatas != null) {
			serverStatsDataMaps = new HashMap<String, NavigableMap<Long, Long>>();
			for (ProducerServerStatsData serverStatsData : serverStatsDatas) {
				if (serverStatsDataMaps.containsKey(serverStatsData.getIp())) {
					NavigableMap<Long, Long> serverStatsDataMap = serverStatsDataMaps.get(serverStatsData.getIp());
					serverStatsDataMap.put(serverStatsData.getTimeKey(), serverStatsData.getQps());
					serverStatsDataMaps.put(serverStatsData.getIp(), serverStatsDataMap);
				} else {
					NavigableMap<Long, Long> serverStatsDataMap = new TreeMap<Long, Long>();
					serverStatsDataMap.put(serverStatsData.getTimeKey(), serverStatsData.getQps());
					serverStatsDataMaps.put(serverStatsData.getIp(), serverStatsDataMap);
				}
			}
		}
		return serverStatsDataMaps;
	}
}
