package com.dianping.swallow.web.monitor.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.controller.listener.ResourceListener;
import com.dianping.swallow.web.controller.listener.ResourceObserver;
import com.dianping.swallow.web.model.resource.BaseResource;
import com.dianping.swallow.web.util.CountDownLatchUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIdResourceService;

/**
 * @author qiyin
 *         <p/>
 *         2015年10月8日 上午11:06:00
 */
@Component
public class ConsumerIdResourceCollector extends AbstractRealTimeCollector implements MonitorDataListener,
		ResourceObserver {

	@Autowired
	private ConsumerStatsDataWapper cStatsDataWapper;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerIdResourceService cResourceService;

	@Autowired
	private ResourceContainer resourceContainer;

	private IpStatusMonitor<ConsumerIdKey, ConsumerIpStatsData> ipStatusMonitor = new IpStatusMonitorImpl<ConsumerIdKey, ConsumerIpStatsData>();

	private List<ResourceListener> listeners = new ArrayList<ResourceListener>();

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		collectorName = getClass().getSimpleName();
		consumerDataRetriever.registerListener(this);
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
						doCollector();
					}
				});
			}
		});
	}

	private void doIpDataMonitor(String topicName, String consumerId) {
		List<ConsumerIpStatsData> ipStatsDatas = cStatsDataWapper.getIpStatsDatas(topicName, consumerId, -1, false);
		ipStatusMonitor.putActiveIpDatas(new ConsumerIdKey(topicName, consumerId), ipStatsDatas);
		updateConsumerIdResource(topicName, consumerId);
	}

	@Override
	public void doCollector() {
		logger.info("[doCollector] start collect consumerIdResource.");
		Set<String> topicNames = cStatsDataWapper.getTopics(false);
		if (topicNames == null || topicNames.isEmpty()) {
			return;
		}
		for (final String topicName : topicNames) {
			if (StringUtils.isBlank(topicName)) {
				continue;
			}
			Set<String> consumerIds = cStatsDataWapper.getConsumerIds(topicName, false);
			if (consumerIds == null || consumerIds.isEmpty()) {
				continue;
			}
			final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(consumerIds.size());
			for (final String consumerId : consumerIds) {
				try {
					executor.submit(new Runnable() {
						@Override
						public void run() {
							try {
								doIpDataMonitor(topicName, consumerId);
							} catch (Throwable t) {
								logger.error("[run] server {} doIpDataMonitor error.", topicName, t);
							} finally {
								downLatch.countDown();
							}
						}
					});
				} catch (Throwable t) {
					logger.error("[submit] [doCollector] executor thread submit error.", t);
				} finally {
					downLatch.countDown();
				}
			}
			CountDownLatchUtil.await(downLatch);
		}
	}

	private void updateConsumerIdResource(String topicName, String consumerId) {
		ConsumerIdKey consumerIdKey = new ConsumerIdKey(topicName, consumerId);
		ConsumerIdResource consumerIdResource = resourceContainer.findConsumerIdResource(topicName, consumerId, false);
		boolean result = false;
		if (consumerIdResource == null) {
			List<IpInfo> currentIpInfos = ipStatusMonitor.getRelatedIpInfo(consumerIdKey, null);
			consumerIdResource = cResourceService.buildConsumerIdResource(topicName, consumerId);
			consumerIdResource.setConsumerIpInfos(currentIpInfos);
			result = cResourceService.insert(consumerIdResource);
			logger.info("[updateConsumerIdResource] insert consumerIdResource {}", consumerIdResource.toString());
		} else {
			List<IpInfo> currentIpInfos = ipStatusMonitor.getRelatedIpInfo(consumerIdKey,
					consumerIdResource.getConsumerIpInfos());
			if (ipStatusMonitor.isChanged(consumerIdResource.getConsumerIpInfos(), currentIpInfos)) {
				consumerIdResource.setConsumerIpInfos(currentIpInfos);
				result = cResourceService.update(consumerIdResource);
				logger.info("[updateConsumerIdResource] update consumerIdResource {}", consumerIdResource.toString());
			}
		}
		if (result) {
			doUpdateNotify(consumerIdResource);
		}
	}

	@Override
	public void doRegister(ResourceListener listener) {
		listeners.add(listener);
	}

	@Override
	public void doUpdateNotify(BaseResource resource) {
		for (ResourceListener listener : listeners) {
			listener.doUpdateNotify(resource);
		}
	}

	@Override
	public void doDeleteNotify(BaseResource resource) {
		for (ResourceListener listener : listeners) {
			listener.doDeleteNotify(resource);
		}
	}

	public static class ConsumerIdKey {

		private String topicName;

		private String consumerId;

		public ConsumerIdKey(String topicName, String consumerId) {
			this.topicName = topicName;
			this.consumerId = consumerId;
		}

		public String getTopicName() {
			return topicName;
		}

		public void setTopicName(String topicName) {
			this.topicName = topicName;
		}

		public String getConsumerId() {
			return consumerId;
		}

		public void setConsumerId(String consumerId) {
			this.consumerId = consumerId;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
			result = prime * result + ((consumerId == null) ? 0 : consumerId.hashCode());
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
			ConsumerIdKey other = (ConsumerIdKey) obj;
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
			return true;
		}

	}

}
