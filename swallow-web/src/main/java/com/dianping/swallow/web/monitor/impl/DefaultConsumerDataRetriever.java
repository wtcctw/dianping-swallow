package com.dianping.swallow.web.monitor.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.ConsumerStatisRetriever;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.OrderEntity;
import com.dianping.swallow.web.monitor.OrderStatsData;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService.StatsDataMapPair;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;

/**
 * @author mengwenchao
 *
 *         2015年4月21日 上午11:04:09
 */
@Component
public class DefaultConsumerDataRetriever
		extends
		AbstractMonitorDataRetriever<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData>
		implements ConsumerDataRetriever {

	public static final String CAT_TYPE = "ConsumerDataRetriever";

	@Autowired
	private ConsumerServerStatsDataService cServerStatsDataService;

	@Autowired
	private ConsumerTopicStatsDataService cTopicStatsDataService;

	@Autowired
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Override
	public boolean dataExistInMemory(long start, long end) {
		NavigableMap<Long, Long> qpxStatsData = statis.getQpx(StatisType.SEND);

		if (qpxStatsData == null || qpxStatsData.isEmpty()) {
			return false;
		}

		Long firstKey = statis.getQpx(StatisType.SEND).firstKey();
		if (firstKey != null) {
			if (getKey(start) >= firstKey.longValue()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public ConsumerOrderDataPair getDelayOrderForAllConsumerId(int size, long start, long end) {
		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
		long fromKey = getKey(start);
		long toKey = getKey(end);
		OrderStatsData sendStatsData = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.SEND), start, end);
		OrderStatsData ackStatsData = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.ACK), start, end);
		ConsumerOrderDataPair orderDataResult = new ConsumerOrderDataPair(sendStatsData, ackStatsData);
		Set<String> topics = retriever.getTopics(false);
		if (topics == null) {
			return null;
		}
		Iterator<String> iterator = topics.iterator();
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (TOTAL_KEY.equals(topicName)) {
				continue;
			}
			Map<String, NavigableMap<Long, Long>> sendDelays = retriever.getDelayForAllConsumerId(topicName,
					StatisType.SEND, false);
			Map<String, NavigableMap<Long, Long>> ackDelays = retriever.getDelayForAllConsumerId(topicName,
					StatisType.ACK, false);
			if (sendDelays != null) {
				for (Map.Entry<String, NavigableMap<Long, Long>> sendDelay : sendDelays.entrySet()) {
					if (TOTAL_KEY.equals(sendDelay.getKey())) {
						continue;
					}
					sendStatsData.add(new OrderEntity(topicName, sendDelay.getKey(), getSumStatsData(
							sendDelay.getValue(), fromKey, toKey)));
				}
			}

			if (ackDelays != null) {
				for (Map.Entry<String, NavigableMap<Long, Long>> ackDelay : ackDelays.entrySet()) {
					if (TOTAL_KEY.equals(ackDelay.getKey())) {
						continue;
					}
					ackStatsData.add(new OrderEntity(topicName, ackDelay.getKey(), getSumStatsData(ackDelay.getValue(),
							fromKey, toKey)));
				}
			}

		}
		return orderDataResult;
	}

	@Override
	public ConsumerOrderDataPair getDelayOrderForAllConsumerId(int size) {
		return getDelayOrderForAllConsumerId(size, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public ConsumerOrderDataPair getQpxOrderForAllConsumerId(int size, long start, long end) {
		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
		long fromKey = getKey(start);
		long toKey = getKey(end);
		OrderStatsData sendStatsData = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.SEND), start, end);
		OrderStatsData ackStatsData = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.ACK), start, end);
		ConsumerOrderDataPair orderDataResult = new ConsumerOrderDataPair(sendStatsData, ackStatsData);
		Set<String> topics = retriever.getTopics(false);
		if (topics == null) {
			return null;
		}
		Iterator<String> iterator = topics.iterator();
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (TOTAL_KEY.equals(topicName)) {
				continue;
			}
			Map<String, NavigableMap<Long, Long>> sendQpxs = retriever.getQpxForAllConsumerId(topicName,
					StatisType.SEND, false);
			Map<String, NavigableMap<Long, Long>> ackQpxs = retriever.getQpxForAllConsumerId(topicName, StatisType.ACK,
					false);
			if (sendQpxs != null) {
				for (Map.Entry<String, NavigableMap<Long, Long>> sendQpx : sendQpxs.entrySet()) {
					if (TOTAL_KEY.equals(sendQpx.getKey())) {
						continue;
					}
					sendStatsData.add(new OrderEntity(topicName, sendQpx.getKey(), getSumStatsData(sendQpx.getValue(),
							fromKey, toKey)));
				}
			}

			if (ackQpxs != null) {
				for (Map.Entry<String, NavigableMap<Long, Long>> ackQpx : ackQpxs.entrySet()) {
					if (TOTAL_KEY.equals(ackQpx.getKey())) {
						continue;
					}
					ackStatsData.add(new OrderEntity(topicName, ackQpx.getKey(), getSumStatsData(ackQpx.getValue(),
							fromKey, toKey)));
				}
			}

		}

		return orderDataResult;
	}

	@Override
	public ConsumerOrderDataPair getQpxOrderForAllConsumerId(int size) {
		return getQpxOrderForAllConsumerId(size, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public List<ConsumerDataPair> getDelayForAllConsumerId(String topic, long start, long end) {

		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
		Map<String, NavigableMap<Long, Long>> sendDelays = null;
		Map<String, NavigableMap<Long, Long>> ackDelays = null;
		List<ConsumerDataPair> result = new LinkedList<ConsumerDataRetriever.ConsumerDataPair>();
		long startKey = getKey(start);
		long endKey = getKey(end);
		if (dataExistInMemory(start, end)) {
			sendDelays = retriever.getDelayForAllConsumerId(topic, StatisType.SEND, false);
			ackDelays = retriever.getDelayForAllConsumerId(topic, StatisType.ACK, false);
			if (sendDelays != null) {

				for (Entry<String, NavigableMap<Long, Long>> entry : sendDelays.entrySet()) {

					String consumerId = entry.getKey();
					NavigableMap<Long, Long> send = entry.getValue();
					NavigableMap<Long, Long> ack = ackDelays.get(consumerId);
					if (send == null) {
						continue;
					}
					send = send.subMap(startKey, true, endKey, true);
					ack = ack.subMap(startKey, true, endKey, true);
					StatsData sendStatis = createStatsData(
							createConsumerIdDelayDesc(topic, consumerId, StatisType.SEND), send, start, end);
					StatsData ackStatis = createStatsData(createConsumerIdDelayDesc(topic, consumerId, StatisType.ACK),
							ack, start, end);
					result.add(new ConsumerDataPair(consumerId, sendStatis, ackStatis));
				}
			}
		} else {
			Map<String, StatsDataMapPair> statsDataResults = getTopicDelayInDb(topic, start, end);
			boolean isTotal = false;
			if (MonitorData.TOTAL_KEY.equals(topic)) {
				isTotal = true;
			}
			if (statsDataResults != null && !statsDataResults.isEmpty()) {
				for (Map.Entry<String, StatsDataMapPair> statsDataResult : statsDataResults.entrySet()) {
					if (!isTotal && MonitorData.TOTAL_KEY.equals(statsDataResult.getKey())) {
						continue;
					}
					StatsDataMapPair statsDataMapPair = statsDataResult.getValue();
					NavigableMap<Long, Long> sendRawData = null;
					NavigableMap<Long, Long> ackRawData = null;
					if (statsDataMapPair != null) {
						sendRawData = statsDataMapPair.getSendStatsData();
						ackRawData = statsDataMapPair.getSendStatsData();
					}
					sendRawData = fillStatsData(sendRawData, startKey, endKey);
					StatsData sendStatis = createStatsData(
							createConsumerIdDelayDesc(topic, statsDataResult.getKey(), StatisType.SEND), sendRawData,
							start, end);
					ackRawData = fillStatsData(ackRawData, startKey, endKey);
					StatsData ackStatis = createStatsData(
							createConsumerIdDelayDesc(topic, statsDataResult.getKey(), StatisType.ACK), ackRawData,
							start, end);
					result.add(new ConsumerDataPair(statsDataResult.getKey(), sendStatis, ackStatis));
				}
			}
		}

		return result;
	}

	protected Map<String, StatsDataMapPair> getTopicDelayInDb(String topic, long start, long end) {
		long startKey = getKey(start);
		long endKey = getKey(end);
		Map<String, StatsDataMapPair> statsDataResults = null;
		if (MonitorData.TOTAL_KEY.equals(topic)) {
			StatsDataMapPair statsDataResult = cTopicStatsDataService.findSectionDelayData(MonitorData.TOTAL_KEY,
					startKey, endKey);
			statsDataResults = new HashMap<String, StatsDataMapPair>();
			statsDataResults.put(topic, statsDataResult);
		} else {
			statsDataResults = consumerIdStatsDataService.findSectionQpsData(topic, startKey, endKey);
		}
		return statsDataResults;
	}

	@Override
	public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx, long start, long end) {

		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;
		Map<String, NavigableMap<Long, Long>> sendQpxs = null;
		Map<String, NavigableMap<Long, Long>> ackQpxs = null;
		List<ConsumerDataPair> result = new LinkedList<ConsumerDataRetriever.ConsumerDataPair>();
		long startKey = getKey(start);
		long endKey = getKey(end);
		if (dataExistInMemory(start, end)) {
			sendQpxs = retriever.getQpxForAllConsumerId(topic, StatisType.SEND, false);
			ackQpxs = retriever.getQpxForAllConsumerId(topic, StatisType.ACK, false);
			if (sendQpxs != null) {
				for (Entry<String, NavigableMap<Long, Long>> entry : sendQpxs.entrySet()) {

					String consumerId = entry.getKey();
					NavigableMap<Long, Long> send = entry.getValue();
					NavigableMap<Long, Long> ack = ackQpxs.get(consumerId);
					if (send == null) {
						continue;
					}
					send = send.subMap(startKey, true, endKey, true);
					ack = ack.subMap(startKey, true, endKey, true);
					StatsData sendStatis = createStatsData(createConsumerIdQpxDesc(topic, consumerId, StatisType.SEND),
							send, start, end);
					StatsData ackStatis = createStatsData(createConsumerIdQpxDesc(topic, consumerId, StatisType.ACK),
							ack, start, end);

					result.add(new ConsumerDataPair(consumerId, sendStatis, ackStatis));
				}
			}
		} else {
			Map<String, StatsDataMapPair> statsDataResults = getTopicQpxInDb(topic, qpx, start, end);
			boolean isTotal = false;
			if (MonitorData.TOTAL_KEY.equals(topic)) {
				isTotal = true;
			}
			if (statsDataResults != null && !statsDataResults.isEmpty()) {
				for (Map.Entry<String, StatsDataMapPair> statsDataResult : statsDataResults.entrySet()) {
					if (!isTotal && MonitorData.TOTAL_KEY.equals(statsDataResult.getKey())) {
						continue;
					}
					StatsDataMapPair statsDataMapPair = statsDataResult.getValue();
					NavigableMap<Long, Long> sendRawData = null;
					NavigableMap<Long, Long> ackRawData = null;
					if (statsDataMapPair != null) {
						sendRawData = statsDataMapPair.getSendStatsData();
						ackRawData = statsDataMapPair.getSendStatsData();
					}
					sendRawData = fillStatsData(sendRawData, startKey, endKey);
					StatsData sendStatis = createStatsData(
							createConsumerIdQpxDesc(topic, statsDataResult.getKey(), StatisType.SEND), sendRawData,
							start, end);

					ackRawData = fillStatsData(ackRawData, startKey, endKey);
					StatsData ackStatis = createStatsData(
							createConsumerIdQpxDesc(topic, statsDataResult.getKey(), StatisType.ACK), ackRawData,
							start, end);
					result.add(new ConsumerDataPair(statsDataResult.getKey(), sendStatis, ackStatis));
				}
			}
		}

		return result;
	}

	protected Map<String, StatsDataMapPair> getTopicQpxInDb(String topic, QPX qpx, long start, long end) {
		long startKey = getKey(start);
		long endKey = getKey(end);
		Map<String, StatsDataMapPair> statsDataResults = null;
		if (MonitorData.TOTAL_KEY.equals(topic)) {
			StatsDataMapPair statsDataResult = cTopicStatsDataService.findSectionQpsData(MonitorData.TOTAL_KEY,
					startKey, endKey);
			statsDataResults = new HashMap<String, StatsDataMapPair>();
			statsDataResults.put(topic, statsDataResult);
		} else {
			statsDataResults = consumerIdStatsDataService.findSectionQpsData(topic, startKey, endKey);
		}
		return statsDataResults;
	}

	@Override
	public Map<String, ConsumerDataPair> getServerQpx(QPX qpx, long start, long end) {

		Map<String, StatsData> sendQpxs = null;

		Map<String, StatsData> ackQpxs = null;

		if (dataExistInMemory(start, end)) {
			sendQpxs = getServerQpxInMemory(qpx, StatisType.SEND, start, end);
			ackQpxs = getServerQpxInMemory(qpx, StatisType.ACK, start, end);
		} else {
			StatsDataPair statsDataPair = getServerQpxInDb(qpx, start, end);
			sendQpxs = statsDataPair.getSendStatsDatas();
			ackQpxs = statsDataPair.getSendStatsDatas();
		}

		Map<String, ConsumerDataPair> result = new HashMap<String, ConsumerDataRetriever.ConsumerDataPair>();
		for (Entry<String, StatsData> entry : sendQpxs.entrySet()) {

			String serverIp = entry.getKey();
			StatsData sendQpx = entry.getValue();
			StatsData ackQpx = ackQpxs.get(serverIp);
			result.put(serverIp, new ConsumerDataPair(getConsumerIdSubTitle(MonitorData.TOTAL_KEY), sendQpx, ackQpx));
		}

		return result;
	}

	protected StatsDataPair getServerQpxInDb(QPX qpx, long start, long end) {
		StatsDataPair statsDataPair = new StatsDataPair();
		Map<String, StatsData> sendStatsDatas = new HashMap<String, StatsData>();
		Map<String, StatsData> ackStatsDatas = new HashMap<String, StatsData>();
		long startKey = getCeilingTime(start);
		long endKey = getCeilingTime(end);
		Map<String, StatsDataMapPair> statsDataPairMaps = cServerStatsDataService.findSectionQpsData(startKey, endKey);

		for (Map.Entry<String, StatsDataMapPair> qpsStatsDataMap : statsDataPairMaps.entrySet()) {
			String serverIp = qpsStatsDataMap.getKey();

			if (StringUtils.equals(TOTAL_KEY, serverIp)) {
				continue;
			}

			StatsDataMapPair statsDataMapPair = qpsStatsDataMap.getValue();
			NavigableMap<Long, Long> sendStatsData = null;
			NavigableMap<Long, Long> ackStatsData = null;
			if (statsDataMapPair != null) {
				sendStatsData = statsDataMapPair.getSendStatsData();
				ackStatsData = statsDataMapPair.getSendStatsData();
			}
			sendStatsData = fillStatsData(sendStatsData, startKey, endKey);
			ackStatsData = fillStatsData(ackStatsData, startKey, endKey);
			sendStatsDatas.put(serverIp,
					createStatsData(createServerQpxDesc(serverIp, StatisType.SEND), sendStatsData, start, end));

			ackStatsDatas.put(serverIp,
					createStatsData(createServerQpxDesc(serverIp, StatisType.ACK), ackStatsData, start, end));

		}
		statsDataPair.setAckStatsDatas(ackStatsDatas);
		statsDataPair.setSendStatsDatas(sendStatsDatas);
		return statsDataPair;
	}

	private String getConsumerIdSubTitle(String consumerId) {
		if (consumerId.equals(MonitorData.TOTAL_KEY)) {
			return "全局平均";
		}
		return "consumerID:" + consumerId;
	}

	@Override
	public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx) {

		return getQpxForAllConsumerId(topic, qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public Map<String, ConsumerDataPair> getServerQpx(QPX qpx) {

		return getServerQpx(qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public List<ConsumerDataPair> getDelayForAllConsumerId(String topic) throws Exception {

		return getDelayForAllConsumerId(topic, getDefaultStart(), getDefaultEnd());
	}

	@Override
	protected AbstractAllData<ConsumerTopicData, ConsumerServerData, ConsumerServerStatisData, ConsumerMonitorData> createServerStatis() {

		return new ConsumerAllData();
	}

	@Override
	protected StatsDataDesc createDelayDesc(String topic, StatisType type) {

		return new ConsumerStatsDataDesc(topic, type.getDelayDetailType());
	}

	@Override
	protected StatsDataDesc createQpxDesc(String topic, StatisType type) {

		return new ConsumerStatsDataDesc(topic, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerQpxDesc(String serverIp, StatisType type) {

		return new ConsumerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerDelayDesc(String serverIp, StatisType type) {

		return new ConsumerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getDelayDetailType());
	}

	protected StatsDataDesc createConsumerIdDelayDesc(String topic, String consumerId, StatisType type) {

		return new ConsumerStatsDataDesc(topic, consumerId, type.getDelayDetailType());
	}

	protected StatsDataDesc createConsumerIdQpxDesc(String topic, String consumerId, StatisType type) {

		return new ConsumerStatsDataDesc(topic, consumerId, type.getQpxDetailType());
	}

	@Override
	public Map<String, Set<String>> getAllTopics() {

		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;

		return retriever.getAllTopics();
	}

	public static class StatsDataPair {

		private Map<String, StatsData> sendStatsDatas;

		private Map<String, StatsData> ackStatsDatas;

		public Map<String, StatsData> getSendStatsDatas() {
			return sendStatsDatas;
		}

		public void setSendStatsDatas(Map<String, StatsData> sendStatsDatas) {
			this.sendStatsDatas = sendStatsDatas;
		}

		public Map<String, StatsData> getAckStatsDatas() {
			return ackStatsDatas;
		}

		public void setAckStatsDatas(Map<String, StatsData> ackStatsDatas) {
			this.ackStatsDatas = ackStatsDatas;
		}

	}
}
