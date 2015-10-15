package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.EventReporter;
import com.dianping.swallow.web.container.ResourceContainer;
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
	private ResourceContainer resourceContainer;

	private Map<IpStatsDataKey, Long> firstCandidates = new ConcurrentHashMap<IpStatsDataKey, Long>();

	private Map<IpStatsDataKey, Long> secondCandidates = new ConcurrentHashMap<IpStatsDataKey, Long>();

	private Map<IpStatsDataKey, Long> whiteLists = new ConcurrentHashMap<IpStatsDataKey, Long>();

	private static final long CHECK_TIMESPAN = 10 * 60 * 1000;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void doAlarm() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				alarmIpData();
			}
		});
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

	public void checkIpGroup(ProducerIpGroupStatsData ipGroupStatsData) {
		if (ipGroupStatsData == null) {
			return;
		}
		List<ProducerIpStatsData> ipStatsDatas = ipGroupStatsData.getProducerIpStatsDatas();
		if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
			return;
		}
		boolean hasGroupStatsData = ipGroupStatsData.hasStatsData();
		for (ProducerIpStatsData ipStatsData : ipStatsDatas) {
			boolean hasStatsData = ipStatsData.checkStatsData();
			IpStatsDataKey key = new IpStatsDataKey(ipStatsData);
			if (hasStatsData) {
				whiteLists.put(key, System.currentTimeMillis());
			} else {
				if (hasGroupStatsData) {
					if (!firstCandidates.containsKey(key)) {
						firstCandidates.put(key, System.currentTimeMillis());
					} else {
						if (whiteLists.containsKey(key) && whiteLists.get(key) > firstCandidates.get(key)) {
							firstCandidates.put(key, System.currentTimeMillis());
						}
					}
				} else {
					if (ipStatsDatas.size() == 1) {
						if (!secondCandidates.containsKey(key)) {
							secondCandidates.put(key, System.currentTimeMillis());
						} else {
							if (whiteLists.containsKey(key) && whiteLists.get(key) > secondCandidates.get(key)) {
								secondCandidates.put(key, System.currentTimeMillis());
							}
						}
					}
				}
			}
		}
	}

	public void alarmSureRecords() {
		Iterator<Entry<IpStatsDataKey, Long>> iterator = firstCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<IpStatsDataKey, Long> checkRecord = iterator.next();
			IpStatsDataKey key = checkRecord.getKey();
			long lastRecordTime = checkRecord.getValue();
			if (System.currentTimeMillis() - lastRecordTime < CHECK_TIMESPAN) {
				continue;
			}
			iterator.remove();
			report(key.getTopicName(), key.getIp());
		}
	}

	public void alarmUnSureRecords() {
		Iterator<Entry<IpStatsDataKey, Long>> iterator = secondCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<IpStatsDataKey, Long> checkRecord = iterator.next();
			IpStatsDataKey key = checkRecord.getKey();
			long lastRecordTime = checkRecord.getValue();

			if (System.currentTimeMillis() - lastRecordTime < CHECK_TIMESPAN) {
				continue;
			}
			iterator.remove();
			long avgQps = pIpStatsDataService.findAvgQps(key.getTopicName(), key.getIp(),
					getTimeKey(getPreNDayKey(1, CHECK_TIMESPAN)), getTimeKey(getPreNDayKey(1, 0)));

			if (avgQps > 0) {
				report(key.getTopicName(), key.getIp());
			}
		}
	}

	private boolean isReport(String topicName, String ip) {
		TopicResource topicResource = resourceContainer.findTopicResource(topicName);
		if (topicResource.isProducerAlarm()) {
			List<IpInfo> ipInfos = topicResource.getProducerIpInfos();
			if (ipInfos == null || ipInfos.isEmpty()) {
				return true;
			}
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
					.setEventType(EventType.PRODUCER).setCreateTime(new Date()).setCheckInterval(CHECK_TIMESPAN);
			eventReporter.report(clientEvent);
		}
	}

	private static class IpStatsDataKey {

		public IpStatsDataKey(ProducerIpStatsData ipStatsData) {
			this.topicName = ipStatsData.getTopicName();
			this.ip = ipStatsData.getIp();
		}

		private String topicName;

		private String ip;

		public String getTopicName() {
			return topicName;
		}

		public String getIp() {
			return ip;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
			result = prime * result + ((ip == null) ? 0 : ip.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			IpStatsDataKey other = (IpStatsDataKey) obj;
			if (topicName == null) {
				if (other.topicName != null)
					return false;
			} else if (!topicName.equals(other.topicName))
				return false;
			if (ip == null) {
				if (other.ip != null)
					return false;
			} else if (!ip.equals(other.ip))
				return false;
			return true;
		}

		@Override
		public String toString() {
			return "IpStatsDataKey [topicName=" + topicName + ", ip=" + ip + "]";
		}

	}

}
