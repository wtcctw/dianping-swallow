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

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.dao.ConsumerIdResourceDao.ConsumerIdParam;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * @author mingdongli
 *
 *         2015年8月31日下午8:14:56
 */
public class ConsumerIdResourceCollector implements MonitorDataListener, Runnable {

	private static final String FACTORY_NAME = "ConsumerIdResourceCollector";

	@Resource(name = "consumerIdResourceService")
	private ConsumerIdResourceService consumerIdResourceService;

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
						ConsumerIdResource consumerIdResource = buildConsumerIdResource(topic, cid, ips);
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

	private boolean valid(String topic, String consumerId) {

		if (StringUtil.isNotBlank(topic) && StringUtil.isNotBlank(consumerId)) {
			return true;
		} else {
			return false;
		}
	}

	private ConsumerIdResource buildConsumerIdResource(String topic, String consumerId, Set<String> ips) {

		ConsumerIdResource consumerIdResource = new ConsumerIdResource();
		consumerIdResource.setAlarm(Boolean.TRUE);
		consumerIdResource.setTopic(topic);
		consumerIdResource.setConsumerId(consumerId);

		List<String> ipList = null;

		if (ips == null) {
			ipList = new ArrayList<String>();
		} else {
			ipList = new ArrayList<String>(ips);
		}

		consumerIdResource.setConsumerIps(ipList);

		ConsumerIdResource defaultResource = consumerIdResourceService.findDefault();
		if (defaultResource == null) {
			throw new RuntimeException("No default configuration for ConsumerIdResource");
		}
		consumerIdResource.setConsumerAlarmSetting(defaultResource.getConsumerAlarmSetting());

		return consumerIdResource;
	}

	@Override
	public void run() {

		try {
			SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "doCollector");
			catWrapper.doAction(new SwallowAction() {
				@Override
				public void doAction() throws SwallowException {
					flushConsumerIdMetaData();
				}
			});
		} catch (Throwable th) {
			logger.error("[startConsumerIdResourceCollector]", th);
		} finally {

		}
	}

}
