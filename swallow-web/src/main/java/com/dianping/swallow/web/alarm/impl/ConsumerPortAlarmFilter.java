package com.dianping.swallow.web.alarm.impl;

import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.util.NetUtil;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerPortAlarmFilter")
public class ConsumerPortAlarmFilter extends AbstractServiceAlarmFilter {

	private static final Logger logger = LoggerFactory.getLogger(ConsumerPortAlarmFilter.class);

	private static final String MASTER_NAME = "master";

	private static final String SLAVE_NAME = "slave";

	private static final int CONSUMER_MASTER_SERVICE_PORT = 8081;

	private static final int CONSUMER_SLAVE_SERVICE_PORT = 8082;

	@Autowired
	private AlarmManager alarmManager;

	@Autowired
	private IPDescManager ipDescManager;

	@Autowired
	private IPCollectorService ipCollectorService;

	@Override
	public boolean doAccept() {
		return checkPort();
	}

	public boolean checkPort() {
		Map<String, String> cmdbConsumerMasters = ipCollectorService.getCmdbConsumerMasters();
		Map<String, String> cmdbConsumerSlaves = ipCollectorService.getCmdbConsumerSlaves();
		if (cmdbConsumerMasters == null || cmdbConsumerSlaves == null) {
			logger.error("[doCheckPort] cannot find consumermaster or consumerslave info from cmdb.");
			return false;
		}
		for (Map.Entry<String, String> consumerMaster : cmdbConsumerMasters.entrySet()) {
			String masterName = consumerMaster.getKey();
			String slaveName = StringUtils.replace(masterName, MASTER_NAME, SLAVE_NAME);
			if (!cmdbConsumerSlaves.containsKey(slaveName)) {
				continue;
			}
			boolean usingMaster = NetUtil.isPortOpen(consumerMaster.getValue(), CONSUMER_MASTER_SERVICE_PORT);
			boolean usingSlave = NetUtil.isPortOpen(cmdbConsumerSlaves.get(slaveName), CONSUMER_SLAVE_SERVICE_PORT);
			if (!usingMaster && usingSlave) {
				alarmManager.consumerPortAlarm(consumerMaster.getValue(), cmdbConsumerSlaves.get(slaveName), false);
				return false;
			} else if (!usingMaster && !usingSlave) {
				alarmManager.consumerPortAlarm(consumerMaster.getValue(), cmdbConsumerSlaves.get(slaveName), true);
				return false;
			}
		}
		return true;
	}
}
