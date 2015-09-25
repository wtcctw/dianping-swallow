package com.dianping.swallow.web.monitor.collector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.resource.ServerResource;
import com.dianping.swallow.web.model.resource.ServerType;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.IPCollectorService.ConsumerServer;
import com.dianping.swallow.web.service.IPCollectorService.ConsumerServerPair;
import com.dianping.swallow.web.service.ProducerServerResourceService;

/**
 * 
 * @author qiyin
 *
 *         2015年9月6日 上午10:54:32
 */
@Component
public class ServerResourceCollector extends AbstractResourceCollector {

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private ProducerServerResourceService pServerResourceService;

	@Autowired
	private ConsumerServerResourceService cServerResourceService;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		collectorName = getClass().getSimpleName();
		collectorInterval = 10;
		collectorDelay = 1;
	}

	@Override
	public void doCollector() {
		doServerCollector();
	}

	public void doServerCollector() {
		doProducerServerCollector();
		doConsumerServerCollector();
	}

	public void doProducerServerCollector() {
		Map<String, String> ipsMap = new HashMap<String, String>();
		List<String> serverIps = ipCollectorService.getProducerServerIps();
		Map<String, String> serverIpsMap = getServerIpsMap(ipCollectorService.getProducerServerIpsMap());
		Set<String> statsServerIps = producerStatsDataWapper.getServerIps(false);
		setServerMap(serverIps, ipsMap);

		if (serverIpsMap != null) {
			ipsMap.putAll(serverIpsMap);
		}

		setServerMap(statsServerIps, ipsMap);

		for (Map.Entry<String, String> ipEntry : ipsMap.entrySet()) {
			ServerResource serverResource = pServerResourceService.findByIp(ipEntry.getKey());
			if (serverResource == null) {
				ProducerServerResource pServerResource = pServerResourceService.buildProducerServerResource(
						ipEntry.getKey(), ipEntry.getValue());
				pServerResourceService.insert(pServerResource);
			}
		}
	}

	public void doConsumerServerCollector() {

		List<ConsumerServerPair> consumerServerPairs = new ArrayList<ConsumerServerPair>();
		List<ConsumerServerPair> collectorServerPairs = ipCollectorService.getConsumerServerPairs();
		if (collectorServerPairs != null) {
			consumerServerPairs.addAll(collectorServerPairs);
		}

		for (ConsumerServerPair consumerServerPair : consumerServerPairs) {
			ConsumerServer masterServer = consumerServerPair.getMasterServer();
			ConsumerServer slaveServer = consumerServerPair.getSlaveServer();
			int groupId = 0;
			if (StringUtils.isNotBlank(masterServer.getIp())) {
				ConsumerServerResource masterResource = (ConsumerServerResource) cServerResourceService
						.findByIp(masterServer.getIp());
				if (masterResource == null) {
					groupId = cServerResourceService.getNextGroupId();
					ConsumerServerResource cServerResource = cServerResourceService.buildConsumerServerResource(
							masterServer.getIp(), masterServer.getHostName(), masterServer.getPort(), groupId,
							ServerType.MASTER);
					cServerResourceService.insert(cServerResource);
					logger.info("[doConsumerServerCollector] masterServer {} is saved.", masterServer);
				} else {
					groupId = masterResource.getGroupId();
				}
			}
			if (StringUtils.isNotBlank(slaveServer.getIp())) {
				ServerResource slaveResource = cServerResourceService.findByIp(slaveServer.getIp());
				if (slaveResource == null) {
					ConsumerServerResource cServerResource = cServerResourceService.buildConsumerServerResource(
							slaveServer.getIp(), slaveServer.getHostName(), slaveServer.getPort(), groupId,
							ServerType.SLAVE);
					cServerResourceService.insert(cServerResource);
					logger.info("[doConsumerServerCollector] slaveServer {} is saved.", slaveServer);
				}
			}
		}

		Set<String> statsServerIps = consumerStatsDataWapper.getServerIps(false);
		for (String serverIp : statsServerIps) {
			ServerResource serverResource = cServerResourceService.findByIp(serverIp);
			if (serverResource == null) {
				ConsumerServerResource cServerResource = cServerResourceService.buildConsumerServerResource(serverIp,
						cServerResourceService.getNextGroupId());
				cServerResourceService.insert(cServerResource);
				logger.info("[doConsumerServerCollector] serverIp {} is saved.", serverIp);
			}
		}

	}

	private Map<String, String> getServerIpsMap(Map<String, String> serverIpsMap) {
		Map<String, String> resultIpsMap = null;
		if (serverIpsMap != null) {
			resultIpsMap = new HashMap<String, String>();
			for (Map.Entry<String, String> serverIpEntry : serverIpsMap.entrySet()) {
				resultIpsMap.put(serverIpEntry.getValue(), serverIpEntry.getKey());
			}
		}
		return resultIpsMap;
	}

	private void setServerMap(Collection<String> serverIps, Map<String, String> ipsMap) {
		if (serverIps != null) {
			for (String ip : serverIps) {
				if (!ipsMap.containsKey(ip)) {
					ipsMap.put(ip, StringUtils.EMPTY);
				}
			}
		}
	}

	@Override
	public int getCollectorInterval() {
		return collectorInterval;
	}

	public void setCollectorInterval(int collectorInterval) {
		this.collectorInterval = collectorInterval;
	}

	@Override
	public int getCollectorDelay() {
		return collectorDelay;
	}
}
