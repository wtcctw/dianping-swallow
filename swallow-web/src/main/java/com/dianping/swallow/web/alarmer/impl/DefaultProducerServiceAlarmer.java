package com.dianping.swallow.web.alarmer.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.swallow.web.alarmer.ProducerServiceAlarmer;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */

public class DefaultProducerServiceAlarmer extends AbstractServiceAlarmer implements ProducerServiceAlarmer {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerServiceAlarmer.class);

	private static final String PEGION_PRODUCER_URL = "http://service.dianping.com/swallowService/producerService_1.0.0";

	private static final String CHECK_SERVICE_WHITELIST = "swallow.producerserver.checkservice.whitelist";

	private static final String COMMA_SPLIT = ",";
	private static final String COLON_SPLIT = ":";

	private ConfigCache configCache;

	private volatile String producerServerValue;

	private volatile String producerServerWhiteList;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private ProducerServerAlarmSettingService producerServerAlarmSettingService;

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPDescManager ipDescManager;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		configCache = ConfigCache.getInstance();
		producerServerValue = configCache.getProperty(PEGION_PRODUCER_URL);
		setProducerServerWhiteList(configCache.getProperty(CHECK_SERVICE_WHITELIST));
		configCache.addChange(new ConfigChange() {

			public void onChange(String key, String value) {
				if (key.equals(PEGION_PRODUCER_URL)) {
					setProducerServerValue(value);
				} else if (key.equals(CHECK_SERVICE_WHITELIST)) {
					setProducerServerWhiteList(value);
				}
			}
		});
	}

	public DefaultProducerServiceAlarmer() {
		setAlarmInterval(30);
	}

	@Override
	public void doAlarm() {
		doCheckProcess();
		doCheckPort();
		doCheckService();
		doCheckSender();
		logger.info("[doAlarm] check producer service.");
	}

	@Override
	public void doCheckProcess() {

	}

	@Override
	public void doCheckPort() {

	}

	@Override
	public void doCheckService() {
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
			}
		}

	}

	private List<String> getIpList() {
		List<String> ipList = new ArrayList<String>();
		if (StringUtils.isNotBlank(producerServerValue)) {
			String[] hosts = producerServerValue.split(COMMA_SPLIT);
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

	private List<String> getWhiteList() {
		List<String> whiteList = new ArrayList<String>();
		if (StringUtils.isNotBlank(producerServerWhiteList)) {
			String[] whites = producerServerWhiteList.split(COMMA_SPLIT);
			for (String white : whites) {
				if (StringUtils.isNotBlank(white)) {
					whiteList.add(white);
				}
			}
		}
		return whiteList;
	}

	@Override
	public void doCheckSender() {
		Set<String> serverIps = ipCollectorService.getProducerServerIps();
		if (serverIps != null) {
			Iterator<String> iterator = serverIps.iterator();
			List<String> whiteList = getWhiteList();
			while (iterator.hasNext()) {
				String serverIp = iterator.next();
				if (whiteList == null && !whiteList.contains(serverIp)) {

				}
			}
		}
	}

	public String getProducerServerValue() {
		return producerServerValue;
	}

	public void setProducerServerValue(String producerServerValue) {
		this.producerServerValue = producerServerValue;
	}

	public String getProducerServerWhiteList() {
		return producerServerWhiteList;
	}

	public void setProducerServerWhiteList(String producerServerWhiteList) {
		this.producerServerWhiteList = producerServerWhiteList;
	}

}
