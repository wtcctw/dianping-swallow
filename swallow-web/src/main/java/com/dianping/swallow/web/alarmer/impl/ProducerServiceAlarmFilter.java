package com.dianping.swallow.web.alarmer.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.swallow.web.alarmer.AlarmFilter;
import com.dianping.swallow.web.alarmer.AlarmFilterChain;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;

public class ProducerServiceAlarmFilter extends AbstractProducerAlarmFilter{

	private static final Logger logger = LoggerFactory.getLogger(ProducerServiceAlarmFilter.class);

	private static final String PEGION_PRODUCER_URL_KEY = "http://service.dianping.com/swallowService/producerService_1.0.0";

	private static final String COMMA_SPLIT = ",";

	private static final String COLON_SPLIT = ":";

	private ConfigCache configCache;

	private volatile String producerServerIp;

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPDescManager ipDescManager;
	
	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private ProducerServerAlarmSettingService serverAlarmSettingService;

	@PostConstruct
	public void initialize() {
		configCache = ConfigCache.getInstance();
		setProducerServerIp(configCache.getProperty(PEGION_PRODUCER_URL_KEY));
		configCache.addChange(new ConfigChange() {

			public void onChange(String key, String value) {
				if (key.equals(PEGION_PRODUCER_URL_KEY)) {
					setProducerServerIp(value);
				}
			}
		});
	}

	public boolean doAccept(){
		return checkService();
	}
	
	private boolean checkService(){
		Map<String, String> cmdbProducers = ipCollectorService.getCmdbProducers();
		List<String> whiteList = getWhiteList();
		List<String> ipList = getIpList();

		for (Map.Entry<String, String> cmdbProducer : cmdbProducers.entrySet()) {
			if (StringUtils.isBlank(cmdbProducer.getValue())) {
				continue;
			}
			if (!whiteList.contains(cmdbProducer.getValue()) && !ipList.contains(cmdbProducer.getValue())) {
				String message = "[ip] " + cmdbProducer.getValue()
						+ "service is not work,Please Please handle immediately.";
				alarmService.sendAll(cmdbProducer.getValue(), "[producerServer not work]", message);
				return false;
			}
		}
		return true;
	}
	
	protected List<String> getIpList() {
		List<String> ipList = new ArrayList<String>();
		if (StringUtils.isNotBlank(producerServerIp)) {
			String[] hosts = producerServerIp.split(COMMA_SPLIT);
			if (hosts != null) {
				for (String host : hosts) {
					String[] temp = host.split(COLON_SPLIT);
					if (temp != null && temp.length > 0) {
						ipList.add(temp[0]);
					}
				}
			}

		}
		return ipList;
	}

	protected List<String> getWhiteList() {
		List<ProducerServerAlarmSetting> serverAlarmSettings = serverAlarmSettingService.findAll();
		if (serverAlarmSettings == null || serverAlarmSettings.size() == 0) {
			return null;
		}
		ProducerServerAlarmSetting serverAlarmSetting = serverAlarmSettings.get(0);
		return serverAlarmSetting.getWhiteList();
	}
	
	public String getProducerServerIp() {
		return producerServerIp;
	}

	public void setProducerServerIp(String producerServerIp) {
		this.producerServerIp = producerServerIp;
	}

}
