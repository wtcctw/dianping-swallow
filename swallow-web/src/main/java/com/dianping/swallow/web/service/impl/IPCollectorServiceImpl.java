package com.dianping.swallow.web.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.CmdbService;
import com.dianping.swallow.web.service.IPCollectorService;

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

	private static final String PRODUCER_SERVER_IPLIST_KEY = "swallow.producer.server.iplist";

	private static final String CONSUMER_SERVER_MASTER_IPLIST_KEY = "swallow.consumer.server.master.iplist";

	private static final String CONSUMER_SERVER_SLAVE_IPLIST_KEY = "swallow.consumer.server.slave.iplist";

	private static final String TOTAL_KEY = "total";

	private static final String TOPIC_CONSUMERID_SPLIT = "&";

	private static final String IP_SPLIT = ",";

	private static final String MASTER_NAME = "master";

	private static final String SLAVE_NAME = "slave";

	private Set<String> statisIps = new ConcurrentSkipListSet<String>();

	private Map<String, Long> statisConsumerServerIps = new ConcurrentHashMap<String, Long>();

	private Map<String, Long> statisProducerServerIps = new ConcurrentHashMap<String, Long>();

	private volatile List<String> cmdbProducerIps = null;

	private volatile List<String> cmdbConsumerSlaveIps = null;

	private volatile List<String> cmdbConsumerMasterIps = null;

	private volatile List<String> lionProducerIps = null;

	private volatile List<String> lionConsumerSlaveIps = null;

	private volatile List<String> lionConsumerMasterIps = null;

	private volatile Map<String, String> cmdbProducerMap = null;

	private volatile Map<String, String> cmdbConsumerSlaveMap = null;

	private volatile Map<String, String> cmdbConsumerMasterMap = null;

	@Autowired
	private CmdbService cmdbService;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	private int interval = 120;// 秒

	private int delay = 5;

	private static ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

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
			String strProducerServerIp = configCache.getProperty(PRODUCER_SERVER_IPLIST_KEY);
			String strConsumerMasterServerIp = configCache.getProperty(CONSUMER_SERVER_MASTER_IPLIST_KEY);
			String strConsumerSlaveServerIp = configCache.getProperty(CONSUMER_SERVER_SLAVE_IPLIST_KEY);
			lionProducerIps = convertToList(strProducerServerIp);
			lionConsumerMasterIps = convertToList(strConsumerMasterServerIp);
			lionConsumerSlaveIps = convertToList(strConsumerSlaveServerIp);
			configCache.addChange(new ConfigChange() {
				@Override
				public void onChange(String key, String value) {
					if (key.equals(PRODUCER_SERVER_IPLIST_KEY)) {
						lionProducerIps = convertToList(value);
					} else if (key.equals(CONSUMER_SERVER_MASTER_IPLIST_KEY)) {
						lionConsumerMasterIps = convertToList(value);
					} else if (key.equals(CONSUMER_SERVER_SLAVE_IPLIST_KEY)) {
						lionConsumerSlaveIps = convertToList(value);
					}
				}

			});
		} catch (LionException e) {
			logger.error("lion read producer and consumer server ips failed", e);
		}
	}

	private List<String> convertToList(String strValue) {
		if (StringUtils.isBlank(strValue)) {
			return null;
		}
		String[] strArr = strValue.split(IP_SPLIT);
		if (strArr != null) {
			List<String> tempList = new ArrayList<String>();
			for (String strTemp : strArr) {
				if (StringUtils.isNotBlank(strTemp)) {
					tempList.add(strTemp);
				}
			}
			return tempList;
		}
		return null;
	}

	private void scheduleCmdbDataTask() {
		setFuture(scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					doTask();
				} catch (Throwable th) {
					logger.error("[startTask]", th);
				} finally {

				}
			}

		}, getDelay(), getInterval(), TimeUnit.SECONDS));
	}

	private void doTask() {
		List<EnvDevice> producerEnvDevices = cmdbService.getEnvDevices(SWALLOW_PRODUCER_NAME);
		List<EnvDevice> consumerSlaveEnvDevices = cmdbService.getEnvDevices(SWALLOW_CONSUMER_SLAVE_NAME);
		List<EnvDevice> consumerMasterEnvDevices = cmdbService.getEnvDevices(SWALLOW_CONSUMER_MASTER_NAME);
		if (producerEnvDevices != null) {
			List<String> producerIps = new ArrayList<String>();
			Map<String, String> producerIpsMap = new HashMap<String, String>();
			for (EnvDevice envDevice : producerEnvDevices) {
				producerIps.add(envDevice.getIp());
				producerIpsMap.put(envDevice.getHostName(), envDevice.getIp());
			}
			this.cmdbProducerIps = producerIps;
			this.cmdbProducerMap = producerIpsMap;
		}
		if (consumerMasterEnvDevices != null && consumerSlaveEnvDevices != null) {
			List<String> consumerMasterIps = new ArrayList<String>();
			Map<String, String> consumerMasterIpsMap = new HashMap<String, String>();
			List<String> consumerSlaveIps = new ArrayList<String>();
			Map<String, String> consumerSlaveIpsMap = new HashMap<String, String>();
			for (EnvDevice envMasterDevice : consumerMasterEnvDevices) {
				for (EnvDevice envSlaveDevice : consumerSlaveEnvDevices) {
					String slaveName = StringUtils.replace(envMasterDevice.getHostName(), MASTER_NAME, SLAVE_NAME);
					if (slaveName.equals(envSlaveDevice.getHostName())) {
						consumerMasterIps.add(envMasterDevice.getIp());
						consumerSlaveIps.add(envSlaveDevice.getIp());
						consumerMasterIpsMap.put(envMasterDevice.getHostName(), envMasterDevice.getIp());
						consumerSlaveIpsMap.put(envSlaveDevice.getHostName(), envSlaveDevice.getIp());
						this.cmdbConsumerMasterIps = consumerMasterIps;
						this.cmdbConsumerSlaveIps = consumerSlaveIps;
						this.cmdbConsumerMasterMap = consumerMasterIpsMap;
						this.cmdbConsumerSlaveMap = consumerSlaveIpsMap;
					}
				}
			}
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

	@Override
	public Map<String, Long> getStatisConsumerServerIps() {
		return copyMap(statisConsumerServerIps);
	}

	@Override
	public Map<String, Long> getStatisProducerServerIps() {
		return copyMap(statisProducerServerIps);
	}

	@Override
	public Set<String> getStatisIps() {
		return copySet(statisIps);
	}

	private <T> Map<String, T> copyMap(Map<String, T> srcMap) {
		Map<String, T> destMap = new HashMap<String, T>();
		for (Entry<String, T> srcEntity : srcMap.entrySet()) {
			destMap.put(srcEntity.getKey(), srcEntity.getValue());
		}
		return destMap;
	}

	private Set<String> copySet(Set<String> srcSet) {
		Set<String> destSet = new HashSet<String>();
		Iterator<String> iterator = srcSet.iterator();
		while (iterator.hasNext()) {
			String entity = iterator.next();
			destSet.add(entity);
		}
		return destSet;
	}

	@Override
	public List<String> getProducerServerIps() {
		if (cmdbProducerIps != null && !cmdbProducerIps.isEmpty()) {
			return Collections.unmodifiableList(cmdbProducerIps);
		}
		return Collections.unmodifiableList(lionProducerIps);
	}

	@Override
	public List<String> getConsumerServerSlaveIps() {
		if (cmdbConsumerSlaveIps != null && !cmdbConsumerSlaveIps.isEmpty()) {
			return Collections.unmodifiableList(cmdbConsumerSlaveIps);
		}
		return Collections.unmodifiableList(lionConsumerSlaveIps);
	}

	@Override
	public List<String> getConsumerServerMasterIps() {
		if (cmdbConsumerMasterIps != null && !cmdbConsumerMasterIps.isEmpty()) {
			return Collections.unmodifiableList(cmdbConsumerMasterIps);
		}
		return Collections.unmodifiableList(lionConsumerMasterIps);
	}

	@Override
	public Map<String, String> getProducerServerIpsMap() {
		return Collections.unmodifiableMap(cmdbProducerMap);
	}

	@Override
	public Map<String, String> getConsumerServerMasterIpsMap() {
		return Collections.unmodifiableMap(cmdbConsumerMasterMap);
	}

	@Override
	public Map<String, String> getConsumerServerSlaveIpsMap() {
		return Collections.unmodifiableMap(cmdbConsumerSlaveMap);
	}

	@Override
	public Set<String> getTopicConsumerIdIps(String topicName, String consumerId) {
		Set<String> consumerIdIps = consumerStatsDataWapper.getConsumerIdIps(topicName, consumerId);
		if (consumerIdIps != null) {
			consumerIdIps.remove(TOTAL_KEY);
		}
		return consumerIdIps;
	}

	@Override
	public Set<String> getProducerTopicIps(String topicName) {
		Set<String> topicIps = producerStatsDataWapper.getTopicIps(topicName);
		if (topicIps != null) {
			topicIps.remove(TOTAL_KEY);
		}
		return topicIps;
	}

	@Override
	public Set<String> getConsumerTopicIps(String topicName) {
		Set<String> topicIps = consumerStatsDataWapper.getTopicIps(topicName);
		if (topicIps != null) {
			topicIps.remove(TOTAL_KEY);
		}
		return topicIps;
	}

	@Override
	public String getTopicConsumerIdKey(String topic, String consumerId) {
		return topic + TOPIC_CONSUMERID_SPLIT + consumerId;
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
