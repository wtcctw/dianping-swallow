package com.dianping.swallow.web.alarm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;
import com.dianping.swallow.web.util.NetUtil;

/**
 *
 * @author qiyin
 *
 */
//@Service("consumerPortAlarmFilter")
public class ConsumerPortAlarmFilter extends AbstractServiceAlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerPortAlarmFilter.class);

	private static final String CONSUMER_SERVER_MASTER_PORT_KEY = "swallow.consumer.server.master.port";

	private static final String CONSUMER_SERVER_SLAVE_PORT_KEY = "swallow.consumer.server.slave.port";

	private volatile int masterPort = 8081;

	private volatile int slavePort = 8082;

	private Map<String, Boolean> isSlaveIps = new ConcurrentHashMap<String, Boolean>();

	private static final String KEY_SPLIT = "&";

	@Autowired
	private MessageManager alarmManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	private ConfigCache configCache;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	@PostConstruct
	public void initialize() {
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
				}

			});
		} catch (LionException e) {
			logger.error("lion read consumer master slave port failed", e);
		}

	}

	@Override
	public boolean doAccept() {
		return checkPort();
	}

	public boolean checkPort() {
		List<String> consumerServerMasterIps = ipCollectorService.getConsumerServerMasterIps();
		List<String> consumerServerSlaveIps = ipCollectorService.getConsumerServerSlaveIps();
		if (consumerServerMasterIps == null || consumerServerMasterIps.size() == 0 || consumerServerSlaveIps == null
				|| consumerServerSlaveIps.size() == 0) {
			logger.error("[checkPort] cannot find consumermaster or consumerslave ips.");
			return false;
		}
		List<String> whiteList = globalAlarmSettingService.getConsumerWhiteList();
		int index = 0;

		for (String masterIp : consumerServerMasterIps) {
			if (whiteList == null || !whiteList.contains(masterIp)) {
				alarmPort(masterIp, consumerServerSlaveIps.get(index));
			}
			index++;
		}
		return true;
	}

	private boolean alarmPort(String masterIp, String slaveIp) {
		boolean usingMaster = NetUtil.isPortOpen(masterIp, masterPort);
		if (!usingMaster) {
			usingMaster = NetUtil.isPortOpen(masterIp, masterPort);
		}
		boolean usingSlave = NetUtil.isPortOpen(slaveIp, slavePort);
		if (!usingSlave) {
			usingSlave = NetUtil.isPortOpen(masterIp, slavePort);
		}
		String key = masterIp + KEY_SPLIT + slaveIp;
		if (!usingMaster && usingSlave) {
			isSlaveIps.put(masterIp, true);
			ServerEvent serverEvent = new ServerEvent();
			serverEvent.setIp(masterIp);
			serverEvent.setSlaveIp(slaveIp);
			serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SLAVEPORT_OPENED);
			serverEvent.setEventType(EventType.CONSUMER);
			serverEvent.setCreateTime(new Date());
			eventReporter.report(serverEvent);
			lastCheckStatus.put(key, false);
			return false;
		} else if (usingMaster && usingSlave) {
			isSlaveIps.put(masterIp, false);
			ServerEvent serverEvent = new ServerEvent();
			serverEvent.setIp(masterIp);
			serverEvent.setSlaveIp(slaveIp);
			serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_BOTHPORT_OPENED);
			serverEvent.setEventType(EventType.CONSUMER);
			serverEvent.setCreateTime(new Date());
			eventReporter.report(serverEvent);
			lastCheckStatus.put(key, false);
			return false;
		} else if (!usingMaster && !usingSlave) {
			isSlaveIps.put(masterIp, false);
			ServerEvent serverEvent = new ServerEvent();
			serverEvent.setIp(masterIp);
			serverEvent.setSlaveIp(slaveIp);
			serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_BOTHPORT_UNOPENED);
			serverEvent.setEventType(EventType.CONSUMER);
			serverEvent.setCreateTime(new Date());
			lastCheckStatus.put(key, false);
			eventReporter.report(serverEvent);
			return false;
		} else {
			isSlaveIps.put(masterIp, false);
			if (lastCheckStatus.containsKey(key) && !lastCheckStatus.get(key).booleanValue()) {
				ServerEvent serverEvent = new ServerEvent();
				serverEvent.setIp(masterIp);
				serverEvent.setSlaveIp(slaveIp);
				serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_PORT_OPENED_OK);
				serverEvent.setEventType(EventType.CONSUMER);
				serverEvent.setCreateTime(new Date());
				eventReporter.report(serverEvent);
				lastCheckStatus.put(key, true);
			}
		}
		return true;
	}

	public int getMasterPort() {
		return masterPort;
	}

	public int getSlavePort() {
		return slavePort;
	}

	public boolean isSlaveOpen(String ip) {
		if (isSlaveIps.containsKey(ip)) {
			return isSlaveIps.get(ip);
		}
		return false;
	}
}
