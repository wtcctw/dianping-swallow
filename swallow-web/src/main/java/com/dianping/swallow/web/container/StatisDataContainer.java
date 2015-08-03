package com.dianping.swallow.web.container;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.service.ConsumerIdStatisDataService;
import com.dianping.swallow.web.service.ProducerTopicStatisDataService;
import com.dianping.swallow.web.common.Pair;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 上午11:34:00
 */
@Service("statisDataContainer")
public class StatisDataContainer implements InitializingBean {

	private static final int TOTALSIZE = 120;

	private static StatisDataContainer instance;

	@Autowired
	private ConsumerIdStatisDataService consumerIdStatisDataService;

	@Autowired
	private ProducerTopicStatisDataService topicStatisDataService;

	private Map<Long, Map<String, ConsumerIdStatsData>> consumerIdStatisDatas = new LinkedHashMap<Long, Map<String, ConsumerIdStatsData>>() {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, Map<String, ConsumerIdStatsData>> eldest) {
			return size() > TOTALSIZE;
		}
	};

	private Map<Long, Map<String, ProducerTopicStatsData>> producerTopicStatisDatas = new LinkedHashMap<Long, Map<String, ProducerTopicStatsData>>() {
		
		private static final long serialVersionUID = 1L;

		@Override
		protected boolean removeEldestEntry(Map.Entry<Long, Map<String, ProducerTopicStatsData>> eldest) {
			return size() > TOTALSIZE;
		}
	};

	public Pair<Long, Long> findConsumerIdSectionQps(String topicName, String consumerId, long startKey, long endKey) {
		Pair<Long, Long> result = new Pair<Long, Long>();
		// List<ConsumerIdStatsData>
		// consumerIdStatisDataService.findSectionData(topicName, consumerId,
		// startKey, endKey);
		return result;
	}

	public long findTopicSectionQps(String consumerId, long startKey, long endKey) {
		long result = 0L;

		return result;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}

	public static StatisDataContainer getInstance() {
		return instance;
	}

}
