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
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ProducerClientEvent;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
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

	@Autowired
	private AlarmResourceContainer resourceContainer;

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
		if (ipGroupStatsDatas == null || ipGroupStatsDatas.isEmpty()) {
			return;
		}
		for (final ProducerIpGroupStatsData ipGroupStatsData : ipGroupStatsDatas) {
			checkIpGroup(ipGroupStatsData);
		}
	}

	public void checkIpGroup(ProducerIpGroupStatsData ipGroupStatsData) {
		boolean hasGroupStatsData = ipGroupStatsData.hasStatsData();
		List<ProducerIpStatsData> ipStatsDatas = ipGroupStatsData.getProducerIpStatsDatas();
		if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
			return;
		}
		for (ProducerIpStatsData ipStatsData : ipStatsDatas) {
			boolean hasStatsData = ipStatsData.checkStatsData();
			if (hasStatsData) {
				whiteLists.put(ipStatsData, System.currentTimeMillis());
			} else {
				if (hasGroupStatsData) {
					if (!firstCandidates.containsKey(ipStatsData)) {
						firstCandidates.put(ipStatsData, System.currentTimeMillis());
					}
				} else {
					if (ipStatsDatas.size() == 1 && !secondCandidates.containsKey(ipStatsData)) {
						secondCandidates.put(ipStatsData, System.currentTimeMillis());
					}
				}
			}
		}
	}

	public void alarmSureRecords() {
		Iterator<Entry<ProducerIpStatsData, Long>> iterator = firstCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ProducerIpStatsData, Long> checkRecord = iterator.next();
			ProducerIpStatsData ipStatsData = checkRecord.getKey();
			long lastRecordTime = checkRecord.getValue();
			if (System.currentTimeMillis() - lastRecordTime < CHECK_TIMESPAN) {
				continue;
			}
			iterator.remove();
			if (whiteLists.containsKey(ipStatsData) && whiteLists.get(ipStatsData) > lastRecordTime) {
				continue;
			}
			report(ipStatsData.getTopicName(), ipStatsData.getIp());
		}
	}

	public void alarmUnSureRecords() {

		Iterator<Entry<ProducerIpStatsData, Long>> iterator = secondCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<ProducerIpStatsData, Long> checkRecord = iterator.next();
			ProducerIpStatsData ipStatsData = checkRecord.getKey();
			long lastRecordTime = checkRecord.getValue();

			if (System.currentTimeMillis() - lastRecordTime < CHECK_TIMESPAN) {
				continue;
			}
			iterator.remove();
			if (whiteLists.containsKey(ipStatsData) && whiteLists.get(ipStatsData) > lastRecordTime) {
				continue;
			}
			long avgQps = pIpStatsDataService.findAvgQps(ipStatsData.getTopicName(), ipStatsData.getIp(),
					getTimeKey(getPreNDayKey(1, CHECK_TIMESPAN)), getTimeKey(getPreNDayKey(1, 0)));

			if (avgQps > 0) {
				report(ipStatsData.getTopicName(), ipStatsData.getIp());
			}
		}

	}

	private boolean isReport(String topicName, String ip) {
		TopicResource topicResource = resourceContainer.findTopicResource(topicName);
		if (topicResource.isProducerAlarm()) {
			List<IpInfo> ipInfos = topicResource.getProducerIpInfos();
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

	private void report(String topicName, String ip) {
		if (isReport(topicName, ip)) {

			ProducerClientEvent clientEvent = eventFactory.createPClientEvent();
			clientEvent.setTopicName(topicName).setIp(ip).setClientType(ClientType.CLIENT_SENDER)
					.setEventType(EventType.PRODUCER).setCreateTime(new Date());
			eventReporter.report(clientEvent);
		}
	}

}
