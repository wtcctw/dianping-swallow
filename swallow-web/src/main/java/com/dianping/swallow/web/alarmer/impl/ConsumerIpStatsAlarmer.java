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
import com.dianping.swallow.web.model.event.ConsumerClientEvent;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.stats.ConsumerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService.ConsumerIpQpsPair;

/**
 * 
 * @author qiyin
 *
 *         2015年9月17日 下午8:24:14
 */
@Component
public class ConsumerIpStatsAlarmer extends AbstractStatsAlarmer {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	protected EventReporter eventReporter;

	@Autowired
	protected EventFactory eventFactory;

	@Autowired
	private ConsumerStatsDataWapper cStatsDataWapper;

	@Autowired
	private ConsumerIpStatsDataService cIpStatsDataService;

	private Map<ConsumerIpStatsData, Long> firstCandidates = new ConcurrentHashMap<ConsumerIpStatsData, Long>();

	private Map<ConsumerIpStatsData, Long> secondCandidates = new ConcurrentHashMap<ConsumerIpStatsData, Long>();

	private Map<ConsumerIpStatsData, Long> whiteLists = new ConcurrentHashMap<ConsumerIpStatsData, Long>();

	private static final long CHECK_TIMESPAN = 2 * 60 * 1000;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
	}

	@Override
	public void doAlarm() {
		final List<ConsumerIpGroupStatsData> ipGroupStatsDatas = cStatsDataWapper.getIpGroupStatsDatas(
				getLastTimeKey(), false);
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				alarmIpData(ipGroupStatsDatas);
			}
		});
	}

	public void alarmIpData(List<ConsumerIpGroupStatsData> ipGroupStatsDatas) {
		checkIpGroups(ipGroupStatsDatas);
		alarmSureRecords();
		alarmUnSureRecords();
	}

	public void checkIpGroups(List<ConsumerIpGroupStatsData> ipGroupStatsDatas) {
		if (ipGroupStatsDatas == null || ipGroupStatsDatas.size() == 0) {
			return;
		}

		for (final ConsumerIpGroupStatsData ipGroupStatsData : ipGroupStatsDatas) {
			checkIpGroup(ipGroupStatsData);
		}
	}

	public void checkIpGroup(ConsumerIpGroupStatsData ipGroupStatsData) {
		boolean hasGroupStatsData = ipGroupStatsData.hasStatsData();
		List<ConsumerIpStatsData> ipStatsDatas = ipGroupStatsData.getConsumerIpStatsDatas();
		if (ipStatsDatas == null || ipStatsDatas.size() == 0) {
			return;
		}
		for (ConsumerIpStatsData ipStatsData : ipStatsDatas) {
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
		Iterator<Entry<ConsumerIpStatsData, Long>> iterator = firstCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ConsumerIpStatsData, Long> firstCandidate = iterator.next();
			ConsumerIpStatsData ipStatsData = firstCandidate.getKey();
			long lastRecordTime = firstCandidate.getValue();
			if (System.currentTimeMillis() - lastRecordTime >= CHECK_TIMESPAN) {
				iterator.remove();
				if (whiteLists.containsKey(ipStatsData) && whiteLists.get(ipStatsData) > lastRecordTime) {
					continue;
				}
				ConsumerClientEvent clientEvent = eventFactory.createCClientEvent();
				clientEvent.setConsumerId(ipStatsData.getConsumerId()).setTopicName(ipStatsData.getTopicName())
						.setIp(ipStatsData.getIp()).setClientType(ClientType.CLIENT_RECEIVER)
						.setEventType(EventType.CONSUMER).setCreateTime(new Date());
				eventReporter.report(clientEvent);

			}
		}

	}

	public void alarmUnSureRecords() {

		Iterator<Entry<ConsumerIpStatsData, Long>> iterator = secondCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ConsumerIpStatsData, Long> secondCandidate = iterator.next();
			ConsumerIpStatsData ipStatsData = secondCandidate.getKey();
			long lastRecordTime = secondCandidate.getValue();

			if (System.currentTimeMillis() - lastRecordTime >= CHECK_TIMESPAN) {
				iterator.remove();
				if (whiteLists.containsKey(ipStatsData) && whiteLists.get(ipStatsData) > lastRecordTime) {
					continue;
				}
				
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
			}
		}
	}

}
