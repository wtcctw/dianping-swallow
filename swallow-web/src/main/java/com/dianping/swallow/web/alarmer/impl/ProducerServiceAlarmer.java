package com.dianping.swallow.web.alarmer.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
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
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.HttpService.HttpResult;
import com.dianping.swallow.web.service.IpCollectorService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:07:08
 */
@Component
public class ProducerServiceAlarmer extends AbstractServiceAlarmer {

	private String pigeonHealthUrl = "http://{ip}:4080/stats.json";

	private static final String PIGEON_HEALTH_URL_KEY = "pigeonHealthUrl";

	@Autowired
	private HttpService httpSerivice;

	@Autowired
	private IpCollectorService ipCollectorService;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		initProperties();
	}

	private void initProperties() {
		try {
			InputStream in = ProducerServiceAlarmer.class.getClassLoader().getResourceAsStream(SERVER_CHECK_URL_FILE);
			if (in != null) {
				Properties prop = new Properties();
				try {
					prop.load(in);
					pigeonHealthUrl = StringUtils.trim(prop.getProperty(PIGEON_HEALTH_URL_KEY));
				} finally {
					in.close();
				}
			} else {
				logger.info("[initProperties] Load {} file failed.", SERVER_CHECK_URL_FILE);
				throw new RuntimeException();
			}
		} catch (Exception e) {
			logger.info("[initProperties] Load {} file failed.", SERVER_CHECK_URL_FILE);
			throw new RuntimeException(e);
		}
	}

	@Override
	public void doAlarm() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "doAlarm");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				checkService();
			}
		});
	}

	private boolean checkService() {
		List<String> producerServerIps = ipCollectorService.getProducerServerIps();
		if (producerServerIps == null) {
			logger.error("[checkService] cannot find producerserver ips.");
			return false;
		}
		for (String serverIp : producerServerIps) {
			if (StringUtils.isBlank(serverIp)) {
				continue;
			}
			ProducerServerResource pServerResource = resourceContainer.findProducerServerResource(serverIp);
			if (pServerResource == null || !pServerResource.isAlarm()) {
				continue;
			}
			String url = StringUtils.replace(pigeonHealthUrl, "{ip}", serverIp);
			HttpResult result = checkUrl(url);
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
