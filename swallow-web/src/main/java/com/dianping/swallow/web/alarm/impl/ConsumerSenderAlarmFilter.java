package com.dianping.swallow.web.alarm.impl;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.SwallowAlarmSettingService;
import com.dianping.swallow.web.util.NetUtil;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerSenderAlarmFilter")
public class ConsumerSenderAlarmFilter extends AbstractServiceAlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerSenderAlarmFilter.class);

	@Autowired
	private AlarmManager alarmManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private SwallowAlarmSettingService swallowAlarmSettingService;

	@Autowired
	private ConsumerPortAlarmFilter consumerPortAlarmFilter;

	@Override
	public boolean doAccept() {
		return checkSender();
	}

	public boolean checkSender() {
		List<String> consumerServerMasterIps = ipCollectorService.getConsumerServerMasterIps();
		List<String> consumerServerSlaveIps = ipCollectorService.getConsumerServerMasterIps();
		if (consumerServerMasterIps == null || consumerServerMasterIps.size() == 0) {
			logger.error("[checkSender] cannot find consumer server master ips.");
			return true;
		}

		Set<String> statisConsumerServerIps = ipCollectorService.getStatisConsumerServerIps();
		List<String> whiteList = swallowAlarmSettingService.getConsumerWhiteList();
		int index = 0;
		for (String serverIp : consumerServerMasterIps) {
			if (whiteList == null || !whiteList.contains(serverIp)) {
				if (!statisConsumerServerIps.contains(serverIp)) {
					String slaveIp = consumerServerSlaveIps.get(index);
					if (checkSlaveServerSender(statisConsumerServerIps, serverIp, slaveIp)) {
						alarmManager.consumerSenderAlarm(serverIp);
					}
				}
			}
			index++;
		}
		ipCollectorService.clearStatisConsumerServerIps();
		return true;
	}

	private boolean checkSlaveServerSender(Set<String> statisIps, String masterIp, String slaveIp) {
		if (!NetUtil.isPortOpen(masterIp, consumerPortAlarmFilter.getMasterPort())) {
			if (NetUtil.isPortOpen(slaveIp, consumerPortAlarmFilter.getSlavePort())) {
				if (!statisIps.contains(slaveIp)) {
					alarmManager.consumerSenderAlarm(slaveIp);
				}
				return false;
			}
			return true;
		}
		return true;
	}
}
