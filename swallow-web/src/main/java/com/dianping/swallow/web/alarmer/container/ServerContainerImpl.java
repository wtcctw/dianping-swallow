package com.dianping.swallow.web.alarmer.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.container.ResourceContainer.ConsumerServerResourcePair;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.resource.ServerType;
import com.dianping.swallow.web.model.server.ConsumerHAServer;
import com.dianping.swallow.web.model.server.ConsumerServer;
import com.dianping.swallow.web.model.server.ProducerServer;
import com.dianping.swallow.web.model.server.ServerFactory;

/**
 * 
 * @author qiyin
 *
 *         2015年10月19日 下午2:33:24
 */
@Component
public class ServerContainerImpl extends AbstractAlamerContainer implements ServerContainer {

	private Map<String, ProducerServer> producerServers = new ConcurrentHashMap<String, ProducerServer>();

	private Map<String, ConsumerHAServer> consumerHAServers = new ConcurrentHashMap<String, ConsumerHAServer>();

	@Autowired
	protected ResourceContainer resourceContainer;

	@Autowired
	protected ServerFactory serverFactory;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		interval = 30;
		delay = 6;
		containerName = "ServerContainer";
	}

	@Override
	public Map<String, ConsumerHAServer> getConsumerHAServers() {
		return new HashMap<String, ConsumerHAServer>(consumerHAServers);
	}

	@Override
	public Map<String, ProducerServer> getProducerServers() {
		return new HashMap<String, ProducerServer>(producerServers);
	}

	@Override
	public void doLoadResource() {
		logger.info("[doLoadResource] scheduled load server info.");
		updateProducerServers();
		updateConsumerServers();
	}

	public void updateProducerServers() {
		List<ProducerServerResource> serverResources = resourceContainer.findProducerServerResources(false);
		removeProducerServer(serverResources);
		addProducerServer(serverResources);
	}

	private void removeProducerServer(List<ProducerServerResource> serverResources) {
		Iterator<Entry<String, ProducerServer>> serverIterator = producerServers.entrySet().iterator();
		while (serverIterator.hasNext()) {
			Entry<String, ProducerServer> entryServer = serverIterator.next();
			String ip = entryServer.getKey();
			boolean isHasElement = false;
			if (serverResources == null) {
				isHasElement = false;
			} else {
				for (ProducerServerResource serverResource : serverResources) {
					if (serverResource.isAlarm()) {
						if (StringUtils.equals(ip, serverResource.getIp())) {
							isHasElement = true;
							break;
						}
					}
				}
			}
			if (!isHasElement) {
				serverIterator.remove();
			}
		}
	}

	private void addProducerServer(List<ProducerServerResource> serverResources) {
		for (ProducerServerResource serverResource : serverResources) {
			String ip = serverResource.getIp();
			if (StringUtils.isBlank(ip) || !serverResource.isAlarm()) {
				continue;
			}
			if (!producerServers.containsKey(ip)) {
				producerServers.put(ip, serverFactory.createProducerServer(ip));
			}
		}
	}

	public void updateConsumerServers() {
		List<ConsumerServerResourcePair> serverResourcePairs = resourceContainer.findConsumerServerResourcePairs();
		removeConsumerServer(serverResourcePairs);
		addConsumerServer(serverResourcePairs);
	}

	private void removeConsumerServer(List<ConsumerServerResourcePair> serverResourcePairs) {
		Iterator<Entry<String, ConsumerHAServer>> serverIterator = consumerHAServers.entrySet().iterator();
		while (serverIterator.hasNext()) {
			Entry<String, ConsumerHAServer> entryServer = serverIterator.next();
			String ip = entryServer.getKey();
			boolean isHasElement = false;
			if (serverResourcePairs == null) {
				isHasElement = false;
			} else {
				for (ConsumerServerResourcePair serverResourcePair : serverResourcePairs) {
					ConsumerServerResource serverResource = serverResourcePair.getMasterResource();
					if (serverResource.getType() == ServerType.MASTER && serverResource.isAlarm()) {
						if (StringUtils.equals(ip, serverResource.getIp())) {
							isHasElement = true;
							break;
						}
					}
				}
			}
			if (!isHasElement) {
				serverIterator.remove();
			}
		}
	}

	private void addConsumerServer(List<ConsumerServerResourcePair> serverResourcePairs) {
		if (serverResourcePairs == null) {
			return;
		}
		for (ConsumerServerResourcePair serverResourcePair : serverResourcePairs) {
			ConsumerServerResource masterResource = serverResourcePair.getMasterResource();
			ConsumerServerResource slaveResource = serverResourcePair.getSlaveResource();
			String masterIp = masterResource.getIp();
			int masterPort = masterResource.getPort();
			String slaveIp = slaveResource.getIp();
			int slavePort = slaveResource.getPort();
			if (StringUtils.isBlank(masterIp) || !masterResource.isAlarm()) {
				continue;
			}
			ConsumerServer masterServer = serverFactory.createConsumerServer(masterIp, masterPort, true);
			ConsumerServer slaveServer = serverFactory.createConsumerServer(slaveIp, slavePort, false);
			if (!consumerHAServers.containsKey(masterIp)) {
				consumerHAServers.put(masterIp, serverFactory.createConsumerHAServer(masterServer, slaveServer));
			}
		}
	}
	
	@Override
	public int getInterval() {
		return interval;
	}

	@Override
	public int getDelay() {
		return delay;
	}

}
