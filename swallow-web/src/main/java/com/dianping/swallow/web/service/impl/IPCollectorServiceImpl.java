package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerIdData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ConsumerTopicData;
import com.dianping.swallow.common.server.monitor.data.structure.MessageInfo;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerServerData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerTopicData;
import com.dianping.swallow.web.model.cmdb.EnvDevice;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * 
 * @author qiyin
 *
 */
@Service("ipCollectorService")
public class IPCollectorServiceImpl implements IPCollectorService {

	private static final Logger logger = LoggerFactory.getLogger(IPCollectorServiceImpl.class);

	private static final String SWALLOW_PRODUCER_NAME = "swallow-producer";

	private static final String SWALLOW_CONSUMER_SLAVE_NAME = "swallow-consumerslave";

	private static final String SWALLOW_CONSUMER_MASTER_NAME = "swallow-consumermaster";

	private static final String CONSUMER_SERVER_MASTER_PORT_KEY = "swallow.consumer.server.master.port";

	private static final String CONSUMER_SERVER_SLAVE_PORT_KEY = "swallow.consumer.server.slave.port";

	private volatile int masterPort = 8081;

	private volatile int slavePort = 8082;

	private static final String FACTORY_NAME = "IpCollector";

	private static final String MASTER_NAME = "master";

	private static final String SLAVE_NAME = "slave";

	private Set<String> statisIps = new ConcurrentSkipListSet<String>();

	private Map<String, Long> statisConsumerServerIps = new ConcurrentHashMap<String, Long>();

	private Map<String, Long> statisProducerServerIps = new ConcurrentHashMap<String, Long>();

	private volatile Map<String, String> cmdbProducerMap = null;

	private volatile Map<String, String> cmdbConsumerSlaveMap = null;

	private volatile Map<String, String> cmdbConsumerMasterMap = null;

	private volatile List<ProducerServer> producerServers = null;

	private volatile List<ConsumerServerPair> consumerServerPairs = null;

	@Autowired
	private CmdbService cmdbService;

	private int interval = 120;// 秒

	private int delay = 5;

