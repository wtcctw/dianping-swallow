package com.dianping.swallow.web.alarmer.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.IPCollectorService;

/**
*
* @author qiyin
*
*/
public class ConsumerSenderAlarmFilter extends AbstractConsumerAlarmFilter {

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
					// alarm
					return false;
				}
			}
		}
		ipCollectorService.clearConsumerServerIps();
		return true;
	}

}
