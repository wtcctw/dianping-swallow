package com.dianping.swallow.web.alarm.impl;

import java.util.Date;
import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.service.HttpService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("producerServiceAlarmFilter")
public class ProducerServiceAlarmFilter extends AbstractServiceAlarmFilter {

	private static final String PIGEON_HEALTH_URL_KEY = "http://{ip}:4080/stats.json";

	private volatile String producerServerIp;

	@Autowired
	private MessageManager alarmManager;

	@Autowired
	private HttpService httpSerivice;

	@Autowired
	private IPDescManager ipDescManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	@Override
	public boolean doAccept() {
		return checkService();
	}

	private boolean checkService() {
		List<String> producerServerIps = ipCollectorService.getProducerServerIps();
		List<String> whiteList = globalAlarmSettingService.getProducerWhiteList();
		for (String serverIp : producerServerIps) {
			if (StringUtils.isBlank(serverIp)) {
				continue;
			}
			if (!whiteList.contains(serverIp)) {
				String url = StringUtils.replace(PIGEON_HEALTH_URL_KEY, "{ip}", serverIp);
				if (!httpSerivice.httpGet(url).isSuccess() && !httpSerivice.httpGet(url).isSuccess()) {
					ServerEvent serverEvent = new ServerEvent();
					serverEvent.setIp(serverIp);
					serverEvent.setSlaveIp(serverIp);
					serverEvent.setAlarmType(AlarmType.PRODUCER_SERVER_PIGEON_SERVICE);
					serverEvent.setEventType(EventType.PRODUCER);
					serverEvent.setCreateTime(new Date());
					eventReporter.report(serverEvent);
					lastCheckStatus.put(serverIp, false);
				} else if (lastCheckStatus.containsKey(serverIp) && !lastCheckStatus.get(serverIp).booleanValue()) {
					ServerEvent serverEvent = new ServerEvent();
					serverEvent.setIp(serverIp);
					serverEvent.setSlaveIp(serverIp);
					serverEvent.setAlarmType(AlarmType.PRODUCER_SERVER_PIGEON_SERVICE_OK);
					serverEvent.setEventType(EventType.PRODUCER);
					serverEvent.setCreateTime(new Date());
					eventReporter.report(serverEvent);
					lastCheckStatus.put(serverIp, true);
				}
			}
		}
		return true;
	}

	public String getProducerServerIp() {
		return producerServerIp;
	}

	public void setProducerServerIp(String producerServerIp) {
		this.producerServerIp = producerServerIp;
	}

}
