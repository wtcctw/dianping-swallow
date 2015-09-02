package com.dianping.swallow.web.monitor.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowCallableWrapper;
import com.dianping.swallow.common.internal.action.impl.CatCallableWrapper;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerAllData;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.dao.ProducerMonitorDao;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.OrderEntity;
import com.dianping.swallow.web.monitor.OrderStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataDesc;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * @author mengwenchao
 *
 *         2015年4月21日 上午11:04:09
 */
@Component
public class DefaultProducerDataRetriever
		extends
		AbstractMonitorDataRetriever<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData>
		implements ProducerDataRetriever {

	public static final String CAT_TYPE = "DefaultProducerDataRetriever";

	public static final String FACTORY_NAME = "ProducerOrderInDb";

	@Autowired
	private ProducerServerStatsDataService pServerStatsDataService;

	@Autowired
	private ProducerTopicStatsDataService pTopicStatsDataService;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Autowired
	private ProducerMonitorDao producerMonitorDao;

	@Override
	public boolean dataExistInMemory(long start, long end) {
		NavigableMap<Long, Long> qpxStatsData = statis.getQpx(StatisType.SAVE);
		if (qpxStatsData == null || qpxStatsData.isEmpty()) {
			return false;
		}
		Long firstKey = statis.getQpx(StatisType.SAVE).firstKey();
		if (firstKey != null) {
			if (getKey(start) - getKey(OFFSET_TIMESPAN) >= firstKey.longValue()) {
				return true;
			}
		}
		return false;
	}

	public OrderStatsData getDelayOrder(int size, long start, long end) {
		return getDelayOrderInMemory(size, StatisType.SAVE, start, end);
	}

	protected OrderStatsData getDelayOrderInMemory(int size, StatisType type, long start, long end) {
		Set<String> topics = statis.getTopics(false);
		if (topics == null) {
			return null;
		}
		OrderStatsData orderResults = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, type), start, end);
		long fromKey = getKey(start);
		long toKey = getKey(end);
		Iterator<String> iterator = topics.iterator();
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (TOTAL_KEY.equals(topicName)) {
				continue;
			}
			NavigableMap<Long, Long> rawDatas = statis.getDelayForTopic(topicName, type);
			orderResults.add(new OrderEntity(topicName, StringUtils.EMPTY, getSumStatsData(rawDatas, fromKey, toKey)));
		}
		return orderResults;
	}

	public OrderStatsData getDelayOrder(int size) {
		return getDelayOrder(size, getDefaultStart(), getDefaultEnd());
	}

	public OrderStatsData getQpxOrder(int size, long start, long end) {
		return getQpxOrderInMemory(size, StatisType.SAVE, start, end);
	}

	public List<OrderStatsData> getOrder(int size) {
		return getOrder(size, getDefaultStart(), getDefaultEnd());
	}

	public List<OrderStatsData> getOrder(int size, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getOrderInMemory(size, start, end);
		}

		return getOrderInDb(size, start, end);
	}

	public List<OrderStatsData> getOrderInMemory(int size, long start, long end) {
		OrderStatsData delayOrderResult = getDelayOrderInMemory(size, StatisType.SAVE, start, end);
		OrderStatsData qpxOrderResult = getQpxOrderInMemory(size, StatisType.SAVE, start, end);
		List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
		orderStatsDatas.add(delayOrderResult);
		orderStatsDatas.add(qpxOrderResult);
		return orderStatsDatas;
	}

	public List<OrderStatsData> getOrderInDb(int size, long start, long end) {
		long fromKey = getKey(start);
		long toKey = getKey(end);
		OrderStatsData delayOrderResult = new OrderStatsData(size, createDelayDesc(TOTAL_KEY, StatisType.SAVE), start,
				end);
		OrderStatsData qpxOrderResult = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, StatisType.SAVE), start, end);
		List<TopicResource> topicResources = resourceContainer.findTopicResources();
		if (topicResources != null && topicResources.size() > 0) {
			QueryQrderTask queryQrderTask = new QueryQrderTask();
			for (TopicResource topicResource : topicResources) {
				String topicName = topicResource.getTopic();
				if (TOTAL_KEY.equals(topicName)) {
					continue;
				}
				queryQrderTask.submit(new QueryOrderParam(topicName, fromKey, toKey, delayOrderResult, qpxOrderResult));
			}
			queryQrderTask.await();
		}
		List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
		orderStatsDatas.add(delayOrderResult);
		orderStatsDatas.add(qpxOrderResult);
		return orderStatsDatas;
	}

	protected OrderStatsData getQpxOrderInMemory(int size, StatisType type, long start, long end) {
		Set<String> topics = statis.getTopics(false);
		if (topics == null) {
			return null;
		}
		long fromKey = getKey(start);
		long toKey = getKey(end);
		OrderStatsData orderResults = new OrderStatsData(size, createQpxDesc(TOTAL_KEY, type), start, end);
		Iterator<String> iterator = topics.iterator();
		while (iterator.hasNext()) {
			String topicName = iterator.next();
			if (TOTAL_KEY.equals(topicName)) {
				continue;
			}
			NavigableMap<Long, Long> rawDatas = statis.getQpxForTopic(topicName, type);
			orderResults.add(new OrderEntity(topicName, StringUtils.EMPTY, getSumStatsData(rawDatas, fromKey, toKey)));
		}
		return orderResults;
	}

	public OrderStatsData getQpxOrder(int size) {
		return getQpxOrder(size, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public StatsData getSaveDelay(String topic, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getDelayInMemory(topic, StatisType.SAVE, start, end);
		}

		return getDelayInDb(topic, StatisType.SAVE, start, end);
	}

	@Override
	protected StatsData getDelayInDb(String topic, StatisType type, long start, long end) {

		long startKey = getKey(start);
		long endKey = getKey(end);

		NavigableMap<Long, Long> rawData = pTopicStatsDataService.findSectionDelayData(topic, startKey, endKey);
		rawData = fillStatsData(rawData, startKey, endKey);
		return createStatsData(createQpxDesc(topic, StatisType.SAVE), rawData, start, end);
	}

	@Override
	public StatsData getQpx(String topic, QPX qpx, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getQpxInMemory(topic, StatisType.SAVE, start, end);
		}
		return getQpxInDb(topic, StatisType.SAVE, start, end);
	}

	@Override
	protected StatsData getQpxInDb(String topic, StatisType type, long start, long end) {
		long startKey = getKey(start);
		long endKey = getKey(end);

		NavigableMap<Long, Long> rawData = pTopicStatsDataService.findSectionQpsData(topic, startKey, endKey);
		rawData = fillStatsData(rawData, startKey, endKey);
		return createStatsData(createQpxDesc(topic, type), rawData, start, end);
	}

	@Override
	public Map<String, StatsData> getServerQpx(QPX qpx, long start, long end) {

		if (dataExistInMemory(start, end)) {
			return getServerQpxInMemory(qpx, StatisType.SAVE, start, end);
		}

		return getServerQpxInDb(qpx, StatisType.SAVE, start, end);
	}

	protected Map<String, StatsData> getServerQpxInDb(QPX qpx, StatisType type, long start, long end) {
		Map<String, StatsData> result = new HashMap<String, StatsData>();

		long startKey = getKey(start);
		long endKey = getKey(end);
		Map<String, NavigableMap<Long, Long>> statsDataMaps = pServerStatsDataService.findSectionQpsData(startKey,
				endKey);

		for (Map.Entry<String, NavigableMap<Long, Long>> statsDataMap : statsDataMaps.entrySet()) {
			String serverIp = statsDataMap.getKey();

			if (StringUtils.equals(TOTAL_KEY, serverIp)) {
				continue;
			}

			NavigableMap<Long, Long> statsData = statsDataMap.getValue();
			statsData = fillStatsData(statsData, startKey, endKey);
			result.put(serverIp, createStatsData(createServerQpxDesc(serverIp, type), statsData, start, end));

		}
		return result;
	}

	@Override
	public StatsData getQpx(String topic, QPX qpx) {

		return getQpx(topic, qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public StatsData getSaveDelay(final String topic) throws Exception {

		SwallowCallableWrapper<StatsData> wrapper = new CatCallableWrapper<StatsData>(CAT_TYPE, "getSaveDelay");

		return wrapper.doCallable(new Callable<StatsData>() {

			@Override
			public StatsData call() throws Exception {

				return getSaveDelay(topic, getDefaultStart(), getDefaultEnd());
			}
		});
	}

	@Override
	protected AbstractAllData<ProducerTopicData, ProducerServerData, ProducerServerStatisData, ProducerMonitorData> createServerStatis() {

		return new ProducerAllData();
	}

	@Override
	public Map<String, StatsData> getServerQpx(QPX qpx) {
		return getServerQpx(qpx, getDefaultStart(), getDefaultEnd());
	}

	@Override
	protected StatsDataDesc createDelayDesc(String topic, StatisType type) {

		return new ProducerStatsDataDesc(topic, type.getDelayDetailType());
	}

	@Override
	protected StatsDataDesc createQpxDesc(String topic, StatisType type) {

		return new ProducerStatsDataDesc(topic, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerQpxDesc(String serverIp, StatisType type) {

		return new ProducerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getQpxDetailType());
	}

	@Override
	protected StatsDataDesc createServerDelayDesc(String serverIp, StatisType type) {

		return new ProducerServerDataDesc(serverIp, MonitorData.TOTAL_KEY, type.getDelayDetailType());
	}

	private ProducerTopicStatsData getPrePTopicStatsData(String topicName, long startKey, long endKey) {
		ProducerTopicStatsData pTopicStatsData = pTopicStatsDataService.findOneByTopicAndTime(topicName, startKey,
				endKey, true);
		if (pTopicStatsData != null) {
			return pTopicStatsData;
		}
		return new ProducerTopicStatsData();
	}

	private ProducerTopicStatsData getPostPTopicStatsData(String topicName, long startKey, long endKey) {
		ProducerTopicStatsData pTopicStatsData = pTopicStatsDataService.findOneByTopicAndTime(topicName, startKey,
				endKey, false);
		if (pTopicStatsData != null) {
			return pTopicStatsData;
		}
		return new ProducerTopicStatsData();
	}

	private class QueryQrderTask {

		private static final int poolSize = CommonUtils.DEFAULT_CPU_COUNT * 6;

		private static final int MAX_WAIT_TIME = 60;

		private ExecutorService executorService = Executors.newFixedThreadPool(poolSize,
				ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

		public QueryQrderTask() {
			logger.info("[QueryQrderTask] poolSize {} .", poolSize);
		}

		public void submit(final QueryOrderParam orderParam) {
			// logger.info("[submit] QueryOrderParam {} .", orderParam);
			executorService.submit(new Runnable() {

				@Override
				public void run() {
					ProducerTopicStatsData preStatsData = getPrePTopicStatsData(orderParam.getTopicName(),
							orderParam.getFromKey(), orderParam.getToKey());
					ProducerTopicStatsData postStatsData = getPostPTopicStatsData(orderParam.getTopicName(),
							orderParam.getFromKey(), orderParam.getToKey());
					orderParam.getDelayStatsData().add(
							new OrderEntity(orderParam.getTopicName(), StringUtils.EMPTY, postStatsData.getTotalDelay()
									- preStatsData.getTotalDelay()));
					orderParam.getQpxStatsData().add(
							new OrderEntity(orderParam.getTopicName(), StringUtils.EMPTY, postStatsData.getTotalQps()
									- preStatsData.getTotalQps()));
				}
			});
		}

		public void await() {
			executorService.shutdown();
			try {
				executorService.awaitTermination(MAX_WAIT_TIME, TimeUnit.SECONDS);
				logger.info("[await] QueryQrderTask is over .");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	private static class QueryOrderParam {

		private String topicName;

		private long fromKey;

		private long toKey;

		private OrderStatsData delayStatsData;

		private OrderStatsData qpxStatsData;

		public QueryOrderParam(String topicName, long fromKey, long toKey, OrderStatsData delayStatsData,
				OrderStatsData qpxStatsData) {
			this.topicName = topicName;
			this.fromKey = fromKey;
			this.toKey = toKey;
			this.delayStatsData = delayStatsData;
			this.qpxStatsData = qpxStatsData;
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

		public OrderStatsData getDelayStatsData() {
			return delayStatsData;
		}

		public OrderStatsData getQpxStatsData() {
			return qpxStatsData;
		}

		@Override
		public String toString() {
			return "QueryOrderParam [topicName=" + topicName + ", fromKey=" + fromKey + ", toKey=" + toKey
					+ ", delayStatsData=" + delayStatsData + ", qpxStatsData=" + qpxStatsData + "]";
		}

	}
}