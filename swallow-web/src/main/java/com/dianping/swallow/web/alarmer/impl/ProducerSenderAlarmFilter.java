package com.dianping.swallow.web.alarmer.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;

/**
*
* @author qiyin
*
*/
public class ProducerSenderAlarmFilter extends AbstractProducerAlarmFilter {

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
					// alarm
					return false;
				}
			}
		}
		ipCollectorService.clearProducerServerIps();
		return true;
	}
}
