package com.dianping.swallow.web.alarmer.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.ConfigChange;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.service.IPDescService;
import com.dianping.swallow.web.util.NetUtil;

/**
 *
 * @author qiyin
 *
 */
public class DefaultProducerServiceAlarmer extends AbstractServiceAlarmer {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerServiceAlarmer.class);

	private static final String PEGION_PRODUCER_URL = "http://service.dianping.com/swallowService/producerService_1.0.0";
	
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
	public void doCheckProcess() {
		
	}

	@Override
	public void doCheckPort() {

	}

	@Override
	public void doCheckService() {
		Map<String,String> cmdbProducers = ipCollectorService.getCmdbProducers();
		
	}

	public String getProducerServerValue() {
		return producerServerValue;
	}

	public void setProducerServerValue(String producerServerValue) {
		this.producerServerValue = producerServerValue;
	}

}
