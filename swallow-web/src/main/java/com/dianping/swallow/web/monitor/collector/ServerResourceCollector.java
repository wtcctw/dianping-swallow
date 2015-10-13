package com.dianping.swallow.web.monitor.collector;

import java.util.ArrayList;
import java.util.List;
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
import com.dianping.swallow.web.service.IPCollectorService.ProducerServer;
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
		logger.info("[doCollector] start collect serverResource.");
		doServerCollector();
	}

	public void doServerCollector() {
		doProducerServerCollector();
		doConsumerServerCollector();
	}

	public void doProducerServerCollector() {
		List<ProducerServer> producerServers = new ArrayList<ProducerServer>();
		List<ProducerServer> collectorServers = ipCollectorService.getProducerServers();
		if (collectorServers != null && !collectorServers.isEmpty()) {
			producerServers.addAll(collectorServers);
		}
		Set<String> statsServerIps = producerStatsDataWapper.getServerIps(false);
		addProducerServer(producerServers, statsServerIps);

		for (ProducerServer producerServer : producerServers) {
			ServerResource serverResource = pServerResourceService.findByIp(producerServer.getIp());
			if (serverResource == null) {
				ProducerServerResource pServerResource = pServerResourceService.buildProducerServerResource(
						producerServer.getIp(), producerServer.getHostName());
				pServerResourceService.insert(pServerResource);
			}
		}
	}

	public void doConsumerServerCollector() {

		List<ConsumerServerPair> collectorServerPairs = ipCollectorService.getConsumerServerPairs();

		for (ConsumerServerPair consumerServerPair : collectorServerPairs) {
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

	private void addProducerServer(List<ProducerServer> producerServers, Set<String> statsServerIps) {
		if (statsServerIps != null && !statsServerIps.isEmpty()) {
			for (String serverIp : statsServerIps) {
				boolean isExist = false;
				for (ProducerServer producerServer : producerServers) {
					if (producerServer.equalsIp(serverIp)) {
						isExist = true;
					}
				}
				if (!isExist) {
					producerServers.add(new ProducerServer(serverIp, StringUtils.EMPTY));
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
