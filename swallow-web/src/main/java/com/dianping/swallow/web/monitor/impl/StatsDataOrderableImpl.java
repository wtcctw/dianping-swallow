package com.dianping.swallow.web.monitor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.OrderStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsDataOrderable;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever.ConsumerOrderDataPair;

@Component
public class StatsDataOrderableImpl implements StatsDataOrderable {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Override
	public List<OrderStatsData> getOrderStatsData(int size) {
		return getOrderStatsData(size, getDefaultStart(), getDefaultEnd());
	}

	@Override
	public List<OrderStatsData> getOrderStatsData(int size, long start, long end) {
		if (consumerDataRetriever.dataExistInMemory(start, end)) {
			return getOrderStatsDataInMemory(size, start, end);
		} else {
			return getOrderStatsDataInDb(size, start, end);
		}
	}

	private List<OrderStatsData> getOrderStatsDataInDb(int size, long start, long end) {
		List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
		return orderStatsDatas;
	}

	private List<OrderStatsData> getOrderStatsDataInMemory(int size, long start, long end) {
		List<OrderStatsData> orderStatsDatas = new ArrayList<OrderStatsData>();
		OrderStatsData delayOrderData = producerDataRetriever.getDelayOrder(size, start, end);
		OrderStatsData qpxOrderData = producerDataRetriever.getQpxOrder(size, start, end);
		ConsumerOrderDataPair delayOrderDataPair = consumerDataRetriever
				.getDelayOrderForAllConsumerId(size, start, end);
		ConsumerOrderDataPair qpxOrderDataPair = consumerDataRetriever.getDelayOrderForAllConsumerId(size, start, end);
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

	

}
