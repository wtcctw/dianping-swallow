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
import org.mortbay.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerIdStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerTopicStatisData;
import com.dianping.swallow.web.model.statis.ConsumerBaseStatsData;
import com.dianping.swallow.web.model.statis.ConsumerIdStatsData;
import com.dianping.swallow.web.model.statis.ConsumerMachineStatsData;
import com.dianping.swallow.web.model.statis.ConsumerServerStatsData;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerDataWapper")
public class ConsumerDataWapperImpl extends AbstractDataWapper implements ConsumerDataWapper {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Override
	public ConsumerServerStatsData getServerStatsData(long timeKey) {
		Set<String> serverKeys = consumerDataRetriever.getKeys(new CasKeys());
		if (serverKeys == null) {
			return null;
		}
		Iterator<String> iterator = serverKeys.iterator();
		ConsumerServerStatsData serverStatsData = new ConsumerServerStatsData();
		List<ConsumerMachineStatsData> machineStatsDatas = new ArrayList<ConsumerMachineStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String serverIp = iterator.next();
			if (StringUtils.equals(serverIp, TOTAL_KEY)) {
				continue;
			}
			ConsumerServerStatisData serverStatisData = (ConsumerServerStatisData) consumerDataRetriever.getValue(
					new CasKeys(serverIp), StatisType.SEND);
			NavigableMap<Long, Long> sendQpx = serverStatisData.getQpx(StatisType.SEND);
			NavigableMap<Long, Long> ackQpx = serverStatisData.getQpx(StatisType.ACK);
			NavigableMap<Long, Long> sendDelay = serverStatisData.getDelay(StatisType.SEND);
			NavigableMap<Long, Long> ackDelay = serverStatisData.getDelay(StatisType.ACK);
			if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
					|| sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
				return null;
			}
			if (index == 0) {
				Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
				if (tempKey == null) {
					return null;
				}
				timeKey = tempKey.longValue();
				serverStatsData.setTimeKey(timeKey);
				index++;
			}

			ConsumerMachineStatsData machineStatsData = new ConsumerMachineStatsData();
			machineStatsData.setIp(serverIp);
			ConsumerBaseStatsData baseStatsData = new ConsumerBaseStatsData();
			baseStatsData.setSendQpx(sendQpx.get(timeKey));

			Long ackQpxValue = ackQpx.get(timeKey);
			if (ackQpxValue != null) {
				baseStatsData.setAckQpx(ackQpxValue.longValue());
			}

			Long sendDelayValue = sendDelay.get(timeKey);
			if (sendDelayValue != null) {
				baseStatsData.setSendDelay(sendDelayValue.longValue());
			}

			Long ackDelayValue = ackDelay.get(timeKey);
			if (ackDelayValue != null) {
				baseStatsData.setAckDelay(ackDelayValue.longValue());
			}

