package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.event.ClientType;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ProducerClientEvent;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ProducerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData.ProducerIpStatsDataKey;
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
public class ProducerIpStatsAlarmer extends
		AbstractIpStatsAlarmer<ProducerIpStatsDataKey, ProducerIpStatsData, ProducerIpGroupStatsData> {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerStatsDataWapper pStatsDataWapper;

	@Autowired
	private ProducerIpStatsDataService pIpStatsDataService;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void doAlarm() {
		alarmIpData();
	}

	public void alarmIpData() {
		Set<String> topicNames = pStatsDataWapper.getTopics(false);
		for (String topicName : topicNames) {
			ProducerIpGroupStatsData ipGroupStatsData = pStatsDataWapper.getIpGroupStatsData(topicName,
					getLastTimeKey(), false);
			checkIpGroup(ipGroupStatsData);
		}
		alarmSureRecords();
		alarmUnSureRecords();
	}

	@Override
	protected void checkUnSureLastRecords(ProducerIpStatsDataKey statsDataKey) {
		long avgQps = pIpStatsDataService.findAvgQps(statsDataKey.getTopicName(), statsDataKey.getIp(),
				getTimeKey(getPreNDayKey(1, CHECK_TIMESPAN)), getTimeKey(getPreNDayKey(1, 0)));

		if (avgQps > 0) {
			report(statsDataKey);
		}
	}

	@Override
	protected boolean isReport(ProducerIpStatsDataKey statsDataKey) {
		TopicResource topicResource = resourceContainer.findTopicResource(statsDataKey.getTopicName());
		if (topicResource.isProducerAlarm()) {
			List<IpInfo> ipInfos = topicResource.getProducerIpInfos();
			if (ipInfos == null || ipInfos.isEmpty()) {
				return true;
			}
			if (StringUtils.isNotBlank(statsDataKey.getIp())) {
				for (IpInfo ipInfo : ipInfos) {
					if (statsDataKey.getIp().equals(ipInfo.getIp())) {
						return ipInfo.isActiveAndAlarm();
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	protected void report(ProducerIpStatsDataKey statsDataKey) {
		if (isReport(statsDataKey)) {
			ProducerClientEvent clientEvent = eventFactory.createPClientEvent();
			clientEvent.setTopicName(statsDataKey.getTopicName()).setIp(statsDataKey.getIp())
					.setClientType(ClientType.CLIENT_SENDER).setEventType(EventType.PRODUCER).setCreateTime(new Date())
					.setCheckInterval(CHECK_TIMESPAN);
			eventReporter.report(clientEvent);
		}
	}

}
