package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;
import com.dianping.swallow.web.service.IPCollectorService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:35
 */
@Component
public class ConsumerSenderAlarmer extends AbstractServiceAlarmer {

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	@Autowired
	private ConsumerPortAlarmer consumerPortAlarmer;

	private static final long SENDER_TIME_SPAN = 20 * 1000;

	@Override
	public void doAlarm() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "doAlarm");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				checkSender();
			}
		});
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
						ServerEvent serverEvent = eventFactory.createServerEvent();
						serverEvent.setIp(serverIp).setSlaveIp(slaveIp).setServerType(ServerType.SERVER_SENDER)
								.setEventType(EventType.CONSUMER).setCreateTime(new Date());
						eventReporter.report(serverEvent);
						lastCheckStatus.put(serverIp, false);
					}
				} else {
					if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
						ServerEvent serverEvent = eventFactory.createServerEvent();
						serverEvent.setIp(serverIp).setSlaveIp(serverIp).setServerType(ServerType.SERVER_SENDER_OK)
								.setEventType(EventType.CONSUMER).setCreateTime(new Date());
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
		if (consumerPortAlarmer.isSlaveOpen(masterIp)) {
			if (!statisIps.containsKey(slaveIp)) {
				ServerEvent serverEvent = eventFactory.createServerEvent();
				serverEvent.setIp(slaveIp).setSlaveIp(slaveIp).setServerType(ServerType.SERVER_SENDER_OK)
						.setEventType(EventType.CONSUMER).setCreateTime(new Date());
				eventReporter.report(serverEvent);
				lastCheckStatus.put(slaveIp, false);
			} else {
				if (lastCheckStatus.containsKey(slaveIp) && !lastCheckStatus.get(slaveIp).booleanValue()) {
					ServerEvent serverEvent = eventFactory.createServerEvent();
					serverEvent.setIp(slaveIp).setSlaveIp(slaveIp).setServerType(ServerType.SERVER_SENDER_OK)
							.setEventType(EventType.CONSUMER).setCreateTime(new Date());
					eventReporter.report(serverEvent);
					lastCheckStatus.put(slaveIp, true);
				}
			}
			return false;
		}
		return true;
	}

}
