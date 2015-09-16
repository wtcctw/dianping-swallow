package com.dianping.swallow.web.monitor.collector;

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
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.IPCollectorService;
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
		collectorInterval = 6;
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
		Map<String, String> ipsMap = new HashMap<String, String>();
		List<String> masterIps = ipCollectorService.getConsumerServerMasterIps();
		List<String> slaveIps = ipCollectorService.getConsumerServerSlaveIps();
		Map<String, String> masterIpsMap = getServerIpsMap(ipCollectorService.getConsumerServerMasterIpsMap());
		Map<String, String> slaveIpsMap = getServerIpsMap(ipCollectorService.getConsumerServerSlaveIpsMap());
		Set<String> statsServerIps = consumerStatsDataWapper.getServerIps(false);
		setServerMap(masterIps, ipsMap);
		setServerMap(slaveIps, ipsMap);
		if (masterIpsMap != null) {
			ipsMap.putAll(masterIpsMap);
		}
		if (slaveIpsMap != null) {
			ipsMap.putAll(slaveIpsMap);
		}
		setServerMap(statsServerIps, ipsMap);
		for (Map.Entry<String, String> ipEntry : ipsMap.entrySet()) {
			ServerResource serverResource = cServerResourceService.findByIp(ipEntry.getKey());
			if (serverResource == null) {
				ConsumerServerResource cServerResource = cServerResourceService.buildConsumerServerResource(
						ipEntry.getKey(), ipEntry.getValue());
				cServerResourceService.insert(cServerResource);
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
