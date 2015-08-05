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
 *         2015年8月3日 下午6:07:00
 */
@Component
public class ProducerSenderAlarmer extends AbstractServiceAlarmer {

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

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
		List<String> producerServerIps = ipCollectorService.getProducerServerIps();
		Map<String, Long> statisProducerServerIps = ipCollectorService.getStatisProducerServerIps();
		List<String> whiteList = globalAlarmSettingService.getProducerWhiteList();
		for (String serverIp : producerServerIps) {
			if (whiteList == null || !whiteList.contains(serverIp)) {
				if (!statisProducerServerIps.containsKey(serverIp)
						|| System.currentTimeMillis() - statisProducerServerIps.get(serverIp).longValue() > SENDER_TIME_SPAN) {
					if (logger.isInfoEnabled()) {
						logger.info("serverIp : {}", serverIp);
					}
					ServerEvent serverEvent = eventFactory.createServerEvent();
					serverEvent.setIp(serverIp).setSlaveIp(serverIp).setServerType(ServerType.SERVER_SENDER)
							.setEventType(EventType.PRODUCER).setCreateTime(new Date());
					eventReporter.report(serverEvent);
					lastCheckStatus.put(serverIp, false);
				} else if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
					ServerEvent serverEvent = eventFactory.createServerEvent();
					serverEvent.setIp(serverIp).setSlaveIp(serverIp).setServerType(ServerType.SERVER_SENDER_OK)
							.setEventType(EventType.PRODUCER).setCreateTime(new Date());
					eventReporter.report(serverEvent);
					lastCheckStatus.put(serverIp, true);
				}
			}
		}
		return true;
	}
}
