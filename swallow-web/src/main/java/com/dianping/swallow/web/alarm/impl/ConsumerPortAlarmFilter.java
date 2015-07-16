package com.dianping.swallow.web.alarm.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.SwallowAlarmSettingService;
import com.dianping.swallow.web.util.NetUtil;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerPortAlarmFilter")
public class ConsumerPortAlarmFilter extends AbstractServiceAlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerPortAlarmFilter.class);

	private static final String CONSUMER_SERVER_MASTER_PORT_KEY = "swallow.consumer.server.master.port";

	private static final String CONSUMER_SERVER_SLAVE_PORT_KEY = "swallow.consumer.server.slave.port";

	private volatile int masterPort = 8081;

	private volatile int slavePort = 8082;

	@Autowired
	private AlarmManager alarmManager;

	@Autowired
	private IPDescManager ipDescManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	private ConfigCache configCache;

	@Autowired
	private SwallowAlarmSettingService swallowAlarmSettingService;

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
			return true;
		}
		if (consumerServerMasterIps.size() != consumerServerSlaveIps.size()) {
			logger.error("[checkPort] consumermaster ips is not corresponding with consumerslave ips.");
			return true;
		}
		List<String> whiteList = swallowAlarmSettingService.getConsumerWhiteList();
		int index = 0;

		for (String masterIp : consumerServerMasterIps) {
			if (whiteList == null || !whiteList.contains(masterIp)) {
				alarmPort(masterIp, consumerServerSlaveIps.get(index++));
			}
		}
		return true;
	}

	private boolean alarmPort(String masterIp, String slaveIp) {
		boolean usingMaster = NetUtil.isPortOpen(masterIp, masterPort);
		boolean usingSlave = NetUtil.isPortOpen(slaveIp, slavePort);
		if (!usingMaster && usingSlave) {
			alarmManager.consumerPortAlarm(masterIp, slaveIp, false);
			return false;
		} else if (usingMaster && usingSlave) {
			alarmManager.consumerPortAlarm(masterIp, slaveIp, true);
			return false;
		}
		return true;
	}

}
