package com.dianping.swallow.web.monitor.impl;

import java.util.HashMap;
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
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService.QpsStatsDataPair;

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
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Override
	public List<ConsumerDataPair> getDelayForAllConsumerId(String topic, long start, long end) {

		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;

		Map<String, NavigableMap<Long, Long>> sendDelays = retriever.getDelayForAllConsumerId(topic, StatisType.SEND,
				false);
		Map<String, NavigableMap<Long, Long>> ackDelays = retriever.getDelayForAllConsumerId(topic, StatisType.ACK,
				false);

		List<ConsumerDataPair> result = new LinkedList<ConsumerDataRetriever.ConsumerDataPair>();

		if (sendDelays != null) {

			for (Entry<String, NavigableMap<Long, Long>> entry : sendDelays.entrySet()) {

				String consumerId = entry.getKey();
				NavigableMap<Long, Long> send = entry.getValue();
				NavigableMap<Long, Long> ack = ackDelays.get(consumerId);

				StatsData sendStatis = createStatsData(createConsumerIdDelayDesc(topic, consumerId, StatisType.SEND),
						send, start, end);
				StatsData ackStatis = createStatsData(createConsumerIdDelayDesc(topic, consumerId, StatisType.ACK),
						ack, start, end);
				result.add(new ConsumerDataPair(consumerId, sendStatis, ackStatis));
			}
		}

		return result;
	}

	@Override
	public List<ConsumerDataPair> getQpxForAllConsumerId(String topic, QPX qpx, long start, long end) {

		ConsumerStatisRetriever retriever = (ConsumerStatisRetriever) statis;

		Map<String, NavigableMap<Long, Long>> sendQpxs = retriever
				.getQpxForAllConsumerId(topic, StatisType.SEND, false);
		Map<String, NavigableMap<Long, Long>> ackQpxs = retriever.getQpxForAllConsumerId(topic, StatisType.ACK, false);

		List<ConsumerDataPair> result = new LinkedList<ConsumerDataRetriever.ConsumerDataPair>();

		if (sendQpxs != null) {
			for (Entry<String, NavigableMap<Long, Long>> entry : sendQpxs.entrySet()) {

				String consumerId = entry.getKey();
				NavigableMap<Long, Long> send = entry.getValue();
				NavigableMap<Long, Long> ack = ackQpxs.get(consumerId);

				StatsData sendStatis = createStatsData(createConsumerIdQpxDesc(topic, consumerId, StatisType.SEND),
						send, start, end);
				StatsData ackStatis = createStatsData(createConsumerIdQpxDesc(topic, consumerId, StatisType.ACK), ack,
						start, end);

				result.add(new ConsumerDataPair(consumerId, sendStatis, ackStatis));
			}
		}
		return result;
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
		long startTimeKey = getCeilingTime(start);
		long endTimeKey = getCeilingTime(end);
		Map<String, QpsStatsDataPair> statsDataPairMaps = cServerStatsDataService.findSectionQpsData(startTimeKey,
				endTimeKey);

		for (Map.Entry<String, QpsStatsDataPair> qpsStatsDataMap : statsDataPairMaps.entrySet()) {
			String serverIp = qpsStatsDataMap.getKey();

			if (StringUtils.equals(TOTAL_KEY, serverIp)) {
				continue;
			}

			QpsStatsDataPair qpsStatsDataPair = qpsStatsDataMap.getValue();
			NavigableMap<Long, Long> sendStatsData = qpsStatsDataPair.getSendStatsData();
			sendStatsData = fillStatsData(sendStatsData, startTimeKey, endTimeKey);
			NavigableMap<Long, Long> ackStatsData = qpsStatsDataPair.getAckStatsData();
			ackStatsData = fillStatsData(ackStatsData, startTimeKey, endTimeKey);
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
