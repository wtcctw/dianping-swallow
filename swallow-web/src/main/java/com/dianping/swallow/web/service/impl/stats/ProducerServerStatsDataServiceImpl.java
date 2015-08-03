package com.dianping.swallow.web.service.impl.stats;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.stats.ProducerServerStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.service.stats.ProducerServerStatsDataService;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午3:17:48
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

}
