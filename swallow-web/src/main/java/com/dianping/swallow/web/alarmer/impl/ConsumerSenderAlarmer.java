package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.container.ResourceContainer.ConsumerServerResourcePair;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.service.IPCollectorService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:35
 */
//@Component
public class ConsumerSenderAlarmer extends AbstractServiceAlarmer {

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private ResourceContainer resourceContainer;

	@Autowired
	private ConsumerPortAlarmer consumerPortAlarmer;

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

		List<ConsumerServerResourcePair> cServerReourcePairs = resourceContainer.findConsumerServerResourcePairs();

		if (cServerReourcePairs == null || cServerReourcePairs.size() == 0) {
			logger.error("[checkSender] cannot find consumer server master reources.");
			return false;
		}

		Map<String, Long> statisConsumerServerIps = ipCollectorService.getStatisConsumerServerIps();

		for (ConsumerServerResourcePair cServerReourcePair : cServerReourcePairs) {

			ConsumerServerResource cMasterResource = cServerReourcePair.getMasterResource();
			ConsumerServerResource cSlaveResource = cServerReourcePair.getSlaveResource();
			String serverIp = cMasterResource.getIp();

			if (StringUtils.isBlank(serverIp) || !cMasterResource.isAlarm()) {
				continue;
			}
			if (!statisConsumerServerIps.containsKey(serverIp)
					|| System.currentTimeMillis() - statisConsumerServerIps.get(serverIp).longValue() > SENDER_TIME_SPAN) {
				String slaveIp = cSlaveResource.getIp();
				if (checkSlaveServerSender(statisConsumerServerIps, serverIp, slaveIp)) {
					report(serverIp, serverIp, ServerType.SERVER_SENDER);
					lastCheckStatus.put(serverIp, false);
				}
			} else {
				if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
					report(serverIp, serverIp, ServerType.SERVER_SENDER_OK);
					lastCheckStatus.put(serverIp, true);
				}
			}
		}
		return true;
	}

	private boolean checkSlaveServerSender(Map<String, Long> statisIps, String masterIp, String slaveIp) {
		if (consumerPortAlarmer.isSlaveOpen(masterIp)) {
			if (!statisIps.containsKey(slaveIp)) {
				report(slaveIp, slaveIp, ServerType.SERVER_SENDER);
				lastCheckStatus.put(slaveIp, false);
			} else {
				if (lastCheckStatus.containsKey(slaveIp) && !lastCheckStatus.get(slaveIp).booleanValue()) {
					report(slaveIp, slaveIp, ServerType.SERVER_SENDER_OK);
					lastCheckStatus.put(slaveIp, true);
				}
			}
			return false;
		}
		return true;
	}

	private void report(String serverIp, String slaveIp, ServerType serverType) {
		ServerEvent serverEvent = eventFactory.createServerEvent();
		serverEvent.setIp(serverIp).setSlaveIp(slaveIp).setServerType(serverType).setEventType(EventType.CONSUMER)
				.setCreateTime(new Date());
		eventReporter.report(serverEvent);
	}

}
