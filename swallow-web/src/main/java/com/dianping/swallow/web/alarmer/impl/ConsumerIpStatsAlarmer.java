package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.model.event.ClientType;
import com.dianping.swallow.web.model.event.ConsumerClientEvent;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
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

	@Autowired
	private AlarmResourceContainer resourceContainer;

	private Map<ConsumerIpStatsData, Long> firstCandidates = new ConcurrentHashMap<ConsumerIpStatsData, Long>();

	private Map<ConsumerIpStatsData, Long> secondCandidates = new ConcurrentHashMap<ConsumerIpStatsData, Long>();

	private Map<ConsumerIpStatsData, Long> whiteLists = new ConcurrentHashMap<ConsumerIpStatsData, Long>();

	private static final long CHECK_TIMESPAN = 10 * 60 * 1000;

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
			if (System.currentTimeMillis() - lastRecordTime < CHECK_TIMESPAN) {
				continue;
			}
			iterator.remove();
			if (whiteLists.containsKey(ipStatsData) && whiteLists.get(ipStatsData) > lastRecordTime) {
				continue;
			}

			report(ipStatsData.getTopicName(), ipStatsData.getConsumerId(), ipStatsData.getIp());
		}
	}

	public void alarmUnSureRecords() {

		Iterator<Entry<ConsumerIpStatsData, Long>> iterator = secondCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ConsumerIpStatsData, Long> secondCandidate = iterator.next();
			ConsumerIpStatsData ipStatsData = secondCandidate.getKey();
			long lastRecordTime = secondCandidate.getValue();

			if (System.currentTimeMillis() - lastRecordTime < CHECK_TIMESPAN) {
				continue;
			}
			iterator.remove();
			if (whiteLists.containsKey(ipStatsData) && whiteLists.get(ipStatsData) > lastRecordTime) {
				continue;
			}

			ConsumerIpQpsPair avgQpsPair = cIpStatsDataService.findAvgQps(ipStatsData.getTopicName(),
					ipStatsData.getConsumerId(), ipStatsData.getIp(), getTimeKey(getPreNDayKey(1, CHECK_TIMESPAN)),
					getTimeKey(getPreNDayKey(1, 0)));

			if (avgQpsPair.getSendQps() > 0 || avgQpsPair.getAckQps() > 0) {
				report(ipStatsData.getTopicName(), ipStatsData.getConsumerId(), ipStatsData.getIp());
			}
		}

	}

	private boolean isReport(String topicName, String consumerId, String ip) {
		TopicResource topicResource = resourceContainer.findTopicResource(topicName);
		ConsumerIdResource ConsumerIdResource = resourceContainer.findConsumerIdResource(topicName, consumerId);
		if (!topicResource.isConsumerAlarm()) {
			return false;
		}
		if (ConsumerIdResource.isAlarm()) {
			List<IpInfo> ipInfos = ConsumerIdResource.getConsumerIpInfos();
			if (StringUtils.isNotBlank(ip)) {
				for (IpInfo ipInfo : ipInfos) {
					if (ip.equals(ipInfo.getIp())) {
						return ipInfo.isActiveAndAlarm();
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	private void report(String topicName, String consumerId, String ip) {
		if (isReport(topicName, consumerId, ip)) {
			ConsumerClientEvent clientEvent = eventFactory.createCClientEvent();
			clientEvent.setConsumerId(consumerId).setTopicName(topicName).setIp(ip)
					.setClientType(ClientType.CLIENT_RECEIVER).setEventType(EventType.CONSUMER)
					.setCreateTime(new Date());
			eventReporter.report(clientEvent);
		}
	}

}
