package com.dianping.swallow.web.monitor.wapper;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerIdStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerTopicStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.MessageInfoStatis;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;
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
	public List<ConsumerServerStatsData> getServerStatsDatas(long timeKey, boolean isTotal) {
		Set<String> serverKeys = consumerDataRetriever.getKeys(new CasKeys());
		if (serverKeys == null) {
			return null;
		}
		Iterator<String> iterator = serverKeys.iterator();
		List<ConsumerServerStatsData> serverStatsDatas = new ArrayList<ConsumerServerStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String serverIp = iterator.next();
			if (!isTotal && TOTAL_KEY.equals(serverIp)) {
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
	public List<ConsumerIdStatsData> getConsumerIdStatsDatas(long timeKey, boolean isTotal) {
		Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (topicKeys == null) {
			return null;
		}
		Iterator<String> iterator = topicKeys.iterator();
		List<ConsumerIdStatsData> consumerIdStatsDataResults = new ArrayList<ConsumerIdStatsData>();
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (!isTotal && TOTAL_KEY.equals(topicName)) {
				continue;
			}
			List<ConsumerIdStatsData> consumerIdStatsDatas = getConsumerIdStatsDatas(topicName, timeKey, isTotal);
			if (consumerIdStatsDatas == null) {
				continue;
			}
			consumerIdStatsDataResults.addAll(consumerIdStatsDatas);
		}
		return consumerIdStatsDataResults;
	}

	@Override
	public List<ConsumerIdStatsData> getConsumerIdStatsDatas(String topicName, long timeKey, boolean isTotal) {
		Set<String> consumerIdKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
		if (consumerIdKeys == null) {
			return null;
		}
		Iterator<String> iterator = consumerIdKeys.iterator();
		List<ConsumerIdStatsData> consumerIdStatsDatas = new ArrayList<ConsumerIdStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String consumerId = iterator.next();
			if (!isTotal && TOTAL_KEY.equals(topicName)) {
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

	@Override
	public List<ConsumerIpStatsData> getIpStatsDatas(long timeKey, boolean isTotal) {
		Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (topicKeys == null) {
			return null;
		}
		Iterator<String> iterator = topicKeys.iterator();
		List<ConsumerIpStatsData> ipStatsDataResults = new ArrayList<ConsumerIpStatsData>();
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (!isTotal && TOTAL_KEY.equals(topicName)) {
				continue;
			}
			List<ConsumerIpStatsData> ipStatsDatas = getIpStatsDatas(topicName, timeKey, isTotal);
			if (ipStatsDatas != null) {
				ipStatsDataResults.addAll(ipStatsDatas);
			}
		}
		return ipStatsDataResults;
	}

	@Override
	public List<ConsumerIpStatsData> getIpStatsDatas(String topicName, long timeKey, boolean isTotal) {
		Set<String> consumerIdKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
		if (consumerIdKeys == null) {
			return null;
		}
		Iterator<String> iterator = consumerIdKeys.iterator();
		List<ConsumerIpStatsData> ipStatsDatasResults = new ArrayList<ConsumerIpStatsData>();
		while (iterator.hasNext()) {
			String consumerId = iterator.next();
			if (!isTotal && TOTAL_KEY.equals(consumerId)) {
				continue;
			}
			List<ConsumerIpStatsData> ipStatsDatas = getIpStatsDatas(topicName, consumerId, timeKey, isTotal);
			if (ipStatsDatas != null) {
				ipStatsDatasResults.addAll(ipStatsDatas);
			}
		}
		return ipStatsDatasResults;
	}

	@Override
	public List<ConsumerIpStatsData> getIpStatsDatas(String topicName, String consumerId, long timeKey, boolean isTotal) {
		Set<String> ipKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName, consumerId));
		if (ipKeys == null) {
			return null;
		}
		int index = 0;
		List<ConsumerIpStatsData> ipStatsDatas = new ArrayList<ConsumerIpStatsData>();
		for (String ip : ipKeys) {
			if (!isTotal && TOTAL_KEY.equals(ip)) {
				continue;
			}
			MessageInfoStatis sendMStatisData = (MessageInfoStatis) consumerDataRetriever.getValue(new CasKeys(
					TOTAL_KEY, topicName, consumerId, ip), StatisType.SEND);

			MessageInfoStatis ackMStatisData = (MessageInfoStatis) consumerDataRetriever.getValue(new CasKeys(
					TOTAL_KEY, topicName, consumerId, ip), StatisType.ACK);

			if (sendMStatisData == null && ackMStatisData == null) {
				continue;
			}
			NavigableMap<Long, Long> sendQpx = sendMStatisData.getQpx(StatisType.SEND);
			NavigableMap<Long, Long> sendDelay = sendMStatisData.getDelay(StatisType.SEND);
			NavigableMap<Long, Long> ackQpx = ackMStatisData.getQpx(StatisType.ACK);
			NavigableMap<Long, Long> ackDelay = ackMStatisData.getDelay(StatisType.ACK);

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

			ConsumerIpStatsData consumerIpStatsData = statsDataFactory.createConsumerIpStatsData();
			consumerIpStatsData.setTopicName(topicName);
			consumerIpStatsData.setConsumerId(consumerId);
			consumerIpStatsData.setIp(ip);
			consumerIpStatsData.setTimeKey(timeKey);
			Long sendQpxValue = sendQpx.get(timeKey);

			if (sendQpxValue != null) {
				consumerIpStatsData.setSendQps(sendQpxValue.longValue());
			}

			Long sendDelayValue = sendDelay.get(timeKey);

			if (sendDelayValue != null) {
				consumerIpStatsData.setSendDelay(sendDelayValue.longValue());
			}

			Long ackQpxValue = ackQpx.get(timeKey);

			if (ackQpxValue != null) {
				consumerIpStatsData.setAckQps(ackQpxValue.longValue());
			}
			Long ackDelayValue = ackDelay.get(timeKey);

			if (ackDelayValue != null) {
				consumerIpStatsData.setAckDelay(ackDelayValue.longValue());
			}
			consumerIpStatsData.setAccumulation(0L);
			ipStatsDatas.add(consumerIpStatsData);
		}
		return ipStatsDatas;
	}

	@Override
	public List<ConsumerIpGroupStatsData> getIpGroupStatsDatas(long timeKey, boolean isTotal) {
		Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (topicKeys == null) {
			return null;
		}
		Iterator<String> iterator = topicKeys.iterator();
		List<ConsumerIpGroupStatsData> ipGroupStatsDataResults = new ArrayList<ConsumerIpGroupStatsData>();
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (!isTotal && TOTAL_KEY.equals(topicName)) {
				continue;
			}
			List<ConsumerIpGroupStatsData> ipGroupStatsDatas = getIpGroupStatsDatas(topicName, timeKey, isTotal);
			if (ipGroupStatsDatas != null) {
				ipGroupStatsDataResults.addAll(ipGroupStatsDatas);
			}
		}
		return ipGroupStatsDataResults;
	}

	@Override
	public List<ConsumerIpGroupStatsData> getIpGroupStatsDatas(String topicName, long timeKey, boolean isTotal) {
		Set<String> consumerIdKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName));
		if (consumerIdKeys == null) {
			return null;
		}
		Iterator<String> iterator = consumerIdKeys.iterator();
		List<ConsumerIpGroupStatsData> ipGroupStatsDatas = new ArrayList<ConsumerIpGroupStatsData>();
		while (iterator.hasNext()) {
			String consumerId = iterator.next();
			if (!isTotal && TOTAL_KEY.equals(consumerId)) {
				continue;
			}
			ConsumerIpGroupStatsData ipGroupStatsData = getIpGroupStatsDatas(topicName, consumerId, timeKey, isTotal);
			ipGroupStatsDatas.add(ipGroupStatsData);
		}

		return ipGroupStatsDatas;
	}

	@Override
	public ConsumerIpGroupStatsData getIpGroupStatsDatas(String topicName, String consumerId, long timeKey,
			boolean isTotal) {
		ConsumerIpGroupStatsData ipGroupStatsData = new ConsumerIpGroupStatsData();
		ipGroupStatsData.setConsumerIpStatsDatas(getIpStatsDatas(topicName, consumerId, timeKey, isTotal));
		return ipGroupStatsData;
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
	public ConsumerTopicStatsData getTotalTopicStatsData(long timeKey) {
		ConsumerTopicStatsData consumerTopicStatsData = statsDataFactory.createConsumerTopicStatsData();
		consumerTopicStatsData.setTopicName(TOTAL_KEY);
		ConsumerTopicStatisData consumerIdStatisData = (ConsumerTopicStatisData) consumerDataRetriever.getValue(
				new CasKeys(TOTAL_KEY, TOTAL_KEY), StatisType.SEND);
		if (consumerIdStatisData == null) {
			return null;
		}
		NavigableMap<Long, Long> sendQpx = consumerIdStatisData.getQpx(StatisType.SEND);
		NavigableMap<Long, Long> ackQpx = consumerIdStatisData.getQpx(StatisType.ACK);
		NavigableMap<Long, Long> sendDelay = consumerIdStatisData.getDelay(StatisType.SEND);
		NavigableMap<Long, Long> ackDelay = consumerIdStatisData.getDelay(StatisType.ACK);

		if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
				|| sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
			return null;
		}
		Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
		if (tempKey == null) {
			return null;
		}
		timeKey = tempKey.longValue();
		consumerTopicStatsData.setTimeKey(timeKey);
		Long sendQpxVlaue = sendQpx.get(timeKey);
		if (sendQpxVlaue != null) {
			consumerTopicStatsData.setSendQps(sendQpxVlaue);
		}

		Long ackQpxValue = ackQpx.get(timeKey);
		if (ackQpxValue != null) {
			consumerTopicStatsData.setAckQps(ackQpxValue.longValue());
		}

		Long sendDelayValue = sendDelay.get(timeKey);
		if (sendDelayValue != null) {
			consumerTopicStatsData.setSendDelay(sendDelayValue.longValue());
		}

		Long ackDelayValue = ackDelay.get(timeKey);
		if (ackDelayValue != null) {
			consumerTopicStatsData.setAckDelay(ackDelayValue.longValue());
		}

		consumerTopicStatsData.setAccumulation(0L);

		return consumerTopicStatsData;
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

	@Override
	public Set<String> getTopics(boolean isTotal) {
		Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (!isTotal && topicKeys != null) {
			if (topicKeys.contains(TOTAL_KEY)) {
				topicKeys.remove(TOTAL_KEY);
			}
		}
		return topicKeys;
	}

	@Override
	public Set<String> getServerIps(boolean isTotal) {
		Set<String> serverIps = consumerDataRetriever.getKeys(new CasKeys());
		if (!isTotal && serverIps != null) {
			if (serverIps.contains(TOTAL_KEY)) {
				serverIps.remove(TOTAL_KEY);
			}
		}
		return serverIps;
	}

	@Override
	public Set<String> getConsumerIdIps(String topicName, String consumerId, boolean isTotal) {
		Set<String> consumerIdIps = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName, consumerId),
				StatisType.SEND);
		if (!isTotal && consumerIdIps != null) {
			if (consumerIdIps.contains(TOTAL_KEY)) {
				consumerIdIps.remove(TOTAL_KEY);
			}
		}
		return consumerIdIps;
	}

	@Override
	public Set<String> getTopicIps(String topicName, boolean isTotal) {
		Set<String> consumerIds = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY, topicName), StatisType.SEND);
		if (consumerIds != null) {
			Iterator<String> iterator = consumerIds.iterator();
			Set<String> ips = new HashSet<String>();
			while (iterator.hasNext()) {
				String consumerId = iterator.next();
				Set<String> tempIps = getConsumerIdIps(topicName, consumerId, false);
				ips.addAll(tempIps);
			}
			return ips;
		} else {
			return null;
		}
	}

	@Override
	public Set<String> getIps(boolean isTotal) {
		Set<String> ips = new HashSet<String>();
		Set<String> topics = getTopics(false);
		if (topics != null) {
			for (String topic : topics) {
				Set<String> topicIps = getTopicIps(topic, false);
				if (topicIps != null) {
					ips.addAll(topicIps);
				}
			}
		}
		Set<String> serverIps = getServerIps(false);
		if (serverIps != null) {
			ips.addAll(serverIps);
		}
		return ips;
	}

}