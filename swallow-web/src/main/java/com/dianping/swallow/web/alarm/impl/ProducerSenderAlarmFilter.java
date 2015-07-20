package com.dianping.swallow.web.alarm.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;

/**
*
* @author qiyin
*
*/
@Service("producerSenderAlarmFilter")
public class ProducerSenderAlarmFilter extends AbstractServiceAlarmFilter {

	@Autowired
	private AlarmManager alarmManager;
	
	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService swallowAlarmSettingService;

	@Override
	public boolean doAccept() {
		return checkSender();
	}

	public boolean checkSender() {
		List<String> producerServerIps = ipCollectorService.getProducerServerIps();
		Set<String> statisProducerServerIps = ipCollectorService.getStatisProducerServerIps();
		List<String> whiteList = swallowAlarmSettingService.getProducerWhiteList();
		for (String serverIp : producerServerIps) {
			if (whiteList == null || !whiteList.contains(serverIp)) {
				if (!statisProducerServerIps.contains(serverIp)) {
					alarmManager.producerSenderAlarm(serverIp);
				}
			}
		}
		ipCollectorService.clearStatisProducerServerIps();
		return true;
	}
}
