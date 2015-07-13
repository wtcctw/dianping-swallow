package com.dianping.swallow.web.alarm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;

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
	private ProducerServerAlarmSettingService serverAlarmSettingService;

	@Override
	public boolean doAccept() {
		return checkSender();
	}

	public boolean checkSender() {
		Map<String, String> cmdbProducers = ipCollectorService.getCmdbProducers();
		Set<String> serverIps = ipCollectorService.getProducerServerIps();
		List<String> whiteList = serverAlarmSettingService.getWhiteList();
		for (Map.Entry<String, String> cmdbProducer : cmdbProducers.entrySet()) {
			String ip = cmdbProducer.getValue();
			if (whiteList == null || !whiteList.contains(ip)) {
				if (!serverIps.contains(ip)) {
					alarmManager.producerSenderAlarm(ip);
					return false;
				}
			}
		}
		ipCollectorService.clearProducerServerIps();
		return true;
	}
}
