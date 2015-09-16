package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
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
	private AlarmResourceContainer resourceContainer;

	private static final long SENDER_TIME_SPAN = 20 * 1000;

	@Override
	public void doAlarm() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName());
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				checkSender();
			}
		});
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
