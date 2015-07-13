package com.dianping.swallow.web.service.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
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
import com.dianping.swallow.web.monitor.wapper.ConsumerDataWapper;
import com.dianping.swallow.web.monitor.wapper.ProducerDataWapper;
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

	private static final String TOTAL_KEY = "total";

	private static final String TOPIC_CONSUMERID_SPLIT = "&";

	private Set<String> ips = new ConcurrentSkipListSet<String>();

	private Set<String> consumerServerIps = new ConcurrentSkipListSet<String>();

	private Set<String> producerServerIps = new ConcurrentSkipListSet<String>();

	private volatile Map<String, String> cmdbProducers = new ConcurrentHashMap<String, String>();

	private volatile Map<String, String> cmdbConsumerSlaves = new ConcurrentHashMap<String, String>();

	private volatile Map<String, String> cmdbConsumerMasters = new ConcurrentHashMap<String, String>();

	private String producerServerIp;
	
	private String consumerServerIp;

	@Autowired
	private CmdbService cmdbService;

	@Autowired
	private ProducerDataWapper producerDataWapper;

	@Autowired
	private ConsumerDataWapper consumerDataWapper;

	private int interval = 120;// 秒

	private int delay = 5;

	private static ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

	private ScheduledFuture<?> future = null;

	@PostConstruct
	public void startTask() {
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
		Map<String, String> cmdbProducers = new ConcurrentHashMap<String, String>();
		Map<String, String> cmdbConsumerSlaves = new ConcurrentHashMap<String, String>();
		Map<String, String> cmdbConsumerMasters = new ConcurrentHashMap<String, String>();
		if (producerEnvDevices != null) {
			for (EnvDevice envDevice : producerEnvDevices) {
				cmdbProducers.put(envDevice.getHostName(), envDevice.getIp());
				setProducerServerIp(envDevice.getIp());
			}
		}
		if (consumerSlaveEnvDevices != null) {
			for (EnvDevice envDevice : consumerSlaveEnvDevices) {
				cmdbConsumerSlaves.put(envDevice.getHostName(), envDevice.getIp());
			}
		}
		if (consumerMasterEnvDevices != null) {
			for (EnvDevice envDevice : consumerMasterEnvDevices) {
				cmdbConsumerMasters.put(envDevice.getHostName(), envDevice.getIp());
				setConsumerServerIp(envDevice.getIp());
			}
		}
		this.cmdbProducers = cmdbProducers;
		this.cmdbConsumerMasters = cmdbConsumerMasters;
		this.cmdbConsumerSlaves = cmdbConsumerSlaves;
	}

	private void addSetData(Set<String> set, String data) {
		if (StringUtils.isNotBlank(data)) {
			set.add(data);
		}
	}

	@Override
	public void addIps(MonitorData monitorData) {
		if (monitorData instanceof ProducerMonitorData) {
			addProducerIps((ProducerMonitorData) monitorData);
		} else if (monitorData instanceof ConsumerMonitorData) {
			addConsumerIps((ConsumerMonitorData) monitorData);
		} else {
			return;
		}
	}

	private void addProducerIps(ProducerMonitorData producerMonitorData) {
		if (producerMonitorData == null) {
			return;
		}
		addSetData(ips, producerMonitorData.getSwallowServerIp());
		addProducerServerIps(producerMonitorData);
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
				addSetData(ips, messageInfo.getKey());
			}
		}
	}

	private void addConsumerIps(ConsumerMonitorData consumerMonitorData) {
		if (consumerMonitorData == null) {
			return;
		}
		addSetData(ips, consumerMonitorData.getSwallowServerIp());
		addConsumerServerIps(consumerMonitorData);
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
					addSetData(ips, messageInfo.getKey());
				}
				for (Map.Entry<String, MessageInfo> messageInfo : consumerIdData.getValue().getSendMessages()
						.entrySet()) {
					if (messageInfo == null) {
						continue;
					}
					addSetData(ips, messageInfo.getKey());
				}
			}

		}
	}

	@Override
	public void addProducerServerIps(ProducerMonitorData producerMonitorData) {
		if (producerMonitorData != null) {
			addSetData(producerServerIps, producerMonitorData.getSwallowServerIp());
		}
	}

	@Override
	public void addConsumerServerIps(ConsumerMonitorData consumerMonitorData) {
		if (consumerMonitorData != null) {
			addSetData(consumerServerIps, consumerMonitorData.getSwallowServerIp());
		}
	}

	@Override
	public Set<String> getConsumerServerIps() {
		return Collections.unmodifiableSet(consumerServerIps);
	}

	@Override
	public Set<String> getProducerServerIps() {
		return Collections.unmodifiableSet(consumerServerIps);
	}

	@Override
	public Set<String> getIps() {
		return Collections.unmodifiableSet(ips);
	}

	@Override
	public Map<String, String> getCmdbProducers() {
		return Collections.unmodifiableMap(cmdbProducers);
	}

	@Override
	public Map<String, String> getCmdbConsumerSlaves() {
		return Collections.unmodifiableMap(cmdbConsumerSlaves);
	}

	@Override
	public Map<String, String> getCmdbConsumerMasters() {
		return Collections.unmodifiableMap(cmdbConsumerMasters);
	}

	@Override
	public Set<String> getTopicConsumerIdIps(String topicName, String consumerId) {
		Set<String> consumerIdIps = consumerDataWapper.getConsumerIdIps(topicName, consumerId);
		if (consumerIdIps != null) {
			consumerIdIps.remove(TOTAL_KEY);
		}
		return consumerIdIps;
	}

	@Override
	public Set<String> getProducerTopicIps(String topicName) {
		Set<String> topicIps = producerDataWapper.getTopicIps(topicName);
		if (topicIps != null) {
			topicIps.remove(TOTAL_KEY);
		}
		return topicIps;
	}

	@Override
	public String getTopicConsumerIdKey(String topic, String consumerId) {
		return topic + TOPIC_CONSUMERID_SPLIT + consumerId;
	}

	@Override
	public void clearProducerServerIps() {
		producerServerIps.clear();
	}

	@Override
	public void clearConsumerServerIps() {
		consumerServerIps.clear();
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

	@Override
	public String getProducerServerIp() {
		return producerServerIp;
	}

	public void setProducerServerIp(String producerServerIp) {
		this.producerServerIp = producerServerIp;
	}
	
	@Override
	public String getConsumerServerIp() {
		return consumerServerIp;
	}

	public void setConsumerServerIp(String consumerServerIp) {
		this.consumerServerIp = consumerServerIp;
	}

}
