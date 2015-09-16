package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.model.event.ClientType;
import com.dianping.swallow.web.model.event.ConsumerClientEvent;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.stats.ConsumerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService.ConsumerIpQpsPair;

public class ConsumerIpStatsAlarmer extends AbstractStatsAlarmer {

	@Autowired
	protected EventReporter eventReporter;

	@Autowired
	protected EventFactory eventFactory;

	@Autowired
	private ConsumerStatsDataWapper cStatsDataWapper;

	@Autowired
	private ConsumerIpStatsDataService cIpStatsDataService;

	private Map<ConsumerIpStatsData, Long> unSureRecords = new ConcurrentHashMap<ConsumerIpStatsData, Long>();

	private Map<ConsumerIpStatsData, Long> sureRecords = new ConcurrentHashMap<ConsumerIpStatsData, Long>();

	private static final long CHECK_TIMESPAN = 10 * 60 * 1000;

	@Override
	public void doAlarm() {
		final List<ConsumerIpGroupStatsData> cIpGroupStatsDatas = cStatsDataWapper.getIpGroupStatsDatas(
				getLastTimeKey(), false);
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				cIpGroupAlarms(cIpGroupStatsDatas);
			}
		});
	}

	public void cIpGroupAlarms(List<ConsumerIpGroupStatsData> cIpGroupStatsDatas) {
		if (cIpGroupStatsDatas == null || cIpGroupStatsDatas.size() == 0) {
			return;
		}

		for (final ConsumerIpGroupStatsData ipGroupStatsData : cIpGroupStatsDatas) {
			cIpGroupAlarm(ipGroupStatsData);
		}

	}

	public void cIpGroupAlarm(ConsumerIpGroupStatsData ipGroupStatsData) {
		boolean hasStatsData = ipGroupStatsData.hasStatsData();
		List<ConsumerIpStatsData> ipStatsDatas = ipGroupStatsData.getConsumerIpStatsDatas();
		if (ipStatsDatas == null || ipStatsDatas.size() == 0) {
			return;
		}
		for (ConsumerIpStatsData ipStatsData : ipStatsDatas) {
			if (!hasStatsData && ipStatsDatas.size() == 1) {
				if (!unSureRecords.containsKey(ipStatsData)) {
					unSureRecords.put(ipStatsData, System.currentTimeMillis());
				}
				return;
			}
			boolean isStatsData = ipStatsData.checkStatsData(hasStatsData);
			if (isStatsData) {
				sureRecords.put(ipStatsData, System.currentTimeMillis());
			} else {
				if (!sureRecords.containsKey(ipStatsData)) {
					sureRecords.put(ipStatsData, System.currentTimeMillis());
				}
			}
		}
	}

	public void alarmSureRecords() {
		Iterator<Entry<ConsumerIpStatsData, Long>> iterator = sureRecords.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ConsumerIpStatsData, Long> checkRecord = iterator.next();
			ConsumerIpStatsData ipStatsData = checkRecord.getKey();
			long lastTime = checkRecord.getValue();
			if (System.currentTimeMillis() - lastTime >= CHECK_TIMESPAN) {

				ConsumerClientEvent clientEvent = eventFactory.createCClientEvent();
				clientEvent.setConsumerId(ipStatsData.getConsumerId()).setTopicName(ipStatsData.getTopicName())
						.setIp(ipStatsData.getIp()).setClientType(ClientType.CLIENT_RECEIVER)
						.setEventType(EventType.CONSUMER).setCreateTime(new Date());
				eventReporter.report(clientEvent);
				iterator.remove();

			}
		}

	}

	public void alarmUnSureRecords() {

		Iterator<Entry<ConsumerIpStatsData, Long>> iterator = unSureRecords.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ConsumerIpStatsData, Long> checkRecord = iterator.next();
			ConsumerIpStatsData ipStatsData = checkRecord.getKey();
			long lastTime = checkRecord.getValue();

			if (System.currentTimeMillis() - lastTime >= CHECK_TIMESPAN) {

				ConsumerIpQpsPair avgQpsPair = cIpStatsDataService.findAvgQps(ipStatsData.getTopicName(),
						ipStatsData.getConsumerId(), ipStatsData.getIp(), getTimeKey(getPreNDayKey(1, CHECK_TIMESPAN)),
						getTimeKey(System.currentTimeMillis()));

				if (avgQpsPair.getSendQps() > 0 || avgQpsPair.getAckQps() > 0) {

					ConsumerClientEvent clientEvent = eventFactory.createCClientEvent();
					clientEvent.setConsumerId(ipStatsData.getConsumerId()).setTopicName(ipStatsData.getTopicName())
							.setIp(ipStatsData.getIp()).setClientType(ClientType.CLIENT_RECEIVER)
							.setEventType(EventType.CONSUMER).setCreateTime(new Date());
					eventReporter.report(clientEvent);
				}
				iterator.remove();
			}
		}
	}

}
