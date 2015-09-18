package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ProducerIpStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年9月17日 下午8:25:05
 */
@Component
public class ProducerIpStatsAlarmer extends AbstractStatsAlarmer {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	protected EventReporter eventReporter;

	@Autowired
	protected EventFactory eventFactory;

	@Autowired
	private ProducerStatsDataWapper pStatsDataWapper;

	@Autowired
	private ProducerIpStatsDataService pIpStatsDataService;

	private Map<ProducerIpStatsData, Long> firstCandidates = new ConcurrentHashMap<ProducerIpStatsData, Long>();

	private Map<ProducerIpStatsData, Long> secondCandidates = new ConcurrentHashMap<ProducerIpStatsData, Long>();
	
	private Map<ProducerIpStatsData, Long> whiteLists = new ConcurrentHashMap<ProducerIpStatsData, Long>();

	private static final long CHECK_TIMESPAN = 2 * 60 * 1000;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void doAlarm() {
		final List<ProducerIpGroupStatsData> ipGroupStatsDatas = pStatsDataWapper.getIpGroupStatsDatas(
				getLastTimeKey(), false);
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				alarmIpData(ipGroupStatsDatas);
			}
		});
	}

	public void alarmIpData(final List<ProducerIpGroupStatsData> ipGroupStatsDatas) {
		checkIpGroups(ipGroupStatsDatas);
		alarmSureRecords();
		alarmUnSureRecords();
	}

	public void checkIpGroups(final List<ProducerIpGroupStatsData> ipGroupStatsDatas) {
		if (ipGroupStatsDatas == null || ipGroupStatsDatas.size() == 0) {
			return;
		}
		for (final ProducerIpGroupStatsData ipGroupStatsData : ipGroupStatsDatas) {
			checkIpGroup(ipGroupStatsData);
		}
	}

	public void checkIpGroup(ProducerIpGroupStatsData ipGroupStatsData) {
		boolean hasGroupStatsData = ipGroupStatsData.hasStatsData();
		List<ProducerIpStatsData> ipStatsDatas = ipGroupStatsData.getProducerIpStatsDatas();
		if (ipStatsDatas == null || ipStatsDatas.size() == 0) {
			return;
		}
		for (ProducerIpStatsData ipStatsData : ipStatsDatas) {
			boolean hasStatsData = ipStatsData.checkStatsData();
			if (hasStatsData) {
				whiteLists.put(ipStatsData, System.currentTimeMillis());
			} else if (!hasStatsData && hasGroupStatsData) {
				if (!firstCandidates.containsKey(ipStatsData)) {
					firstCandidates.put(ipStatsData, System.currentTimeMillis());
				}
			} else if (!hasStatsData && !hasGroupStatsData) {
				if (ipStatsDatas.size() == 1 && !secondCandidates.containsKey(ipStatsData)) {
					secondCandidates.put(ipStatsData, System.currentTimeMillis());
				}
			}
		}
	}

	public void alarmSureRecords() {
		Iterator<Entry<ProducerIpStatsData, Long>> iterator = firstCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ProducerIpStatsData, Long> checkRecord = iterator.next();
			ProducerIpStatsData ipStatsData = checkRecord.getKey();
			long lastTime = checkRecord.getValue();
			if (System.currentTimeMillis() - lastTime >= CHECK_TIMESPAN) {
				iterator.remove();
				if (whiteLists.containsKey(ipStatsData) && whiteLists.get(ipStatsData) < lastTime) {
					continue;
				}
				ProducerClientEvent clientEvent = eventFactory.createPClientEvent();
				clientEvent.setTopicName(ipStatsData.getTopicName()).setIp(ipStatsData.getIp())
						.setClientType(ClientType.CLIENT_SENDER).setEventType(EventType.PRODUCER)
						.setCreateTime(new Date());
				eventReporter.report(clientEvent);
			}
		}

	}

	public void alarmUnSureRecords() {

		Iterator<Entry<ProducerIpStatsData, Long>> iterator = secondCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ProducerIpStatsData, Long> checkRecord = iterator.next();
			ProducerIpStatsData ipStatsData = checkRecord.getKey();
			long lastTime = checkRecord.getValue();

			if (System.currentTimeMillis() - lastTime >= CHECK_TIMESPAN) {
				iterator.remove();
				if (whiteLists.containsKey(ipStatsData) && whiteLists.get(ipStatsData) < lastTime) {
					continue;
				}
				long avgQps = pIpStatsDataService.findAvgQps(ipStatsData.getTopicName(), ipStatsData.getIp(),
						getTimeKey(getPreNDayKey(1, CHECK_TIMESPAN)), getTimeKey(System.currentTimeMillis()));

				if (avgQps > 0) {

					ProducerClientEvent clientEvent = eventFactory.createPClientEvent();
					clientEvent.setTopicName(ipStatsData.getTopicName()).setIp(ipStatsData.getIp())
							.setClientType(ClientType.CLIENT_SENDER).setEventType(EventType.PRODUCER)
							.setCreateTime(new Date());
					eventReporter.report(clientEvent);
				}
			}
		}
	}

}
