package com.dianping.swallow.web.monitor.collector;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.resource.ServerResource;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.ProducerServerResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年9月6日 上午10:54:32
 */
@Component
public class ServerResourceCollector extends AbstractResourceCollector {

	private static final Logger logger = LoggerFactory.getLogger(ServerResourceCollector.class);

	private static final String FACTORY_NAME = "ServerResourceCollector";

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

	private int collectorInterval = 360;

	private int delayInterval = 60;

	private ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));

	@PostConstruct
	public void doScheduledTask() {
		scheduled.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, ServerResourceCollector.class
							.getSimpleName());
					catWrapper.doAction(new SwallowAction() {
						@Override
						public void doAction() throws SwallowException {
							doServerCollector();
						}
					});
				} catch (Throwable th) {
					logger.error("[run]", th);
				} finally {

				}
			}
		}, getDelayInterval(), getCollectorInterval(), TimeUnit.SECONDS);
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

	public int getCollectorInterval() {
		return collectorInterval;
	}

	public void setCollectorInterval(int collectorInterval) {
		this.collectorInterval = collectorInterval;
	}

	public int getDelayInterval() {
		return delayInterval;
	}

	public void setDelayInterval(int delayInterval) {
		this.delayInterval = delayInterval;
	}
}
