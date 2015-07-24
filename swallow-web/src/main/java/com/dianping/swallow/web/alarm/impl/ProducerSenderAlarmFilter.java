package com.dianping.swallow.web.alarm.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
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
	private GlobalAlarmSettingService globalAlarmSettingService;

	@Override
	public boolean doAccept() {
		return checkSender();
	}

	public boolean checkSender() {
		List<String> producerServerIps = ipCollectorService.getProducerServerIps();
		Map<String, Long> statisProducerServerIps = ipCollectorService.getStatisProducerServerIps();
		List<String> whiteList = globalAlarmSettingService.getProducerWhiteList();
		for (String serverIp : producerServerIps) {
			if (whiteList == null || !whiteList.contains(serverIp)) {
				if (!statisProducerServerIps.containsKey(serverIp)) {
					alarmManager.producerServerAlarm(serverIp, AlarmType.PRODUCER_SERVER_SENDER);
					lastCheckStatus.put(serverIp, false);
				} else if (System.currentTimeMillis() - statisProducerServerIps.get(serverIp).longValue() > 30) {
					alarmManager.producerServerAlarm(serverIp, AlarmType.PRODUCER_SERVER_SENDER);
					lastCheckStatus.put(serverIp, false);
				} else if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
					alarmManager.producerServerAlarm(serverIp, AlarmType.PRODUCER_SERVER_SENDER_OK);
					lastCheckStatus.put(serverIp, true);
				}
			}
		}
		return true;
	}
}
