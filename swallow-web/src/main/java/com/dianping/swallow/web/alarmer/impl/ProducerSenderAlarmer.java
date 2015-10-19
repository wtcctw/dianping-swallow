package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.IPCollectorService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:07:00
 */
//@Component
public class ProducerSenderAlarmer extends AbstractServiceAlarmer {

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private ResourceContainer resourceContainer;

	private static final long SENDER_TIME_SPAN = 20 * 1000;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		alarmInterval = 30;
		alarmDelay = 30;
	}

	@Override
	public void doAlarm() {
		checkSender();
	}

	public boolean checkSender() {
		List<ProducerServerResource> pServerResources = resourceContainer.findProducerServerResources(false);
		Map<String, Long> statisProducerServerIps = ipCollectorService.getStatisProducerServerIps();
		if (pServerResources == null) {
			logger.error("[checkSender] cannot find producerServerResources.");
			return false;
		}
		for (ProducerServerResource pServerResource : pServerResources) {
			String serverIp = pServerResource.getIp();
			if (StringUtils.isBlank(serverIp) || !pServerResource.isAlarm()) {
				continue;
			}
			if (!statisProducerServerIps.containsKey(serverIp)
					|| System.currentTimeMillis() - statisProducerServerIps.get(serverIp).longValue() > SENDER_TIME_SPAN) {
				if (logger.isInfoEnabled()) {
					logger.info("serverIp : {}", serverIp);
				}
				report(serverIp, ServerType.SERVER_SENDER);
				lastCheckStatus.put(serverIp, false);
			} else if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
				report(serverIp, ServerType.SERVER_SENDER_OK);
				lastCheckStatus.put(serverIp, true);
			}
		}
		return true;
	}

	private void report(String serverIp, ServerType serverType) {
		ServerEvent serverEvent = eventFactory.createServerEvent();
		serverEvent.setIp(serverIp).setSlaveIp(serverIp).setServerType(serverType).setEventType(EventType.PRODUCER)
				.setCreateTime(new Date());
		eventReporter.report(serverEvent);
	}
}
