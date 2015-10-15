package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.AlarmConfig;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

/**
 * 
 * @author qiyin
 *
 *         2015年9月17日 下午8:24:53
 */
@Component
public class ConsumerSlaveServiceAlarmer extends AbstractServiceAlarmer {

	private String slaveMonitorUrl = "http://{ip}:8080/names/";

	private static final String MONOGO_MONITOR_SIGN = "mongoManager";

	@Autowired
	private AlarmConfig alarmConfig;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private ResourceContainer resourceContainer;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		alarmInterval = 30;
		alarmDelay = 30;
		if (StringUtils.isNotBlank(alarmConfig.getSlaveMonitorUrl())) {
			slaveMonitorUrl = alarmConfig.getSlaveMonitorUrl();
		}
	}

	@Override
	public void doAlarm() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				checkSlaveService();
			}
		});
	}

	private boolean checkSlaveService() {
		List<ConsumerServerResource> cSlaveReources = resourceContainer.findConsumerSlaveServerResources();
		if (cSlaveReources == null) {
			logger.error("[checkSlaveService] cannot find consumerSlaveServerResources.");
			return false;
		}
		for (ConsumerServerResource cSlaveReource : cSlaveReources) {
			String slaveIp = cSlaveReource.getIp();
			if (StringUtils.isBlank(slaveIp) || !cSlaveReource.isAlarm()) {
				continue;
			}
			String url = StringUtils.replace(slaveMonitorUrl, "{ip}", slaveIp);
			HttpResult result = httpRequest(url);

			if (!result.isSuccess() || !result.getResponseBody().contains(MONOGO_MONITOR_SIGN)) {
				report(slaveIp, ServerType.SLAVE_SERVICE);
				lastCheckStatus.put(slaveIp, false);
			} else {
				if (lastCheckStatus.containsKey(slaveIp) && !lastCheckStatus.get(slaveIp).booleanValue()) {
					report(slaveIp, ServerType.SLAVE_SERVICE_OK);
					lastCheckStatus.put(slaveIp, true);
				}
			}
		}
		return true;
	}

	private void report(String slaveIp, ServerType serverType) {
		ServerEvent serverEvent = eventFactory.createServerEvent();
		serverEvent.setIp(slaveIp).setSlaveIp(slaveIp).setServerType(serverType)
				.setEventType(EventType.CONSUMER).setCreateTime(new Date());
		eventReporter.report(serverEvent);
	}

}
