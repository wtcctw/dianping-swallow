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

	private Map<IpStatsDataKey, Long> firstCandidates = new ConcurrentHashMap<IpStatsDataKey, Long>();

	private Map<IpStatsDataKey, Long> secondCandidates = new ConcurrentHashMap<IpStatsDataKey, Long>();

	private Map<IpStatsDataKey, Long> whiteLists = new ConcurrentHashMap<IpStatsDataKey, Long>();

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
		if (ipGroupStatsDatas == null || ipGroupStatsDatas.isEmpty()) {
			return;
		}

		for (final ConsumerIpGroupStatsData ipGroupStatsData : ipGroupStatsDatas) {
			checkIpGroup(ipGroupStatsData);
		}
	}

	public void checkIpGroup(ConsumerIpGroupStatsData ipGroupStatsData) {
		boolean hasGroupStatsData = ipGroupStatsData.hasStatsData();
		List<ConsumerIpStatsData> ipStatsDatas = ipGroupStatsData.getConsumerIpStatsDatas();
		if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
			return;
		}
		for (ConsumerIpStatsData ipStatsData : ipStatsDatas) {
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
			Entry<IpStatsDataKey, Long> firstCandidate = iterator.next();
			IpStatsDataKey key = firstCandidate.getKey();
			long lastRecordTime = firstCandidate.getValue();
			if (System.currentTimeMillis() - lastRecordTime < CHECK_TIMESPAN) {
				continue;
			}
			iterator.remove();
			report(key.getTopicName(), key.getConsumerId(), key.getIp());
		}
	}

	public void alarmUnSureRecords() {

		Iterator<Entry<IpStatsDataKey, Long>> iterator = secondCandidates.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<IpStatsDataKey, Long> secondCandidate = iterator.next();
			IpStatsDataKey key = secondCandidate.getKey();
			long lastRecordTime = secondCandidate.getValue();

			if (System.currentTimeMillis() - lastRecordTime < CHECK_TIMESPAN) {
				continue;
			}
			iterator.remove();
			ConsumerIpQpsPair avgQpsPair = cIpStatsDataService.findAvgQps(key.getTopicName(), key.getConsumerId(),
					key.getIp(), getTimeKey(getPreNDayKey(1, CHECK_TIMESPAN)), getTimeKey(getPreNDayKey(1, 0)));

			if (avgQpsPair.getSendQps() > 0 || avgQpsPair.getAckQps() > 0) {
				report(key.getTopicName(), key.getConsumerId(), key.getIp());
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

	private void report(String topicName, String consumerId, String ip) {
		if (isReport(topicName, consumerId, ip)) {
			ConsumerClientEvent clientEvent = eventFactory.createCClientEvent();
			clientEvent.setConsumerId(consumerId).setTopicName(topicName).setIp(ip)
					.setClientType(ClientType.CLIENT_RECEIVER).setEventType(EventType.CONSUMER)
					.setCreateTime(new Date()).setCheckInterval(CHECK_TIMESPAN);
			eventReporter.report(clientEvent);
		}
	}

	private static class IpStatsDataKey {

		public IpStatsDataKey(ConsumerIpStatsData ipStatsData) {
			this.topicName = ipStatsData.getTopicName();
			this.consumerId = ipStatsData.getConsumerId();
			this.ip = ipStatsData.getIp();
		}

		private String topicName;

		private String consumerId;

		private String ip;

		public String getTopicName() {
			return topicName;
		}

		public String getConsumerId() {
			return consumerId;
		}

		public String getIp() {
			return ip;
		}

		@Override
		public String toString() {
			return "IpStatsDataKey [topicName=" + topicName + ", consumerId=" + consumerId + ", ip=" + ip + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
			result = prime * result + ((consumerId == null) ? 0 : consumerId.hashCode());
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
			if (consumerId == null) {
				if (other.consumerId != null)
					return false;
			} else if (!consumerId.equals(other.consumerId))
				return false;
			if (ip == null) {
				if (other.ip != null)
					return false;
			} else if (!ip.equals(other.ip))
				return false;
			return true;
		}

	}

}
