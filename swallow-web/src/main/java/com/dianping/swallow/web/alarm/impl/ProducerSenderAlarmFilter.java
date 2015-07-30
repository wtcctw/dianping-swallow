package com.dianping.swallow.web.alarm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */

@Service("producerSenderAlarmFilter")
public class ProducerSenderAlarmFilter extends AbstractServiceAlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(ProducerSenderAlarmFilter.class);

	@Autowired
	private MessageManager alarmManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	private static final long SENDER_TIME_SPAN = 20 * 1000;

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
				if (!statisProducerServerIps.containsKey(serverIp)
						|| System.currentTimeMillis() - statisProducerServerIps.get(serverIp).longValue() > SENDER_TIME_SPAN) {
					if (logger.isInfoEnabled()) {
						logger.info("serverIp :" + serverIp + "  currentTime:" + System.currentTimeMillis()
								+ "  lastUpdateTime:" + statisProducerServerIps.get(serverIp).longValue());
					}
					ServerEvent event = new ServerEvent();
					event.setIp(serverIp);
					event.setSlaveIp(serverIp);
					event.setAlarmType(AlarmType.PRODUCER_SERVER_SENDER);
					event.setEventType(EventType.PRODUCER);
					event.setCreateTime(new Date());
					eventReporter.report(event);
					lastCheckStatus.put(serverIp, false);
				} else if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
					ServerEvent event = new ServerEvent();
					event.setIp(serverIp);
					event.setSlaveIp(serverIp);
					event.setAlarmType(AlarmType.PRODUCER_SERVER_SENDER_OK);
					event.setEventType(EventType.PRODUCER);
					event.setCreateTime(new Date());
					eventReporter.report(event);
					lastCheckStatus.put(serverIp, true);
				}
			}
		}
		return true;
	}
}
