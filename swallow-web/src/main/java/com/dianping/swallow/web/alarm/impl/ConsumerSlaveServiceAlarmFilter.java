package com.dianping.swallow.web.alarm.impl;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.service.IPCollectorService;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerSlaveServiceAlarmFilter")
public class ConsumerSlaveServiceAlarmFilter extends AbstractServiceAlarmFilter {

	private static final String SLAVE_MONITOR_URL = "http://{ip}:8089/names";

	private static final String MONOGO_MONITOR_SIGN = "mongoManager";

	@Autowired
	private AlarmManager alarmManager;

	@Autowired
	private HttpService httpSerivice;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	@Override
	public boolean doAccept() {
		return checkSlaveService();
	}

	private boolean checkSlaveService() {
		List<String> whiteList = globalAlarmSettingService.getConsumerWhiteList();
		List<String> consumerServerSlaveIps = ipCollectorService.getConsumerServerSlaveIps();
		if (consumerServerSlaveIps != null)
			for (String slaveIp : consumerServerSlaveIps) {
				
				if (whiteList == null || !whiteList.contains(slaveIp)) {
					String url = StringUtils.replace(SLAVE_MONITOR_URL, "{ip}", slaveIp);
					HttpResult result = httpSerivice.httpGet(url);
					if (!result.isSuccess()) {
						result = httpSerivice.httpGet(url);
					}
					
					if (!result.isSuccess() || !result.getResponseBody().contains(MONOGO_MONITOR_SIGN)) {
						alarmManager.consumerServerAlarm(slaveIp, slaveIp,
								AlarmType.CONSUMER_SERVER_SLAVESERVICE_STARTED);
						lastCheckStatus.put(slaveIp, false);
					} else {
						if (lastCheckStatus.containsKey(slaveIp) && !lastCheckStatus.get(slaveIp).booleanValue()) {
							alarmManager.consumerServerAlarm(slaveIp, slaveIp,
									AlarmType.CONSUMER_SERVER_SLAVESERVICE_STARTED_OK);
							lastCheckStatus.put(slaveIp, true);
						}
					}
					
				}
			}
		return true;
	}
}
