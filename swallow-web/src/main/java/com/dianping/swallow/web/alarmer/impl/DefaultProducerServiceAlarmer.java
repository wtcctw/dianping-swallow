package com.dianping.swallow.web.alarmer.impl;

import java.util.Map;

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

/**
 *
 * @author qiyin
 *
 */
public class DefaultProducerServiceAlarmer extends AbstractServiceAlarmer implements ProducerServiceAlarmer{

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerServiceAlarmer.class);

	private static final String PEGION_PRODUCER_URL = "http://service.dianping.com/swallowService/producerService_1.0.0";

	private static final String PEGION_PRODUCER_SPLIT = ",";

	private ConfigCache configCache;

	private volatile String producerServerValue;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Autowired
	private AlarmService alarmService;

	@Autowired
	private IPDescManager ipDescManager;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		configCache = ConfigCache.getInstance();
		producerServerValue = configCache.getProperty(PEGION_PRODUCER_URL);
		configCache.addChange(new ConfigChange() {

			public void onChange(String key, String value) {
				if (key.equals(PEGION_PRODUCER_URL)) {
					setProducerServerValue(value);
				}
			}
		});
	}

	public DefaultProducerServiceAlarmer() {
		setAlarmInterval(30);
	}

	@Override
	public void doAlarm(){
		doCheckProcess();
		doCheckPort();
		doCheckService();
		doCheckSender();
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
		if (StringUtils.isNotBlank(producerServerValue)) {
			String[] ips = producerServerValue.split(PEGION_PRODUCER_SPLIT);
			if (ips != null) {
			} else {

			}

		}

	}

	@Override
	public void doCheckSender() {

	}

	public String getProducerServerValue() {
		return producerServerValue;
	}

	public void setProducerServerValue(String producerServerValue) {
		this.producerServerValue = producerServerValue;
	}

}
