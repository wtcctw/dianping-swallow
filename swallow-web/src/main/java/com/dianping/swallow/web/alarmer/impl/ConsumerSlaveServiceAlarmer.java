package com.dianping.swallow.web.alarmer.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.event.EventFactory;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerType;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.HttpService.HttpResult;

@Component
public class ConsumerSlaveServiceAlarmer extends AbstractServiceAlarmer {

	private static final String SLAVE_MONITOR_URL_KEY = "slaveMonitorUrl";

	private String slaveMonitorUrl = "http://{ip}:8080/names";

	private static final String MONOGO_MONITOR_SIGN = "mongoManager";

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

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
					slaveMonitorUrl = StringUtils.trim(prop.getProperty(SLAVE_MONITOR_URL_KEY));
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
				checkSlaveService();
			}
		});
	}

	private boolean checkSlaveService() {
		List<String> whiteList = globalAlarmSettingService.getConsumerWhiteList();
		List<String> consumerServerSlaveIps = ipCollectorService.getConsumerServerSlaveIps();
		if (consumerServerSlaveIps == null) {
			return false;
		}
		for (String slaveIp : consumerServerSlaveIps) {
			if (whiteList == null || !whiteList.contains(slaveIp)) {
				String url = StringUtils.replace(slaveMonitorUrl, "{ip}", slaveIp);
				HttpResult result = checkUrl(url);
				if (!result.isSuccess() || !result.getResponseBody().contains(MONOGO_MONITOR_SIGN)) {
					ServerEvent serverEvent = EventFactory.getInstance().createServerEvent();
					serverEvent.setIp(slaveIp).setSlaveIp(slaveIp).setServerType(ServerType.SLAVE_SERVICE)
							.setEventType(EventType.CONSUMER).setCreateTime(new Date());
					eventReporter.report(serverEvent);
					lastCheckStatus.put(slaveIp, false);
				} else {
					if (lastCheckStatus.containsKey(slaveIp) && !lastCheckStatus.get(slaveIp).booleanValue()) {
						ServerEvent serverEvent = EventFactory.getInstance().createServerEvent();
						serverEvent.setIp(slaveIp).setSlaveIp(slaveIp).setServerType(ServerType.SLAVE_SERVICE_OK)
								.setEventType(EventType.CONSUMER).setCreateTime(new Date());
						eventReporter.report(serverEvent);
						lastCheckStatus.put(slaveIp, true);
					}
				}
			}
		}
		return true;
	}

}
