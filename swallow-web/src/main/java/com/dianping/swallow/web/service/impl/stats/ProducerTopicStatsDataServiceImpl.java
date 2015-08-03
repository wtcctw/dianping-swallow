package com.dianping.swallow.web.service.impl.stats;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.dao.stats.ProducerTopicStatsDataDao;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.service.stats.ProducerTopicStatsDataService;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午3:17:56
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
	public boolean update(ProducerTopicStatsData topicStatsData) {
		return producerTopicStatsDataDao.update(topicStatsData);
	}

	@Override
	public int deleteById(String id) {
		return producerTopicStatsDataDao.deleteById(id);
	}

	@Override
	public ProducerTopicStatsData findById(String id) {
		return producerTopicStatsDataDao.findById(id);
	}

	@Override
	public List<ProducerTopicStatsData> findByTopic(String topicName) {
		return producerTopicStatsDataDao.findByTopic(topicName);
	}

	@Override
	public List<ProducerTopicStatsData> findSectionData(String topicName, long startKey, long endKey) {
		return producerTopicStatsDataDao.findSectionData(topicName, startKey, endKey);
	}

}
