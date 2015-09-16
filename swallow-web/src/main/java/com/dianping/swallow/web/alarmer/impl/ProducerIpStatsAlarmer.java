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
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ProducerClientEvent;
import com.dianping.swallow.web.model.stats.ProducerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ProducerIpStatsDataService;

public class ProducerIpStatsAlarmer extends AbstractStatsAlarmer {

	@Autowired
	protected EventReporter eventReporter;

	@Autowired
	protected EventFactory eventFactory;

	@Autowired
	private ProducerStatsDataWapper pStatsDataWapper;

	@Autowired
	private ProducerIpStatsDataService pIpStatsDataService;

	private Map<ProducerIpStatsData, Long> unSureRecords = new ConcurrentHashMap<ProducerIpStatsData, Long>();

	private Map<ProducerIpStatsData, Long> sureRecords = new ConcurrentHashMap<ProducerIpStatsData, Long>();

	private static final long CHECK_TIMESPAN = 10 * 60 * 1000;

	@Override
	public void doAlarm() {
		final List<ProducerIpGroupStatsData> pIpGroupStatsDatas = pStatsDataWapper.getIpGroupStatsDatas(
				getLastTimeKey(), false);
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName());
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				pIpGroupAlarms(pIpGroupStatsDatas);
			}
		});
	}

	public void pIpGroupAlarms(final List<ProducerIpGroupStatsData> pIpGroupStatsDatas) {
		if (pIpGroupStatsDatas == null || pIpGroupStatsDatas.size() == 0) {
			return;
		}

		for (final ProducerIpGroupStatsData ipGroupStatsData : pIpGroupStatsDatas) {
			checkIpGroup(ipGroupStatsData);
		}

		alarmSureRecords();
		alarmUnSureRecords();
	}

	public void checkIpGroup(ProducerIpGroupStatsData ipGroupStatsData) {
		boolean hasStatsData = ipGroupStatsData.hasStatsData();
		List<ProducerIpStatsData> ipStatsDatas = ipGroupStatsData.getProducerIpStatsDatas();
		if (ipStatsDatas == null || ipStatsDatas.size() == 0) {
			return;
		}
		for (ProducerIpStatsData ipStatsData : ipStatsDatas) {
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
		Iterator<Entry<ProducerIpStatsData, Long>> iterator = sureRecords.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ProducerIpStatsData, Long> checkRecord = iterator.next();
			ProducerIpStatsData ipStatsData = checkRecord.getKey();
			long lastTime = checkRecord.getValue();
			if (System.currentTimeMillis() - lastTime >= CHECK_TIMESPAN) {

				ProducerClientEvent clientEvent = eventFactory.createPClientEvent();
				clientEvent.setTopicName(ipStatsData.getTopicName()).setIp(ipStatsData.getIp())
						.setClientType(ClientType.CLIENT_SENDER).setEventType(EventType.PRODUCER)
						.setCreateTime(new Date());
				eventReporter.report(clientEvent);
				iterator.remove();

			}
		}

	}

	public void alarmUnSureRecords() {

		Iterator<Entry<ProducerIpStatsData, Long>> iterator = unSureRecords.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ProducerIpStatsData, Long> checkRecord = iterator.next();
			ProducerIpStatsData ipStatsData = checkRecord.getKey();
			long lastTime = checkRecord.getValue();

			if (System.currentTimeMillis() - lastTime >= CHECK_TIMESPAN) {

				long avgQps = pIpStatsDataService.findAvgQps(ipStatsData.getTopicName(), ipStatsData.getIp(),
						getTimeKey(getPreNDayKey(1, CHECK_TIMESPAN)), getTimeKey(System.currentTimeMillis()));

				if (avgQps > 0) {

					ProducerClientEvent clientEvent = eventFactory.createPClientEvent();
					clientEvent.setTopicName(ipStatsData.getTopicName()).setIp(ipStatsData.getIp())
							.setClientType(ClientType.CLIENT_SENDER).setEventType(EventType.PRODUCER)
							.setCreateTime(new Date());
					eventReporter.report(clientEvent);
				}
				iterator.remove();
			}
		}
	}

}
