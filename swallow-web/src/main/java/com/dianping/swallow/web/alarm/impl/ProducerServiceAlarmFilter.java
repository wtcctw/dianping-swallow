package com.dianping.swallow.web.alarm.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.SwallowAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("producerServiceAlarmFilter")
public class ProducerServiceAlarmFilter extends AbstractServiceAlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(ProducerServiceAlarmFilter.class);

	private static final String PIGEON_PRODUCER_URL_KEY = "http://service.dianping.com/swallowService/producerService_1.0.0";

	private static final String COMMA_SPLIT = ",";

	private static final String COLON_SPLIT = ":";

	private ConfigCache configCache;

	private volatile String producerServerIp;

	@Autowired
	private AlarmManager alarmManager;

	@Autowired
	private IPDescManager ipDescManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private SwallowAlarmSettingService swallowAlarmSettingService;

	@PostConstruct
	public void initialize() {
		try {
			configCache = ConfigCache.getInstance();
			setProducerServerIp(configCache.getProperty(PIGEON_PRODUCER_URL_KEY));
			configCache.addChange(new ConfigChange() {

				public void onChange(String key, String value) {
					if (key.equals(PIGEON_PRODUCER_URL_KEY)) {
						setProducerServerIp(value);
					}
				}
			});
		} catch (LionException e) {
			logger.error("[initialize] lion read pigeon producer service url.");
			// throw new RuntimeException();
		}
	}

	@Override
	public boolean doAccept() {
		return checkService();
	}

	private boolean checkService() {
		Map<String, String> cmdbProducers = ipCollectorService.getCmdbProducers();
		List<String> whiteList = swallowAlarmSettingService.getProducerWhiteList();
		List<String> ipList = getIpList();

		for (Map.Entry<String, String> cmdbProducer : cmdbProducers.entrySet()) {
			if (StringUtils.isBlank(cmdbProducer.getValue())) {
				continue;
			}
			if (!whiteList.contains(cmdbProducer.getValue()) && !ipList.contains(cmdbProducer.getValue())) {
				alarmManager.producerServiceAlarm(cmdbProducer.getValue());
				return false;
			}
		}
		return true;
	}

	private List<String> getIpList() {
		List<String> ipList = new ArrayList<String>();
		if (StringUtils.isBlank(producerServerIp)) {
			return ipList;
		}
		String[] hosts = producerServerIp.split(COMMA_SPLIT);
		if (hosts == null) {
			return ipList;
		}
		for (String host : hosts) {
			String[] temp = host.split(COLON_SPLIT);
			if (temp != null && temp.length > 0) {
				ipList.add(temp[0]);
			}
		}
		return ipList;
	}

	public String getProducerServerIp() {
		return producerServerIp;
	}

	public void setProducerServerIp(String producerServerIp) {
		this.producerServerIp = producerServerIp;
	}

}