	private static ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils
			.getThreadFactory(FACTORY_NAME));

	private ScheduledFuture<?> future = null;

	private ConfigCache configCache;

	@PostConstruct
	public void startTask() {
		scheduleCmdbDataTask();
		initLionConfig();
	}

	private void initLionConfig() {
		try {
			configCache = ConfigCache.getInstance();
			masterPort = configCache.getIntProperty(CONSUMER_SERVER_MASTER_PORT_KEY);
			slavePort = configCache.getIntProperty(CONSUMER_SERVER_SLAVE_PORT_KEY);

			configCache.addChange(new ConfigChange() {
				@Override
				public void onChange(String key, String value) {
					if (key.equals(CONSUMER_SERVER_MASTER_PORT_KEY)) {
						masterPort = Integer.parseInt(value);
					} else if (key.equals(CONSUMER_SERVER_SLAVE_PORT_KEY)) {
						slavePort = Integer.parseInt(value);
					}
					logger.info("[initLionConfig] onChange key= {}, value= {}", key, value);
				}

			});
		} catch (LionException e) {
			logger.error("lion read producer and consumer server ips failed", e);
		}
	}

	private void scheduleCmdbDataTask() {
		setFuture(scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					doCmdbIpTask();
					logger.info("[scheduleCmdbDataTask] scheduled.");
				} catch (Throwable th) {
					logger.error("[scheduleCmdbDataTask]", th);
				} finally {

				}
			}

		}, getDelay(), getInterval(), TimeUnit.SECONDS));
	}

	private void doCmdbIpTask() {
		doCmdbProducerIpTask();
		doCmdbConsumerIpTask();
	}

	private void doCmdbProducerIpTask() {
		List<EnvDevice> producerEnvDevices = cmdbService.getEnvDevices(SWALLOW_PRODUCER_NAME);
		if (producerEnvDevices != null) {
			List<String> producerIps = new ArrayList<String>();
			Map<String, String> producerIpsMap = new HashMap<String, String>();
			List<ProducerServer> producerServers = new ArrayList<ProducerServer>();
			for (EnvDevice envDevice : producerEnvDevices) {
				producerIps.add(envDevice.getIp());
				producerIpsMap.put(envDevice.getHostName(), envDevice.getIp());
				producerServers.add(new ProducerServer(envDevice.getIp(), envDevice.getHostName()));
			}
			this.cmdbProducerMap = producerIpsMap;
			this.producerServers = producerServers;
			logger.info("[doCmdbIpTask]cmdbProducerMap= {}", cmdbProducerMap);
		}
	}

	private void doCmdbConsumerIpTask() {
		List<EnvDevice> consumerSlaveEnvDevices = cmdbService.getEnvDevices(SWALLOW_CONSUMER_SLAVE_NAME);
		List<EnvDevice> consumerMasterEnvDevices = cmdbService.getEnvDevices(SWALLOW_CONSUMER_MASTER_NAME);

		if (consumerMasterEnvDevices != null && consumerSlaveEnvDevices != null) {
			List<String> consumerMasterIps = new ArrayList<String>();
			Map<String, String> consumerMasterIpsMap = new HashMap<String, String>();
			List<String> consumerSlaveIps = new ArrayList<String>();
			Map<String, String> consumerSlaveIpsMap = new HashMap<String, String>();
			List<ConsumerServerPair> consumerServerPairs = new ArrayList<ConsumerServerPair>();
			for (EnvDevice envMasterDevice : consumerMasterEnvDevices) {
				for (EnvDevice envSlaveDevice : consumerSlaveEnvDevices) {
					String slaveName = StringUtils.replace(envMasterDevice.getHostName(), MASTER_NAME, SLAVE_NAME);
					if (slaveName.equals(envSlaveDevice.getHostName())) {
						consumerMasterIps.add(envMasterDevice.getIp());
						consumerSlaveIps.add(envSlaveDevice.getIp());

						consumerServerPairs.add(new ConsumerServerPair(new ConsumerServer(envMasterDevice.getIp(),
								envMasterDevice.getHostName(), masterPort), new ConsumerServer(envSlaveDevice.getIp(),
								envSlaveDevice.getHostName(), slavePort)));

						consumerMasterIpsMap.put(envMasterDevice.getHostName(), envMasterDevice.getIp());
						consumerSlaveIpsMap.put(envSlaveDevice.getHostName(), envSlaveDevice.getIp());
						break;
					}
				}
			}
			this.cmdbConsumerMasterMap = consumerMasterIpsMap;
			logger.info("[doCmdbConsumerIpTask] cmdbConsumerMasterMap= {}", cmdbConsumerMasterMap);

			this.cmdbConsumerSlaveMap = consumerSlaveIpsMap;

			logger.info("[doCmdbConsumerIpTask] cmdbConsumerSlaveMap= {}", cmdbConsumerSlaveMap);

			this.consumerServerPairs = consumerServerPairs;
		}
	}

	@Override
	public void addStatisIps(MonitorData monitorData) {
		if (monitorData instanceof ProducerMonitorData) {
			addStatisProducerIps((ProducerMonitorData) monitorData);
		} else if (monitorData instanceof ConsumerMonitorData) {
			addStatisConsumerIps((ConsumerMonitorData) monitorData);
		} else {
			throw new IllegalArgumentException("unsupported MonitorData type " + monitorData.getClass().getName());
		}
	}

	private void addStatisProducerIps(ProducerMonitorData producerMonitorData) {
		if (producerMonitorData == null) {
			return;
		}
		addSetData(statisIps, producerMonitorData.getSwallowServerIp());
		addMapData(statisProducerServerIps, producerMonitorData.getSwallowServerIp());
		ProducerServerData serverData = (ProducerServerData) producerMonitorData.getServerData();
		if (serverData == null) {
			return;
		}
		for (Map.Entry<String, ProducerTopicData> topicData : serverData.entrySet()) {
			if (topicData == null) {
				continue;
			}
			for (Map.Entry<String, MessageInfo> messageInfo : topicData.getValue().entrySet()) {
				if (messageInfo == null) {
					continue;
				}
				addSetData(statisIps, messageInfo.getKey());
			}
		}
	}

	private void addStatisConsumerIps(ConsumerMonitorData consumerMonitorData) {
		if (consumerMonitorData == null) {
			return;
		}
		addSetData(statisIps, consumerMonitorData.getSwallowServerIp());
		addMapData(statisConsumerServerIps, consumerMonitorData.getSwallowServerIp());
		ConsumerServerData serverData = (ConsumerServerData) consumerMonitorData.getServerData();
		if (serverData == null) {
			return;
		}
		for (Map.Entry<String, ConsumerTopicData> topicData : serverData.entrySet()) {
			if (topicData == null) {
				continue;
			}
			for (Map.Entry<String, ConsumerIdData> consumerIdData : topicData.getValue().entrySet()) {
				if (consumerIdData == null) {
					continue;
				}
				for (Map.Entry<String, MessageInfo> messageInfo : consumerIdData.getValue().getAckMessages().entrySet()) {
					if (messageInfo == null) {
						continue;
					}
					addSetData(statisIps, messageInfo.getKey());
				}
				for (Map.Entry<String, MessageInfo> messageInfo : consumerIdData.getValue().getSendMessages()
						.entrySet()) {
					if (messageInfo == null) {
						continue;
					}
					addSetData(statisIps, messageInfo.getKey());
				}
			}

		}
	}

	private void addSetData(Set<String> set, String data) {
		if (StringUtils.isNotBlank(data)) {
			set.add(data);
		}
	}

	private void addMapData(Map<String, Long> map, String data) {
		map.put(data, System.currentTimeMillis());
	}

	public long getLastestStatsTimeByIp(String ip, boolean isProducer) {
		if (isProducer) {
			if (statisProducerServerIps.containsKey(ip)) {
				return statisProducerServerIps.get(ip);
			}
		} else {
			if (statisConsumerServerIps.containsKey(ip)) {
				return statisConsumerServerIps.get(ip);
			}
		}
		return 0L;
	}

	@Override
	public Map<String, Long> getStatisConsumerServerIps() {
		return copyMap(statisConsumerServerIps);
	}

	@Override
	public Map<String, Long> getStatisProducerServerIps() {
		return copyMap(statisProducerServerIps);
	}

	private <T> Map<String, T> copyMap(Map<String, T> srcMap) {
		Map<String, T> destMap = new HashMap<String, T>();
		for (Entry<String, T> srcEntity : srcMap.entrySet()) {
			destMap.put(srcEntity.getKey(), srcEntity.getValue());
		}
		return destMap;
	}

	@Override
	public Map<String, String> getProducerServerIpsMap() {
		if (cmdbProducerMap != null) {
			return Collections.unmodifiableMap(cmdbProducerMap);
		}
		return null;
	}

	@Override
	public Map<String, String> getConsumerServerMasterIpsMap() {
		if (cmdbConsumerMasterMap != null) {
			return Collections.unmodifiableMap(cmdbConsumerMasterMap);
		}
		return null;
	}

	@Override
	public Map<String, String> getConsumerServerSlaveIpsMap() {
		if (cmdbConsumerSlaveMap != null) {
			return Collections.unmodifiableMap(cmdbConsumerSlaveMap);
		}
		return null;
	}

	@Override
	public List<ConsumerServerPair> getConsumerServerPairs() {
		if (consumerServerPairs != null) {
			return Collections.unmodifiableList(consumerServerPairs);
		}
		return null;
	}

	@Override
	public List<ProducerServer> getProducerServers() {
		if (producerServers != null) {
			return Collections.unmodifiableList(producerServers);
		}
		return null;
	}

	public int getInterval() {
		return interval;
	}

	public int getDelay() {
		return delay;
	}

	public ScheduledFuture<?> getFuture() {
		return future;
	}

	public void setFuture(ScheduledFuture<?> future) {
		this.future = future;
	}

}
