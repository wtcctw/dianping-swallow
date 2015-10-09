package com.dianping.swallow.web.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.ProducerIpStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.service.ProducerIpStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年9月15日 下午5:47:34
 */
@Service("producerIpStatsDataService")
public class ProducerIpStatsDataServiceImpl implements ProducerIpStatsDataService {

	@Autowired
	private ProducerIpStatsDataDao producerIpStatsDataDao;

	@Override
	public boolean insert(ProducerIpStatsData ipStatsData) {
		return producerIpStatsDataDao.insert(ipStatsData);
	}

	@Override
	public List<ProducerIpStatsData> find(String topicName, String ip, long startKey, long endKey) {
		return producerIpStatsDataDao.find(topicName, ip, startKey, endKey);
	}

	@Override
	public long findAvgQps(String topicName, String ip, long startKey, long endKey) {
		List<ProducerIpStatsData> ipStatsDatas = producerIpStatsDataDao.find(topicName, ip, startKey, endKey);
		long qps = 0L;
		if (ipStatsDatas == null || ipStatsDatas.size() == 0) {
			return qps;
		}
		int size = ipStatsDatas.size();
		for (ProducerIpStatsData ipStatsData : ipStatsDatas) {
			qps += ipStatsData.getQps();
		}
		return qps / size;
	}

}
