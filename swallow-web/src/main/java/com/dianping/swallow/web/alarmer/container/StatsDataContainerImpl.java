package com.dianping.swallow.web.alarmer.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 下午8:03:27
 */
public class StatsDataContainerImpl implements StatsDataContainer {

	private static final Logger logger = LoggerFactory.getLogger(StatsDataContainerImpl.class);

	private Map<String, ConsumerIdStatsData> cStatsDataContainer = new ConcurrentHashMap<String, ConsumerIdStatsData>();

	private Map<String, ProducerTopicStatsData> pStatsDataContainer = new ConcurrentHashMap<String, ProducerTopicStatsData>();

	@Autowired
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Autowired
	private ProducerTopicStatsDataService topicStatsDataService;

	@Override
	public void setConsumerIdTotalRatio(List<ConsumerIdStatsData> consumerIdStatsDatas) {
		logger.info("[setConsumerIdTotalRatio]");
		if (consumerIdStatsDatas == null) {
			return;
		}
		for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
			String topicName = consumerIdStatsData.getTopicName();
			String consumerId = consumerIdStatsData.getConsumerId();
			String uniquekey = consumerIdStatsData.generateKey();
			ConsumerIdStatsData lastStatsData = null;
			if (cStatsDataContainer.containsKey(uniquekey)) {
				lastStatsData = cStatsDataContainer.get(uniquekey);
			} else {
				List<ConsumerIdStatsData> lastStatsDatas = consumerIdStatsDataService.findByTopicAndConsumerId(
						topicName, consumerId, 0, 1);
				if (lastStatsDatas != null && lastStatsDatas.size() > 0) {
					lastStatsData = lastStatsDatas.get(0);
				}
			}
			consumerIdStatsData.setTotalStatsDatas(lastStatsData);
			cStatsDataContainer.put(uniquekey, consumerIdStatsData);
		}
	}

	@Override
	public void setProducerTopicTotalRatio(List<ProducerTopicStatsData> topicStatsDatas) {
		logger.info("[setProducerTopicTotalRatio]");
		if (topicStatsDatas == null) {
			return;
		}
		for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
			String topicName = topicStatsData.getTopicName();
			ProducerTopicStatsData lastStatsData = null;
			if (pStatsDataContainer.containsKey(topicName)) {
				lastStatsData = pStatsDataContainer.get(topicName);
			} else {
				List<ProducerTopicStatsData> lastStatsDatas = topicStatsDataService.findByTopic(topicName, 0, 1);
				if (lastStatsDatas != null && lastStatsDatas.size() > 0) {
					lastStatsData = lastStatsDatas.get(0);
				}
			}
			topicStatsData.setTotalStatsDatas(lastStatsData);
			pStatsDataContainer.put(topicName, topicStatsData);
		}
	}

}
