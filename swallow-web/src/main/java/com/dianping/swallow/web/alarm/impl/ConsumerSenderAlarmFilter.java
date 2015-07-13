package com.dianping.swallow.web.alarm.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.IPCollectorService;

/**
*
* @author qiyin
*
*/
@Service("consumerSenderAlarmFilter")
public class ConsumerSenderAlarmFilter extends AbstractServiceAlarmFilter {

	@Autowired
	private AlarmManager alarmManager;
	
	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private ConsumerServerAlarmSettingService serverAlarmSettingService;

	@Override
	public boolean doAccept() {
		return checkSender();
	}

	public boolean checkSender() {
		Map<String, String> cmdbmdbConsumerMasters = ipCollectorService.getCmdbConsumerMasters();
		Set<String> serverIps = ipCollectorService.getConsumerServerIps();
		List<String> whiteList = serverAlarmSettingService.getWhiteList();
		for (Map.Entry<String, String> cmdbConsumer : cmdbmdbConsumerMasters.entrySet()) {
			String ip = cmdbConsumer.getValue();
			if (whiteList == null || !whiteList.contains(ip)) {
				if (!serverIps.contains(ip)) {
					alarmManager.consumerSenderAlarm(ip);
					return false;
				}
			}
		}
		ipCollectorService.clearConsumerServerIps();
		return true;
	}

}
