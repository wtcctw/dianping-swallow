package com.dianping.swallow.web.service.impl.stats;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.stats.ConsumerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.service.stats.ConsumerServerStatsDataService;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午3:17:42
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
	public boolean update(ConsumerServerStatsData serverStatsData) {
		return consumerServerStatsDataDao.update(serverStatsData);
	}

	@Override
	public int deleteById(String id) {
		return consumerServerStatsDataDao.deleteById(id);
	}

	@Override
	public ConsumerServerStatsData findById(String id) {
		return consumerServerStatsDataDao.findById(id);
	}

	@Override
	public ConsumerServerStatsData findByTimeKey(String ip, long timeKey) {
		return consumerServerStatsDataDao.findByTimeKey(ip, timeKey);
	}

	@Override
	public List<ConsumerServerStatsData> findSectionData(String ip, long startKey, long endKey) {
		return consumerServerStatsDataDao.findSectionData(ip, startKey, endKey);
	}

}
