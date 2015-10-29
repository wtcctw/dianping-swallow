package com.dianping.swallow.web.alarmer.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.impl.AbstractRetriever;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 下午8:03:27
 */
@Component("statsDataContainer")
public class StatsDataContainerImpl implements StatsDataContainer {

	private static final Logger logger = LogManager.getLogger(StatsDataContainerImpl.class);

	private static final int sampleInterval = AbstractRetriever.getStorageIntervalTime();

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
			try {
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
				consumerIdStatsData.setTotalStatsDatas(lastStatsData, sampleInterval);
				cStatsDataContainer.put(uniquekey, consumerIdStatsData);
			} catch (Exception e) {
				logger.error("[setConsumerIdTotalRatio] setConsumerIdTotalRatio {} error.", consumerIdStatsData, e);
			}
		}

	}

	@Override
	public void setProducerTopicTotalRatio(List<ProducerTopicStatsData> topicStatsDatas) {
		logger.info("[setProducerTopicTotalRatio]");
		if (topicStatsDatas == null) {
			return;
		}
		for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
			try {
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
				topicStatsData.setTotalStatsDatas(lastStatsData, sampleInterval);
				pStatsDataContainer.put(topicName, topicStatsData);
			} catch (Exception e) {
				logger.error("[setProducerTopicTotalRatio] setProducerTopicTotalRatio {} error.", topicStatsData, e);
			}
		}
	}

}
