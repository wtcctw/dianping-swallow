package com.dianping.swallow.web.monitor.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import jodd.util.StringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao.ConsumerIdParam;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * @author mingdongli
 *
 *         2015年8月31日下午8:14:56
 */
@Component
public class IpResourceCollector implements MonitorDataListener, Runnable {

	private static final String FACTORY_NAME = "ConsumerIdResourceCollector";

	@Resource(name = "consumerIdResourceService")
	private ConsumerIdResourceService consumerIdResourceService;

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Resource(name = "producerStatsDataWapper")
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	private AtomicBoolean minute = new AtomicBoolean(false);

	private ExecutorService scheduled = Executors.newSingleThreadExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@PostConstruct
	void updateDashboardContainer() {

		consumerDataRetrieverWrapper.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {

		if (minute.get()) {
			try {
				logger.info("[startCollectConsumerIdResource]");
				scheduled.submit(this);
			} catch (Exception e) {
				if (logger.isErrorEnabled()) {
					logger.error("Error when flush consumerid data to database.", e);
				}
			} finally {
				minute.compareAndSet(true, false);
			}
		} else {
			minute.compareAndSet(false, true);
		}

	}

	private void flushConsumerIdMetaData() {

		Set<String> topics = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL);
		for (String topic : topics) {
			Set<String> consumerids = consumerDataRetrieverWrapper.getKeyWithoutTotal(
					ConsumerDataRetrieverWrapper.TOTAL, topic);
			for (String cid : consumerids) {
				Set<String> ips = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL,
						topic, cid);
				if (valid(topic, cid)) {
					ConsumerIdParam consumerIdParam = new ConsumerIdParam();
					consumerIdParam.setConsumerId(cid);
					consumerIdParam.setTopic(topic);
					Pair<Long, List<ConsumerIdResource>> pair = consumerIdResourceService.find(consumerIdParam);

					if (pair.getFirst() == 0) {
						ConsumerIdResource consumerIdResource = consumerIdResourceService.buildConsumerIdResource(
								topic, cid);
						if (ips != null && !ips.isEmpty()) {
							consumerIdResource.setConsumerIps(new ArrayList<String>(ips));
						}
						consumerIdResourceService.insert(consumerIdResource);
					} else {
						if (ips != null && !ips.isEmpty()) {
							ConsumerIdResource consumerIdResource = pair.getSecond().get(0);
							consumerIdResource.setConsumerIps(new ArrayList<String>(ips));
							consumerIdResourceService.insert(consumerIdResource);
						}
					}
				}
			}
		}
	}

	private void flushTopicMetaData() {

		Set<String> topics = producerStatsDataWapper.getTopics();
		if (topics != null) {
			for (String topic : topics) {
				Set<String> ips = producerStatsDataWapper.getTopicIps(topic);
				if (ips != null) {
					if(ips.contains(ConsumerDataRetrieverWrapper.TOTAL)){
						ips.remove(ConsumerDataRetrieverWrapper.TOTAL);
					}
					TopicResource topicResource = topicResourceService.findByTopic(topic);
					List<String> ipList = new ArrayList<String>(ips);

					if (topicResource == null) {
						topicResource = topicResourceService.buildTopicResource(topic);
						topicResource.setProducerIps(ipList);
						topicResourceService.insert(topicResource);
					} else {
						topicResource.setProducerIps(ipList);
						topicResourceService.insert(topicResource);
					}

				}
			}
		}
	}

	private boolean valid(String topic, String consumerId) {

		if (StringUtil.isNotBlank(topic) && StringUtil.isNotBlank(consumerId)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void run() {

		try {
			SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "doIPCollector");
			catWrapper.doAction(new SwallowAction() {
				@Override
				public void doAction() throws SwallowException {
					flushConsumerIdMetaData();
					flushTopicMetaData();
				}
			});
		} catch (Throwable th) {
			logger.error("[startConsumerIdResourceCollector]", th);
		} finally {

		}
	}

}
