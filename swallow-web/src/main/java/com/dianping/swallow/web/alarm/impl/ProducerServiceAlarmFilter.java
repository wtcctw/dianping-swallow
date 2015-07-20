package com.dianping.swallow.web.alarm.impl;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.manager.IPDescManager;
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
	private AlarmManager alarmManager;

	@Autowired
	private HttpService httpSerivice;

	@Autowired
	private IPDescManager ipDescManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private GlobalAlarmSettingService swallowAlarmSettingService;

	@Override
	public boolean doAccept() {
		return checkService();
	}

	private boolean checkService() {
		List<String> producerServerIps = ipCollectorService.getProducerServerIps();
		List<String> whiteList = swallowAlarmSettingService.getProducerWhiteList();
		for (String serverIp : producerServerIps) {
			if (StringUtils.isBlank(serverIp)) {
				continue;
			}
			if (!whiteList.contains(serverIp)) {
				String url = StringUtils.replace(PIGEON_HEALTH_URL_KEY, "{ip}", serverIp);
				if (!httpSerivice.httpGet(url).isSuccess()) {
					alarmManager.producerServiceAlarm(serverIp);
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
