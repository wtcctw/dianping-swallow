package com.dianping.swallow.web.monitor.collector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ProducerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年9月30日 上午11:24:59
 */
@Component
public class TopicResourceCollector extends AbstractResourceCollector implements MonitorDataListener {

	@Autowired
	private TopicResourceService topicResourceService;

	@Autowired
	private ProducerStatsDataWapper pStatsDataWapper;

	private static final String FACTORY_NAME = "ResourceCollector-TopicIpMonitor";

	private ExecutorService executor = null;

	private long lastTimeKey;

	private static final long ACTIVE_TIMESPAN = 6 * 60 * 60 * 1000;

	private Map<String, ActiveIpData> topicActiveIpDatas = new ConcurrentHashMap<String, ActiveIpData>();

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		collectorName = getClass().getSimpleName();
		collectorInterval = 20;
		collectorDelay = 1;
		lastTimeKey = -1;
		executor = Executors.newSingleThreadExecutor(ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));
	}

	@Override
	public void achieveMonitorData() {
		executor.submit(new Runnable() {
			@Override
			public void run() {
				SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, collectorName + "-IpMonitor");
				catWrapper.doAction(new SwallowAction() {
					@Override
					public void doAction() throws SwallowException {
						doIpDataMonitor();
					}
				});
			}
		});
	}

	private void doIpDataMonitor() {
		List<ProducerIpGroupStatsData> ipGroupStatsDatas = pStatsDataWapper.getIpGroupStatsDatas(lastTimeKey, false);
		if (ipGroupStatsDatas == null || ipGroupStatsDatas.isEmpty()) {
			return;
		}
		for (ProducerIpGroupStatsData ipGroupStatsData : ipGroupStatsDatas) {
			if (ipGroupStatsData == null) {
				continue;
			}
			List<ProducerIpStatsData> ipStatsDatas = ipGroupStatsData.getProducerIpStatsDatas();
			if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
				continue;
			}
			for (ProducerIpStatsData ipStatsData : ipStatsDatas) {
				ActiveIpData activeIpData = null;
				if (topicActiveIpDatas.containsKey(ipStatsData.getTopicName())) {
					activeIpData = topicActiveIpDatas.get(ipStatsData.getTopicName());

				} else {
					activeIpData = new ActiveIpData();
					topicActiveIpDatas.put(ipStatsData.getTopicName(), activeIpData);
				}
				activeIpData.addData(ipStatsData.getIp(), ipStatsData.hasStatsData());
			}
		}
	}

	@Override
	public void doCollector() {
		logger.info("[doCollector] start collect topicResource.");
		doTopicCollector();
	}

	private void doTopicCollector() {
		Set<String> topicNames = pStatsDataWapper.getTopics(false);
		if (topicNames != null && !topicNames.isEmpty()) {
			for (String topicName : topicNames) {
				ProducerIpGroupStatsData ipGroupStatsData = pStatsDataWapper.getIpGroupStatsData(topicName,
						lastTimeKey, false);
				Set<String> activeIps = getActiveGroupIps(ipGroupStatsData);
				updateTopicIpInfos(topicName,activeIps);
			}
		}
	}

	private void updateTopicIpInfos(String topicName, Set<String> activeIps) {
		TopicResource topicResource = topicResourceService.findByTopic(topicName);
		if (topicResource != null) {
			List<IpInfo> ipInfos = topicResource.getProducerIpInfos();
			if ((activeIps == null || activeIps.isEmpty()) && (ipInfos == null || ipInfos.isEmpty())) {
				return;
			}
			if (ipInfos == null || ipInfos.isEmpty()) {
				ipInfos = new ArrayList<IpInfo>();
			} else if (activeIps == null || activeIps.isEmpty()) {
				for (IpInfo ipInfo : ipInfos) {
					ipInfo.setActive(false);
				}
			} else {
				for (String activeIp : activeIps) {
					if (StringUtils.isBlank(activeIp)) {
						continue;
					}
					boolean isIpExist = false;
					for (IpInfo ipInfo : ipInfos) {
						if (activeIp.equals(ipInfo.getIp())) {
							ipInfo.setActive(true);
							isIpExist = true;
						}
					}
					if (!isIpExist) {
						ipInfos.add(new IpInfo(activeIp, true, true));
					}
				}
			}
			topicResource.setProducerIpInfos(ipInfos);
			topicResourceService.update(topicResource);
		}
	}

	private Set<String> getActiveGroupIps(ProducerIpGroupStatsData ipGroupStatsData) {
		if (ipGroupStatsData == null) {
			return null;
		}
		List<ProducerIpStatsData> ipStatsDatas = ipGroupStatsData.getProducerIpStatsDatas();
		if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
			return null;
		} else {
			Set<String> activeIps = new HashSet<String>();
			for (ProducerIpStatsData ipStatsData : ipStatsDatas) {
				if (topicActiveIpDatas.containsKey(ipStatsData.getTopicName())) {
					ActiveIpData activeIpData = topicActiveIpDatas.get(ipStatsData.getTopicName());
					if (activeIpData.isIpActive(ipStatsData.getIp())) {
						activeIps.add(ipStatsData.getIp());
					}
				} else {
					activeIps.add(ipStatsData.getIp());
				}
			}
			return activeIps;
		}
	}

	@Override
	public int getCollectorDelay() {
		return collectorDelay;
	}

	@Override
	public int getCollectorInterval() {
		return collectorInterval;
	}

	public static class ActiveIpData {

		private Map<String, Long> activeDatas = new ConcurrentHashMap<String, Long>();

		public void addData(String ip, boolean hasData) {
			if (hasData) {
				activeDatas.put(ip, System.currentTimeMillis());
			} else {
				if (!activeDatas.containsKey(ip)) {
					activeDatas.put(ip, System.currentTimeMillis());
				}
			}
		}

		public boolean isIpActive(String ip) {
			if (activeDatas.containsKey(ip)) {
				return System.currentTimeMillis() - activeDatas.get(ip) > ACTIVE_TIMESPAN;
			}
			return true;
		}
	}

}
