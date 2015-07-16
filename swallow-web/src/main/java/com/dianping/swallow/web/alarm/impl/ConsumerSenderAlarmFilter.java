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

	@Override
	public boolean doAccept() {
		return checkSender();
	}

	public boolean checkSender() {
		List<String> consumerServerMasterIps = ipCollectorService.getConsumerServerMasterIps();
		if (consumerServerMasterIps == null || consumerServerMasterIps.size() == 0) {
			logger.error("[checkSender] cannot find consumer server master ips.");
			return true;
		}

		Set<String> statisConsumerServerIps = ipCollectorService.getStatisConsumerServerIps();
		List<String> whiteList = swallowAlarmSettingService.getConsumerWhiteList();
		for (String serverIp : consumerServerMasterIps) {
			if (whiteList == null || !whiteList.contains(serverIp)) {
				if (!statisConsumerServerIps.contains(serverIp)) {
					alarmManager.consumerSenderAlarm(serverIp);
				}
			}
		}
		ipCollectorService.clearConsumerServerIps();
		return true;
	}
	
}
