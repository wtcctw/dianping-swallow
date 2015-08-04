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
//@Service("consumerSenderAlarmFilter")
public class ConsumerSenderAlarmFilter extends AbstractServiceAlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerSenderAlarmFilter.class);

	@Autowired
	private MessageManager alarmManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	@Autowired
	private ConsumerPortAlarmFilter consumerPortAlarmFilter;

	private static final long SENDER_TIME_SPAN = 20 * 1000;

	@Override
	public boolean doAccept() {
		return checkSender();
	}

	public boolean checkSender() {
		List<String> consumerServerMasterIps = ipCollectorService.getConsumerServerMasterIps();
		List<String> consumerServerSlaveIps = ipCollectorService.getConsumerServerMasterIps();
		if (consumerServerMasterIps == null || consumerServerMasterIps.size() == 0) {
			logger.error("[checkSender] cannot find consumer server master ips.");
			return true;
		}

		Map<String, Long> statisConsumerServerIps = ipCollectorService.getStatisConsumerServerIps();
		List<String> whiteList = globalAlarmSettingService.getConsumerWhiteList();
		int index = 0;
		for (String serverIp : consumerServerMasterIps) {
			if (whiteList == null || !whiteList.contains(serverIp)) {
				if (!statisConsumerServerIps.containsKey(serverIp)
						|| System.currentTimeMillis() - statisConsumerServerIps.get(serverIp).longValue() > SENDER_TIME_SPAN) {
					String slaveIp = consumerServerSlaveIps.get(index);
					if (checkSlaveServerSender(statisConsumerServerIps, serverIp, slaveIp)) {
						ServerEvent serverEvent = new ServerEvent();
						serverEvent.setIp(serverIp);
						serverEvent.setSlaveIp(slaveIp);
						serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SENDER);
						serverEvent.setEventType(EventType.CONSUMER);
						serverEvent.setCreateTime(new Date());
						eventReporter.report(serverEvent);
						lastCheckStatus.put(serverIp, false);
					}
				} else {
					if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
						ServerEvent serverEvent = new ServerEvent();
						serverEvent.setIp(serverIp);
						serverEvent.setSlaveIp(serverIp);
						serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SENDER_OK);
						serverEvent.setEventType(EventType.CONSUMER);
						serverEvent.setCreateTime(new Date());
						eventReporter.report(serverEvent);
						lastCheckStatus.put(serverIp, true);
					}
				}
			}
			index++;
		}
		return true;
	}

	private boolean checkSlaveServerSender(Map<String, Long> statisIps, String masterIp, String slaveIp) {
		if (consumerPortAlarmFilter.isSlaveOpen(masterIp)) {
			if (!statisIps.containsKey(slaveIp)) {
				ServerEvent serverEvent = new ServerEvent();
				serverEvent.setIp(slaveIp);
				serverEvent.setSlaveIp(slaveIp);
				serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SENDER);
				serverEvent.setEventType(EventType.CONSUMER);
				serverEvent.setCreateTime(new Date());
				eventReporter.report(serverEvent);
				lastCheckStatus.put(slaveIp, false);
			} else {
				if (lastCheckStatus.containsKey(slaveIp) && !lastCheckStatus.get(slaveIp).booleanValue()) {
					ServerEvent serverEvent = new ServerEvent();
					serverEvent.setIp(slaveIp);
					serverEvent.setSlaveIp(slaveIp);
					serverEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SENDER_OK);
					serverEvent.setEventType(EventType.CONSUMER);
					serverEvent.setCreateTime(new Date());
					eventReporter.report(serverEvent);
					lastCheckStatus.put(slaveIp, true);
				}
			}
			return false;
		}
		return true;
	}
}
