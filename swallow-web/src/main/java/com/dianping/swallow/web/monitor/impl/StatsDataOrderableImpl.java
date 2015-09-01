package com.dianping.swallow.web.monitor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.StatisDetailType;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.OrderStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsDataOrderable;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever.ConsumerOrderDataPair;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月31日 上午10:01:10
 */
@Component
public class StatsDataOrderableImpl implements StatsDataOrderable {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Autowired
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Autowired
	private ProducerTopicStatsDataService pTopicStatsDataService;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Override
	public List<OrderStatsData> getOrderStatsData(int size) {
		return getOrderStatsData(size, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public List<OrderStatsData> getOrderStatsData(int size, long start, long end) {
		if (consumerDataRetriever.dataExistInMemory(start, end)) {
			return getOrderStatsDataInMemory(size, start, end);
		} else {
			return getOrderStatsDataInMemory(size, start, end);
		}
	}

	private List<OrderStatsData> getOrderStatsDataInDb(int size, long start, long end) {
		List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
		long startKey = AbstractRetriever.getKey(start);
		long endKey = AbstractRetriever.getKey(end);
		List<ConsumerIdResource> consumerIdResources =resourceContainer.findConsumerIdResources();
		
		return orderStatsDatas;
	}

	private List<OrderStatsData> getOrderStatsDataInMemory(int size, long start, long end) {
		List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
		OrderStatsData delayOrderData = producerDataRetriever.getDelayOrder(size, start, end);
		OrderStatsData qpxOrderData = producerDataRetriever.getQpxOrder(size, start, end);
		ConsumerOrderDataPair delayOrderDataPair = consumerDataRetriever
				.getDelayOrderForAllConsumerId(size, start, end);
		ConsumerOrderDataPair qpxOrderDataPair = consumerDataRetriever.getQpxOrderForAllConsumerId(size, start, end);
		OrderStatsData accuOrderData = accumulationRetriever.getAccuOrderForAllConsumerId(size, start, end);
		orderStatsDatas.add(delayOrderData);
		orderStatsDatas.add(delayOrderDataPair.getSendStatsData());
		orderStatsDatas.add(delayOrderDataPair.getAckStatsData());
		orderStatsDatas.add(qpxOrderData);
		orderStatsDatas.add(qpxOrderDataPair.getSendStatsData());
		orderStatsDatas.add(qpxOrderDataPair.getAckStatsData());
		orderStatsDatas.add(accuOrderData);
		return orderStatsDatas;
	}

	private long getDefaultEnd() {

		return System.currentTimeMillis();
	}

	private long getDefaultStart() {
		return System.currentTimeMillis()
				- TimeUnit.MILLISECONDS.convert(producerDataRetriever.getKeepInMemoryHour(), TimeUnit.HOURS);
	}

	private ConsumerIdStatsData getPreConsumerIdStatsData(String topicName, String consumerId, long timeKey) {
		ConsumerIdStatsData consumerIdStatsData = consumerIdStatsDataService.findOneByTopicAndTimeAndConsumerId(
				topicName, timeKey, consumerId, false);
		if (consumerIdStatsData != null) {
			return consumerIdStatsData;
		}
		return new ConsumerIdStatsData();
	}

	private ConsumerIdStatsData getPostConsumerIdStatsData(String topicName, String consumerId, long timeKey) {
		ConsumerIdStatsData consumerIdStatsData = consumerIdStatsDataService.findOneByTopicAndTimeAndConsumerId(
				topicName, timeKey, consumerId, true);
		if (consumerIdStatsData != null) {
			return consumerIdStatsData;
		}
		consumerIdStatsData = consumerIdStatsDataService.findOneByTopicAndTimeAndConsumerId(topicName, timeKey,
				consumerId, false);
		if (consumerIdStatsData != null) {
			return consumerIdStatsData;
		}
		return new ConsumerIdStatsData();
	}

	private ProducerTopicStatsData getPrePTopicStatsData(String topicName, long timeKey) {
		ProducerTopicStatsData pTopicStatsData = pTopicStatsDataService
				.findOneByTopicAndTime(topicName, timeKey, false);
		if (pTopicStatsData != null) {
			return pTopicStatsData;
		}
		return new ProducerTopicStatsData();
	}

	private ProducerTopicStatsData getPostPTopicStatsData(String topicName, long timeKey) {
		ProducerTopicStatsData pTopicStatsData = pTopicStatsDataService.findOneByTopicAndTime(topicName, timeKey, true);
		if (pTopicStatsData != null) {
			return pTopicStatsData;
		}
		pTopicStatsData = pTopicStatsDataService.findOneByTopicAndTime(topicName, timeKey, false);
		if (pTopicStatsData != null) {
			return pTopicStatsData;
		}
		return new ProducerTopicStatsData();
	}

}
