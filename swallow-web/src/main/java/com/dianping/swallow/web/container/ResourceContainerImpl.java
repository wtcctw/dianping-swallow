package com.dianping.swallow.web.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.resource.BaseResource;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.resource.ServerType;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.service.ConsumerIdResourceService;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.ProducerServerResourceService;
import com.dianping.swallow.web.service.TopicResourceService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:34:10
 */
@Component("alarmResourceContainer")
public class ResourceContainerImpl extends AbstractContainer implements ResourceContainer {

	private static final String DEFAULT_RECORD = "default";

	private static final String KEY_SPLIT = "&";

	private static final String DEFAULT_DEFAULT_RECORD = DEFAULT_RECORD + KEY_SPLIT + DEFAULT_RECORD;

	@Autowired
	private ConsumerServerResourceService cServerResourceService;

	@Autowired
	private ProducerServerResourceService pServerResourceService;

	@Autowired
	private TopicResourceService topicResourceService;

	@Autowired
	private ConsumerIdResourceService consumerIdResourceService;

	private volatile Map<String, ConsumerServerResource> cServerResources = null;

	private volatile List<ConsumerServerResource> cMasterServerResources = null;

	private volatile List<ConsumerServerResource> cSlaveServerResources = null;

	private volatile List<ConsumerServerResourcePair> cServerResourcePairs = null;

	private volatile Map<String, ProducerServerResource> pServerResources = null;

	private volatile Map<String, TopicResource> topicResources = null;