			machineStatsData.setStatisData(baseStatsData);
			machineStatsDatas.add(machineStatsData);
		}
		serverStatsData.setMachineStatisDatas(machineStatsDatas);
		return serverStatsData;
	}

	@Override
	public List<ConsumerTopicStatsData> getTopicStatsData(long timeKey) {
		Set<String> topicKeys = consumerDataRetriever.getKeys(new CasKeys(TOTAL_KEY));
		if (topicKeys == null) {
			return null;
		}
		Iterator<String> iterator = topicKeys.iterator();
		List<ConsumerTopicStatsData> topicStatsDatas = new ArrayList<ConsumerTopicStatsData>();
		int index = 0;
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (StringUtils.equals(topicName, TOTAL_KEY)) {
				continue;
			}
			ConsumerTopicStatsData topicStatsData = new ConsumerTopicStatsData();
			ConsumerTopicStatisData topicStatisData = (ConsumerTopicStatisData) consumerDataRetriever.getValue(
					new CasKeys(TOTAL_KEY, topicName), StatisType.SEND);
			if (topicStatisData == null) {
				return null;
			}
			NavigableMap<Long, Long> sendQpx = topicStatisData.getQpx(StatisType.SEND);
			NavigableMap<Long, Long> ackQpx = topicStatisData.getQpx(StatisType.ACK);
			NavigableMap<Long, Long> sendDelay = topicStatisData.getDelay(StatisType.SEND);
			NavigableMap<Long, Long> ackDelay = topicStatisData.getDelay(StatisType.ACK);
			if (sendQpx == null || sendQpx.isEmpty() || ackQpx == null || ackQpx.isEmpty() || sendDelay == null
					|| sendDelay.isEmpty() || ackDelay == null || ackDelay.isEmpty()) {
				return null;
			}
			if (index == 0) {
				Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
				if (tempKey == null) {
					return null;
				}
				timeKey = tempKey.longValue();
				index++;
			}
			ConsumerBaseStatsData baseStatsData = new ConsumerBaseStatsData();
			baseStatsData.setSendQpx(sendQpx.get(timeKey));

			Long ackQpxValue = ackQpx.get(timeKey);
			if (ackQpxValue != null) {
				baseStatsData.setAckQpx(ackQpxValue.longValue());
			}

			Long sendDelayValue = sendDelay.get(timeKey);
			if (sendDelayValue != null) {
				baseStatsData.setSendDelay(sendDelayValue.longValue());
			}

			Long ackDelayValue = ackDelay.get(timeKey);
			if (ackDelayValue != null) {
				baseStatsData.setAckDelay(ackDelayValue.longValue());
			}
			topicStatsData.setTopicName(topicName);
			topicStatsData.setTimeKey(timeKey);
			topicStatsData.setConsumerStatisData(baseStatsData);
			topicStatsDatas.add(topicStatsData);
		}
		return topicStatsDatas;
	}

	@Override
	public Map<String, List<ConsumerIdStatsData>> getConsumerIdStatsData(long timeKey) {
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
			List<ConsumerIdStatsData> consumerIdStatsDatas = getConsumerIdStatsData(topicName, timeKey);
			if (consumerIdStatsDatas == null) {
				continue;
			}
			consumerIdStatsDataMap.put(topicName, consumerIdStatsDatas);
		}
		return consumerIdStatsDataMap;
	}

	@Override
	public List<ConsumerIdStatsData> getConsumerIdStatsData(String topicName, long timeKey) {
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
			ConsumerIdStatsData consumerIdStatsData = new ConsumerIdStatsData();
			consumerIdStatsData.setConsumerId(consumerId);
			consumerIdStatsData.setTopicName(topicName);
			ConsumerIdStatisData consumerIdStatisData = (ConsumerIdStatisData) consumerDataRetriever.getValue(
					new CasKeys(TOTAL_KEY, topicName, consumerId), StatisType.SEND);
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
			if (index == 0) {
				Long tempKey = timeKey == DEFAULT_VALUE ? sendQpx.lastKey() : sendQpx.higherKey(timeKey);
				if (tempKey == null) {
					return null;
				}
				timeKey = tempKey.longValue();
				index++;
			}
			consumerIdStatsData.setTimeKey(timeKey);
			ConsumerBaseStatsData baseStatsData = new ConsumerBaseStatsData();
			baseStatsData.setSendQpx(sendQpx.get(timeKey));

			Long ackQpxValue = ackQpx.get(timeKey);
			if (ackQpxValue != null) {
				baseStatsData.setAckQpx(ackQpxValue.longValue());
			}

			Long sendDelayValue = sendDelay.get(timeKey);
			if (sendDelayValue != null) {
				baseStatsData.setSendDelay(sendDelayValue.longValue());
			}

			Long ackDelayValue = ackDelay.get(timeKey);
			if (ackDelayValue != null) {
				baseStatsData.setAckDelay(ackDelayValue.longValue());
			}

			baseStatsData.setAccumulation(getConsumerIdAccumulation(topicName, consumerId, timeKey));
			consumerIdStatsData.setStatisData(baseStatsData);
			consumerIdStatsDatas.add(consumerIdStatsData);

		}
		return consumerIdStatsDatas;
	}

	private long getConsumerIdAccumulation(String topic, String consumerId, long timeKey) {
		NavigableMap<Long, Long> accumulations = accumulationRetriever.getConsumerIdAccumulation(topic, consumerId);
		Log.info(accumulations.toString());
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

}
