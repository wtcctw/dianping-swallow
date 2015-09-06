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
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.IpCollectorService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:07:00
 */
@Component
public class ProducerSenderAlarmer extends AbstractServiceAlarmer {

	@Autowired
	private IpCollectorService ipCollectorService;

	@Autowired
	private AlarmResourceContainer resourceContainer;

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
		if (producerServerIps == null) {
			logger.error("[checkSender] cannot find producerserver ips.");
			return false;
		}
		for (String serverIp : producerServerIps) {
			ProducerServerResource pServerResource = resourceContainer.findProducerServerResource(serverIp);
			if (pServerResource == null || !pServerResource.isAlarm()) {
				continue;
			}
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
		return true;
	}
}
