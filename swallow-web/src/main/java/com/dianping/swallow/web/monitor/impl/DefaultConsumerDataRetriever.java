package com.dianping.swallow.web.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.server.monitor.data.ConsumerStatisRetriever;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisDetailType;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.OrderEntity;
import com.dianping.swallow.web.monitor.OrderStatsData;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService.StatsDataMapPair;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

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

	public static final String FACTORY_NAME = "ConsumerOrderInDb";

	@Autowired
	private ConsumerServerStatsDataService cServerStatsDataService;

	@Autowired
	private ConsumerTopicStatsDataService cTopicStatsDataService;

	@Autowired
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Autowired
	private AlarmResourceContainer resourceContainer;

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

	public ConsumerOrderDataPair getDelayOrderForAllConsumerId(int size) {
		return getDelayOrderForAllConsumerId(size, getDefaultStart(), getDefaultEnd());
	}

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

	public ConsumerOrderDataPair getQpxOrderForAllConsumerId(int size) {
		return getQpxOrderForAllConsumerId(size, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public List<OrderStatsData> getOrderForAllConsumerId(int size) {
		return getOrderForAllConsumerId(size, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public List<OrderStatsData> getOrderForAllConsumerId(int size, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getOrderInMemory(size, start, end);
		}

		return getOrderInDb(size, start, end);
	}

	public List<OrderStatsData> getOrderInMemory(int size, long start, long end) {
		ConsumerOrderDataPair delayOrderPair = getDelayOrderForAllConsumerId(size, start, end);
		ConsumerOrderDataPair qpxOrderPair = getQpxOrderForAllConsumerId(size, start, end);
		OrderStatsData accuStatsData = accumulationRetriever.getAccuOrderForAllConsumerId(size, start, end);
		List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
		orderStatsDatas.add(delayOrderPair.getSendStatsData());
		orderStatsDatas.add(delayOrderPair.getAckStatsData());
		orderStatsDatas.add(qpxOrderPair.getSendStatsData());
		orderStatsDatas.add(qpxOrderPair.getAckStatsData());
		orderStatsDatas.add(accuStatsData);
		return orderStatsDatas;
	}

	public List<OrderStatsData> getOrderInDb(int size, long start, long end) {
		long fromKey = getKey(start);
		long toKey = getKey(end);
		OrderStatsData qpxSendStatsData = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.SEND), start,
				end);
		OrderStatsData qpxAckStatsData = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.ACK), start, end);
		OrderStatsData delaySendStatsData = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.SEND),
				start, end);
		OrderStatsData delayAckStatsData = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.ACK), start,
				end);
		OrderStatsData accuStatsData = new OrderStatsData(size, new ConsumerStatsDataDesc(TOTAL_KEY,
				StatisDetailType.ACCUMULATION), start, end);
		List<ConsumerIdResource> consumerIdResources = resourceContainer.findConsumerIdResources();
		if (consumerIdResources != null) {
			QueryQrderTask queryQrderTask = new QueryQrderTask();
			for (ConsumerIdResource consumerIdResource : consumerIdResources) {
				String topicName = consumerIdResource.getTopic();
				String consumerId = consumerIdResource.getConsumerId();
				queryQrderTask.submit(new QueryOrderParam(topicName, consumerId, fromKey, toKey, qpxSendStatsData,
						qpxAckStatsData, delaySendStatsData, delayAckStatsData, accuStatsData));
			}
			queryQrderTask.await();
		}
		List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
		orderStatsDatas.add(delaySendStatsData);
		orderStatsDatas.add(delayAckStatsData);
		orderStatsDatas.add(qpxSendStatsData);
		orderStatsDatas.add(qpxAckStatsData);
		orderStatsDatas.add(accuStatsData);
		return orderStatsDatas;
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

	private ConsumerIdStatsData getPreConsumerIdStatsData(String topicName, String consumerId, long timeKey) {
		ConsumerIdStatsData consumerIdStatsData = consumerIdStatsDataService.findOneByTopicAndTimeAndConsumerId(
				topicName, timeKey, consumerId, true);
		if (consumerIdStatsData != null) {
			return consumerIdStatsData;
		}
		return new ConsumerIdStatsData();
	}

	private ConsumerIdStatsData getPostConsumerIdStatsData(String topicName, String consumerId, long timeKey) {
		ConsumerIdStatsData consumerIdStatsData = consumerIdStatsDataService.findOneByTopicAndTimeAndConsumerId(
				topicName, timeKey, consumerId, false);
		if (consumerIdStatsData != null) {
			return consumerIdStatsData;
		}
		return new ConsumerIdStatsData();
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

	private class QueryQrderTask {

		private static final int poolSize = CommonUtils.DEFAULT_CPU_COUNT * 4;
		
		private static final int MAX_WAIT_TIME =60;

		private ExecutorService executorService = Executors.newFixedThreadPool(poolSize,
				ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

		public void submit(final QueryOrderParam orderParam) {
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					String topicName = orderParam.getTopicName();
					String consumerId = orderParam.getConsumerId();
					ConsumerIdStatsData preStatsData = getPreConsumerIdStatsData(topicName, consumerId,
							orderParam.getFromKey());
					ConsumerIdStatsData postStatsData = getPostConsumerIdStatsData(topicName, consumerId,
							orderParam.getToKey());
					orderParam.getQpxSendStatsData().add(
							new OrderEntity(topicName, consumerId, postStatsData.getSendQps()
									- preStatsData.getSendQps()));
					orderParam.getQpxAckStatsData()
							.add(new OrderEntity(topicName, consumerId, postStatsData.getAckQps()
									- preStatsData.getAckQps()));
					orderParam.getDelaySendStatsData().add(
							new OrderEntity(topicName, consumerId, postStatsData.getSendDelay()
									- preStatsData.getSendDelay()));
					orderParam.getDelayAckStatsData().add(
							new OrderEntity(topicName, consumerId, postStatsData.getAckDelay()
									- preStatsData.getAckDelay()));
					orderParam.getAccuStatsData().add(
							new OrderEntity(topicName, consumerId, postStatsData.getAccumulation()
									- preStatsData.getAccumulation()));
				}
			});
		}

		public void await() {
			executorService.shutdown();
			try {
				executorService.awaitTermination(MAX_WAIT_TIME, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private static class QueryOrderParam {

		private String topicName;

		private String consumerId;

		private long fromKey;

		private long toKey;

		private OrderStatsData delaySendStatsData;

		private OrderStatsData delayAckStatsData;

		private OrderStatsData qpxSendStatsData;

		private OrderStatsData qpxAckStatsData;

		private OrderStatsData accuStatsData;

		public QueryOrderParam(String topicName, String consumerId, long fromKey, long toKey,
				OrderStatsData delaySendStatsData, OrderStatsData delayAckStatsData, OrderStatsData qpxSendStatsData,
				OrderStatsData qpxAckStatsData, OrderStatsData accuStatsData) {
			this.topicName = topicName;
			this.consumerId = consumerId;
			this.fromKey = fromKey;
			this.toKey = toKey;
			this.setDelaySendStatsData(delaySendStatsData);
			this.setDelayAckStatsData(delayAckStatsData);
			this.setQpxSendStatsData(qpxSendStatsData);
			this.setQpxAckStatsData(qpxAckStatsData);
			this.setAccuStatsData(accuStatsData);
		}

		public String getTopicName() {
			return topicName;
		}

		public long getFromKey() {
			return fromKey;
		}

		public long getToKey() {
			return toKey;
		}

		public OrderStatsData getDelaySendStatsData() {
			return delaySendStatsData;
		}

		public void setDelaySendStatsData(OrderStatsData delaySendStatsData) {
			this.delaySendStatsData = delaySendStatsData;
		}

		public OrderStatsData getDelayAckStatsData() {
			return delayAckStatsData;
		}

		public void setDelayAckStatsData(OrderStatsData delayAckStatsData) {
			this.delayAckStatsData = delayAckStatsData;
		}

		public OrderStatsData getQpxSendStatsData() {
			return qpxSendStatsData;
		}

		public void setQpxSendStatsData(OrderStatsData qpxSendStatsData) {
			this.qpxSendStatsData = qpxSendStatsData;
		}

		public OrderStatsData getQpxAckStatsData() {
			return qpxAckStatsData;
		}

		public void setQpxAckStatsData(OrderStatsData qpxAckStatsData) {
			this.qpxAckStatsData = qpxAckStatsData;
		}

		public OrderStatsData getAccuStatsData() {
			return accuStatsData;
		}

		public void setAccuStatsData(OrderStatsData accuStatsData) {
			this.accuStatsData = accuStatsData;
		}

		public String getConsumerId() {
			return consumerId;
		}

	}

}
