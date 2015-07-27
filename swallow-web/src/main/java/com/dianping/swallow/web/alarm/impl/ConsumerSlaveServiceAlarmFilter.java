package com.dianping.swallow.web.alarm.impl;

import java.util.Date;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
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

	private static final String SLAVE_MONITOR_URL = "http://{ip}:8080/names";

	private static final String MONOGO_MONITOR_SIGN = "mongoManager";

	@Autowired
	private MessageManager alarmManager;

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
						ServerEvent serverEvent = new ServerEvent();
						serverEvent.setIp(slaveIp);
						serverEvent.setSlaveIp(slaveIp);
						serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SLAVESERVICE_STARTED);
						serverEvent.setEventType(EventType.CONSUMER);
						serverEvent.setCreateTime(new Date());
						eventReporter.report(serverEvent);
						lastCheckStatus.put(slaveIp, false);
					} else {
						if (lastCheckStatus.containsKey(slaveIp) && !lastCheckStatus.get(slaveIp).booleanValue()) {
							ServerEvent serverEvent = new ServerEvent();
							serverEvent.setIp(slaveIp);
							serverEvent.setSlaveIp(slaveIp);
							serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SLAVESERVICE_STARTED_OK);
							serverEvent.setEventType(EventType.CONSUMER);
							serverEvent.setCreateTime(new Date());
							eventReporter.report(serverEvent);
							lastCheckStatus.put(slaveIp, true);
						}
					}
				}
			}
		return true;
	}
}
