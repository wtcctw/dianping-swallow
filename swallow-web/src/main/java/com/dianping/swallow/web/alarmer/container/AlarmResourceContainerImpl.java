package com.dianping.swallow.web.alarmer.container;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.ProducerServerResourceService;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:34:10
 */
@Component("alarmResourceContainer")
public class AlarmResourceContainerImpl implements AlarmResourceContainer, InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AlarmResourceContainerImpl.class);

	private static final String DEFAULT_RECORD = "default";

	private static final String KEY_SPLIT = "&";

	private static final String DEFAULT_DEFAULT_RECORD = DEFAULT_RECORD + KEY_SPLIT + DEFAULT_RECORD;

	private int interval = 120;// 秒

	private int delay = 5;

	@SuppressWarnings("unused")
	private ScheduledFuture<?> future = null;

	private static final String FACTORY_NAME = "AlarmResourceTask";

	private ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));

	@Autowired
	private ConsumerServerResourceService cServerResourceService;

	@Autowired
	private ProducerServerResourceService pServerResourceService;

	@Autowired
	private TopicResourceService topicResourceService;

	@Autowired
	private ConsumerIdResourceService consumerIdResourceService;

	private volatile Map<String, ConsumerServerResource> cServerResources = null;

	private volatile Map<String, ProducerServerResource> pServerResources = null;

	private volatile Map<String, TopicResource> topicResources = null;

	private volatile Map<String, ConsumerIdResource> consumerIdResources = null;

	public void findResourceData() {
		findCServerResourceData();
		findPServerResourceData();
		findTopicResourceData();
		findConsumerIdResourceData();
	}

	private void findCServerResourceData() {
		List<ConsumerServerResource> tempResources = cServerResourceService.findAll();
		if (tempResources != null) {
			Map<String, ConsumerServerResource> newCServerResources = new HashMap<String, ConsumerServerResource>();
			for (ConsumerServerResource tempResource : tempResources) {
				newCServerResources.put(tempResource.getIp(),  tempResource);
			}
			cServerResources = newCServerResources;
		}
	}

	private void findPServerResourceData() {
		List<ProducerServerResource> tempResources = pServerResourceService.findAll();
		if (tempResources != null) {
			Map<String, ProducerServerResource> newPServerResources = new HashMap<String, ProducerServerResource>();
			for (ProducerServerResource tempResource : tempResources) {
				newPServerResources.put(tempResource.getIp(),  tempResource);
			}
			pServerResources = newPServerResources;
		}
	}

	private void findTopicResourceData() {
		List<TopicResource> tempResources = topicResourceService.findAll();
		if (tempResources != null) {
			Map<String, TopicResource> newTopicResources = new HashMap<String, TopicResource>();
			for (TopicResource tempResource : tempResources) {
				newTopicResources.put(tempResource.getTopic(), tempResource);
			}
			topicResources = newTopicResources;
		}
	}

	private void findConsumerIdResourceData() {
		List<ConsumerIdResource> tempResources = consumerIdResourceService.findAll();
		if (tempResources != null) {
			Map<String, ConsumerIdResource> newConsumerIdResources = new HashMap<String, ConsumerIdResource>();
			for (ConsumerIdResource tempResource : tempResources) {
				newConsumerIdResources.put(tempResource.generateKey(), tempResource);
			}
			consumerIdResources = newConsumerIdResources;
		}
	}

	private void scheduleResourceTask() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					doLoadResourceTask();
				} catch (Throwable th) {
					logger.error("[scheduleResourceTask]", th);
				} finally {

				}
			}

		}, delay, interval, TimeUnit.SECONDS);
	}

	private void doLoadResourceTask() {
		logger.info("[doLoadResourceTask] scheduled load setting data.");
		findResourceData();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduleResourceTask();
	}

	@Override
	public ConsumerServerResource findConsumerServerResource(String ip) {
		if (cServerResources == null) {
			return null;
		}
		if (cServerResources.containsKey(ip)) {
			cServerResources.get(ip);
		} else {
			cServerResources.get(DEFAULT_RECORD);
		}
		return null;
	}

	@Override
	public ProducerServerResource findProducerServerResource(String ip) {
		if (pServerResources == null) {
			return null;
		}
		if (pServerResources.containsKey(ip)) {
			return pServerResources.get(ip);
		} else {
			return pServerResources.get(DEFAULT_RECORD);
		}
	}

	@Override
	public TopicResource findTopicResource(String topic) {
		if (topicResources == null) {
			return null;
		}
		if (topicResources.containsKey(topic)) {
			return topicResources.get(topic);
		} else {
			return topicResources.get(DEFAULT_RECORD);
		}
	}

	@Override
	public ConsumerIdResource findConsumerIdResource(String topicName, String consumerId) {
		if (consumerIdResources == null) {
			return null;
		}
		String key = topicName + KEY_SPLIT + consumerId;
		if (consumerIdResources.containsKey(key)) {
			return consumerIdResources.get(key);
		} else {
			return consumerIdResources.get(DEFAULT_DEFAULT_RECORD);
		}
	}

	@Override
	public List<TopicResource> findTopicResources() {
		if (topicResources != null) {
			return new ArrayList<TopicResource>(topicResources.values());
		}
		return null;
	}

	@Override
	public List<ConsumerIdResource> findConsumerIdResources() {
		if (consumerIdResources != null) {
			return new ArrayList<ConsumerIdResource>(consumerIdResources.values());
		}
		return null;
	}
}
