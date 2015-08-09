package com.dianping.swallow.web.monitor.wapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerIdStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerServerStatisData;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.model.stats.StatsDataFactory;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午3:23:30
 */
@Service("consumerStatsDataWapper")
public class ConsumerStatsDataWapperImpl extends AbstractStatsDataWapper implements ConsumerStatsDataWapper {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Autowired
	private StatsDataFactory statsDataFactory;

	@Override
	public List<ConsumerServerStatsData> getServerStatsDatas(long timeKey) {
		Set<String> serverKeys = consumerDataRetriever.getKeys(new CasKeys());
		if (serverKeys == null) {
			return null;
		}
		Iterator<String> iterator = serverKeys.iterator();
		List<ConsumerServerStatsData> serverStatsDatas = new ArrayList<ConsumerServerStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String serverIp = iterator.next();
			if (StringUtils.equals(serverIp, TOTAL_KEY)) {
				continue;
			}
			ConsumerServerStatisData serverStatisData = (ConsumerServerStatisData) consumerDataRetriever.getValue(
					new CasKeys(serverIp), StatisType.SEND);
			if (serverStatisData == null) {
				continue;
			}
			NavigableMap<Long, Long> sendQpx = serverStatisData.getQpx(StatisType.SEND);
			NavigableMap<Long, Long> ackQpx = serverStatisData.getQpx(StatisType.ACK);
			NavigableMap<Long, Long> sendDelay = serverStatisData.getDelay(StatisType.SEND);
			NavigableMap<Long, Long> ackDelay = serverStatisData.getDelay(StatisType.ACK);
			if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
					|| sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
				continue;
			}
			if (index == 0) {
				Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
				if (tempKey == null) {
					return null;
				}
				timeKey = tempKey.longValue();
				index++;
			}

			ConsumerServerStatsData serverStatsData = statsDataFactory.createConsumerServerStatsData();
			serverStatsData.setTimeKey(timeKey);
			serverStatsData.setIp(serverIp);
			Long sendQpxValue = sendQpx.get(timeKey);
			if (sendQpxValue != null) {
				serverStatsData.setSendQps(sendQpxValue);
			}
			Long ackQpxValue = ackQpx.get(timeKey);
			if (ackQpxValue != null) {
				serverStatsData.setAckQps(ackQpxValue.longValue());
			}

			Long sendDelayValue = sendDelay.get(timeKey);
			if (sendDelayValue != null) {
				serverStatsData.setSendDelay(sendDelayValue.longValue());
			}

			Long ackDelayValue = ackDelay.get(timeKey);
			if (ackDelayValue != null) {
				serverStatsData.setAckDelay(ackDelayValue.longValue());
			}