	private volatile Map<String, ConsumerIdResource> consumerIdResources = null;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		containerName = "AlarmResourceContainer";
		delay = 5;
		interval = 600;
	}

	public void findResourceData() {
		findCServerResourceData();
		findPServerResourceData();
		findTopicResourceData();
		findConsumerIdResourceData();
	}

	private void findCServerResourceData() {
		try {
			List<ConsumerServerResource> tempResources = cServerResourceService.findAll();
			if (tempResources != null) {
				Map<String, ConsumerServerResource> newCServerResources = new HashMap<String, ConsumerServerResource>();
				for (ConsumerServerResource tempResource : tempResources) {
					newCServerResources.put(tempResource.getIp(), tempResource);
				}
				cServerResources = newCServerResources;
				findCHAServerResourceData(tempResources);
			}
		} catch (Exception e) {
			logger.error("[findCServerResourceData] find ConsumerServerResource error.", e);
		}
	}

	private void findCHAServerResourceData(List<ConsumerServerResource> tempResources) {
		List<ConsumerServerResource> newCMasterServerResources = new ArrayList<ConsumerServerResource>();
		List<ConsumerServerResource> newCSlaveServerResources = new ArrayList<ConsumerServerResource>();
		List<ConsumerServerResourcePair> newCServerResourcePairs = new ArrayList<ConsumerServerResourcePair>();
		for (ConsumerServerResource masterResource : tempResources) {
			if (!(masterResource.getType() == ServerType.MASTER)) {
				continue;
			}
			if (masterResource.getGroupId() != 0) {
				continue;
			}
			for (ConsumerServerResource slaveResource : tempResources) {
				if (!(masterResource.getType() == ServerType.SLAVE)) {
					continue;
				}
				if (!slaveResource.getIp().equals(masterResource.getGroupId())) {
					continue;
				}
				newCMasterServerResources.add(masterResource);
				newCSlaveServerResources.add(slaveResource);
				newCServerResourcePairs.add(new ConsumerServerResourcePair(masterResource, slaveResource));
				break;
			}
		}
		cMasterServerResources = newCMasterServerResources;
		logger.info("consumer master server resource {}.", cMasterServerResources);
		cSlaveServerResources = newCSlaveServerResources;
		logger.info("consumer slave server resource {}.", cSlaveServerResources);
		cServerResourcePairs = newCServerResourcePairs;
		logger.info("consumer server pair resource {}.", cServerResourcePairs);

	}

	private void findPServerResourceData() {
		try {
			List<ProducerServerResource> tempResources = pServerResourceService.findAll();
			if (tempResources != null) {
				Map<String, ProducerServerResource> newPServerResources = new HashMap<String, ProducerServerResource>();
				for (ProducerServerResource tempResource : tempResources) {
					newPServerResources.put(tempResource.getIp(), tempResource);
				}
				pServerResources = newPServerResources;
			}
		} catch (Exception e) {
			logger.error("[findPServerResourceData] find ProducerServerResource error.", e);
		}
	}

	private void findTopicResourceData() {
		try {
			List<TopicResource> tempResources = topicResourceService.findAll();
			if (tempResources != null) {
				Map<String, TopicResource> newTopicResources = new HashMap<String, TopicResource>();
				for (TopicResource tempResource : tempResources) {
					newTopicResources.put(tempResource.getTopic(), tempResource);
				}
				topicResources = newTopicResources;
			}
		} catch (Exception e) {
			logger.error("[findTopicResourceData] find TopicResource error.", e);
		}
	}

	private void findConsumerIdResourceData() {
		try {
			List<ConsumerIdResource> tempResources = consumerIdResourceService.findAll();
			if (tempResources != null) {
				Map<String, ConsumerIdResource> newConsumerIdResources = new HashMap<String, ConsumerIdResource>();
				for (ConsumerIdResource tempResource : tempResources) {
					newConsumerIdResources.put(tempResource.generateKey(), tempResource);
				}
				consumerIdResources = newConsumerIdResources;
			}
		} catch (Exception e) {
			logger.error("[findConsumerIdResourceData] find ConsumerIdResource error.", e);
		}
	}

	@Override
	public void doLoadResource() {
		logger.info("[doLoadResource] scheduled load resource data.");
		findResourceData();
	}

	@Override
	public ConsumerServerResource findConsumerServerResource(String ip) {
		if (cServerResources == null) {
			return null;
		}
		if (cServerResources.containsKey(ip)) {
			return cServerResources.get(ip);
		} else {
			return cServerResources.get(DEFAULT_RECORD);
		}
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
	public List<ConsumerServerResource> findConsumerServerResources(boolean isDefault) {
		if (cServerResources != null) {
			List<ConsumerServerResource> results = new ArrayList<ConsumerServerResource>(cServerResources.values());
			if (!isDefault) {
				removeDefault(results);
			}
			return results;
		}
		return null;
	}

	@Override
	public List<ConsumerServerResource> findConsumerMasterServerResources() {
		if (cMasterServerResources != null) {
			return Collections.unmodifiableList(cMasterServerResources);
		}
		return null;
	}

	@Override
	public List<ConsumerServerResource> findConsumerSlaveServerResources() {
		if (cSlaveServerResources != null) {
			return Collections.unmodifiableList(cSlaveServerResources);
		}
		return null;
	}

	@Override
	public List<ConsumerServerResourcePair> findConsumerServerResourcePairs() {
		if (cServerResourcePairs != null) {
			return Collections.unmodifiableList(cServerResourcePairs);
		}
		return null;
	}

	@Override
	public List<ProducerServerResource> findProducerServerResources(boolean isDefault) {
		if (pServerResources != null) {
			List<ProducerServerResource> results = new ArrayList<ProducerServerResource>(pServerResources.values());
			if (!isDefault) {
				removeDefault(results);
			}
			return results;
		}
		return null;
	}

	@Override
	public List<TopicResource> findTopicResources(boolean isDefault) {
		if (topicResources != null) {
			List<TopicResource> results = new ArrayList<TopicResource>(topicResources.values());
			if (!isDefault) {
				removeDefault(results);
			}
			return results;
		}
		return null;
	}

	@Override
	public List<ConsumerIdResource> findConsumerIdResources(boolean isDefault) {
		if (consumerIdResources != null) {
			List<ConsumerIdResource> results = new ArrayList<ConsumerIdResource>(consumerIdResources.values());
			if (!isDefault) {
				removeDefault(results);
			}
			return results;
		}
		return null;
	}

	private <T extends BaseResource> void removeDefault(List<T> resources) {
		if (resources != null) {
			for (Iterator<T> iterator = resources.iterator(); iterator.hasNext();) {
				T serverResource = iterator.next();
				if (serverResource.isDefault()) {
					resources.remove(serverResource);
					break;
				}
			}
		}
	}

	@Override
	public int getDelay() {
		return delay;

	}

	@Override
	public int getInterval() {
		return interval;
	}

}
