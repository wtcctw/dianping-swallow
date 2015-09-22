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
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.service.IPCollectorService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:07:08
 */
@Component
public class ProducerServiceAlarmer extends AbstractServiceAlarmer {

	private String pigeonHealthUrl = "http://{ip}:4080/stats.json";

	@Autowired
	private AlarmConfig alarmConfig;

	@Autowired
	private HttpService httpSerivice;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		alarmInterval = 30;
		alarmDelay = 30;
		if (StringUtils.isNotBlank(alarmConfig.getPigeonHealthUrl())) {
			pigeonHealthUrl = alarmConfig.getPigeonHealthUrl();
		}
	}

	@Override
	public void doAlarm() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				checkService();
			}
		});
	}

	private boolean checkService() {
		List<ProducerServerResource> pServerResources = resourceContainer.findProducerServerResources(false);
		if (pServerResources == null) {
			logger.error("[checkService] cannot find producerServerResources.");
			return false;
		}
		for (ProducerServerResource pServerResource : pServerResources) {
			String serverIp = pServerResource.getIp();
			if (StringUtils.isBlank(serverIp) || !pServerResource.isAlarm()) {
				continue;
			}
			String url = StringUtils.replace(pigeonHealthUrl, "{ip}", serverIp);
			HttpResult result = httpRequest(url);
			if (!result.isSuccess()) {
				ServerEvent serverEvent = eventFactory.createServerEvent();
				serverEvent.setIp(serverIp).setSlaveIp(serverIp).setServerType(ServerType.PIGEON_SERVICE)
						.setEventType(EventType.PRODUCER).setCreateTime(new Date());
				eventReporter.report(serverEvent);
				lastCheckStatus.put(serverIp, false);
			} else if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
				ServerEvent serverEvent = eventFactory.createServerEvent();
				serverEvent.setIp(serverIp).setSlaveIp(serverIp).setServerType(ServerType.PIGEON_SERVICE_OK)
						.setEventType(EventType.PRODUCER).setCreateTime(new Date());
				eventReporter.report(serverEvent);
				lastCheckStatus.put(serverIp, true);
			}

		}
		return true;
	}

}