			serverStatsDatas.add(serverStatsData);
		}
		return serverStatsDatas;
	}

	@Override
	public Map<String, List<ConsumerIdStatsData>> getConsumerIdStatsDatas(long timeKey) {
		Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (topicKeys == null) {
			return null;
		}
		Iterator<String> iterator = topicKeys.iterator();
		Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap = new HashMap<String, List<ConsumerIdStatsData>>();
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (StringUtils.equals(topicName, TOTAL_KEY)) {
				continue;
			}
			List<ConsumerIdStatsData> consumerIdStatsDatas = getConsumerIdStatsDatas(topicName, timeKey);
			if (consumerIdStatsDatas == null) {
				continue;
			}
			consumerIdStatsDataMap.put(topicName, consumerIdStatsDatas);
		}
		return consumerIdStatsDataMap;
	}

	@Override
	public List<ConsumerIdStatsData> getConsumerIdStatsDatas(String topicName, long timeKey) {
		Set<String> consumerIdKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
		if (consumerIdKeys == null) {
			return null;
		}
		Iterator<String> iterator = consumerIdKeys.iterator();
		List<ConsumerIdStatsData> consumerIdStatsDatas = new ArrayList<ConsumerIdStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String consumerId = iterator.next();
			if (StringUtils.equals(consumerId, TOTAL_KEY)) {
				continue;
			}
			ConsumerIdStatsData consumerIdStatsData = statsDataFactory.createConsumerIdStatsData();
			consumerIdStatsData.setConsumerId(consumerId);
			consumerIdStatsData.setTopicName(topicName);
			ConsumerIdStatisData consumerIdStatisData = (ConsumerIdStatisData) consumerDataRetriever.getValue(
					new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.SEND);
			if (consumerIdStatisData == null) {
				continue;
			}
			NavigableMap<Long, Long> sendQpx = consumerIdStatisData.getQpx(StatisType.SEND);
			NavigableMap<Long, Long> ackQpx = consumerIdStatisData.getQpx(StatisType.ACK);
			NavigableMap<Long, Long> sendDelay = consumerIdStatisData.getDelay(StatisType.SEND);
			NavigableMap<Long, Long> ackDelay = consumerIdStatisData.getDelay(StatisType.ACK);

			if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
					|| sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
				continue;
			}
			if (index == 0) {
				Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
				if (tempKey == null) {
					return null;
				}
				timeKey = tempKey.longValue();
				index++;
			}
			consumerIdStatsData.setTimeKey(timeKey);
			Long sendQpxVlaue = sendQpx.get(timeKey);
			if (sendQpxVlaue != null) {
				consumerIdStatsData.setSendQps(sendQpxVlaue);
			}

			Long ackQpxValue = ackQpx.get(timeKey);
			if (ackQpxValue != null) {
				consumerIdStatsData.setAckQps(ackQpxValue.longValue());
			}

			Long sendDelayValue = sendDelay.get(timeKey);
			if (sendDelayValue != null) {
				consumerIdStatsData.setSendDelay(sendDelayValue.longValue());
			}

			Long ackDelayValue = ackDelay.get(timeKey);
			if (ackDelayValue != null) {
				consumerIdStatsData.setAckDelay(ackDelayValue.longValue());
			}

			consumerIdStatsData.setAccumulation(getConsumerIdAccumulation(topicName, consumerId, timeKey));
			consumerIdStatsDatas.add(consumerIdStatsData);

		}
		return consumerIdStatsDatas;
	}

	private long getConsumerIdAccumulation(String topic, String consumerId, long timeKey) {
		NavigableMap<Long, Long> accumulations = accumulationRetriever.getConsumerIdAccumulation(topic, consumerId);
		if (accumulations != null && !accumulations.isEmpty()) {
			Long accumulation = accumulations.get(timeKey);
			if (accumulation == null) {
				return accumulations.get(accumulations.lastKey()).longValue();
			}
			return accumulation.longValue();
		}
		return 0L;
	}

	@Override
	public Set<String> getConsumerIdIps(String topicName, String consumerId) {
		return consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.SEND);
	}

	@Override
	public Set<String> getTopicIps(String topicName) {
		Set<String> consumerIds = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName), StatisType.SEND);
		if (consumerIds != null) {
			Iterator<String> iterator = consumerIds.iterator();
			Set<String> ips = new HashSet<String>();
			while (iterator.hasNext()) {
				String consumerId = iterator.next();
				Set<String> tempIps = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName, consumerId),
						StatisType.SEND);
				ips.addAll(tempIps);
			}
			return ips;
		} else {
			return null;
		}
	}

	@Override
	public List<String> getConusmerIdInfos() {
		List<String> consumerIdInfos = new ArrayList<String>();
		Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (topicKeys != null) {
			Iterator<String> iterator = topicKeys.iterator();
			while (iterator.hasNext()) {
				String topicName = iterator.next();
				if (StringUtils.isNotBlank(topicName)) {
					List<String> partConsumerIds = getConsumerIdsByTopic(topicName);
					if (partConsumerIds == null) {
						continue;
					}
					consumerIdInfos.addAll(partConsumerIds);
				}
			}
		}
		return consumerIdInfos;
	}

	private List<String> getConsumerIdsByTopic(String topicName) {
		Set<String> consumerIds = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName), StatisType.SEND);
		List<String> consumerIdInfos = null;
		if (consumerIds != null) {
			consumerIdInfos = new ArrayList<String>();
			Iterator<String> iterator = consumerIds.iterator();
			while (iterator.hasNext()) {
				String consumerId = iterator.next();
				if (StringUtils.isNotBlank(consumerId)) {
					consumerIdInfos.add(consumerId + " " + topicName);
				}
			}
		}
		return consumerIdInfos;
	}
}
