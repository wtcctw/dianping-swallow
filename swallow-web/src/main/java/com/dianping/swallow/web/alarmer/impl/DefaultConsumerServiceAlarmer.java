package com.dianping.swallow.web.alarmer.impl;

import java.util.Map;

import org.codehaus.plexus.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.alarmer.ConsumerServiceAlarmer;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.service.AlarmService;
import com.dianping.swallow.web.service.IPCollectorService;
import com.dianping.swallow.web.util.NetUtil;

/**
 *
 * @author qiyin
 *
 */
@Service
public class DefaultConsumerServiceAlarmer extends AbstractServiceAlarmer implements ConsumerServiceAlarmer{

	private static final Logger logger = LoggerFactory.getLogger(DefaultConsumerServiceAlarmer.class);

	private static final String MASTER_NAME = "master";

	private static final String SLAVE_NAME = "slave";

	private static final int CONSUMER_MASTER_SERVICE_PORT = 8081;

	private static final int CONSUMER_SLAVE_SERVICE_PORT = 8082;

	@Autowired
	private IPDescManager ipDescManager;

	@Autowired
	private IPCollectorService ipCollectorService;
	
	public DefaultConsumerServiceAlarmer() {
		setAlarmInterval(30);
	}

	@Autowired
	private AlarmService alarmService;
	
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
		Map<String, String> cmdbConsumerMasters = ipCollectorService.getCmdbConsumerMasters();
		Map<String, String> cmdbConsumerSlaves = ipCollectorService.getCmdbConsumerSlaves();
		if (cmdbConsumerMasters == null || cmdbConsumerSlaves == null) {
			logger.error("[doCheckPort] cannot find consumermaster or consumerslave info from cmdb.");
			return;
		}
		for (Map.Entry<String, String> consumerMaster : cmdbConsumerMasters.entrySet()) {
			String masterName = consumerMaster.getKey();
			String slaveName = StringUtils.replace(masterName, MASTER_NAME, SLAVE_NAME);
			if (cmdbConsumerSlaves.containsKey(slaveName)) {
				boolean usingMaster = NetUtil.isPortUsing(consumerMaster.getValue(), CONSUMER_MASTER_SERVICE_PORT);
				boolean usingSlave = NetUtil.isPortUsing(cmdbConsumerSlaves.get(slaveName), CONSUMER_SLAVE_SERVICE_PORT);

				if (!usingMaster && usingSlave) {
					String message = "[Consumer Server] [master ip]" + consumerMaster.getValue() + " [port] "
							+ CONSUMER_MASTER_SERVICE_PORT + "  [slave ip]" + cmdbConsumerSlaves.get(slaveName)
							+ "[port]" + CONSUMER_SLAVE_SERVICE_PORT
							+ " is not Occupied. May be master server is Down.";
					alarmService.sendAll(consumerMaster.getValue(), "[Consumer master switch to slave]", message);
				} else if (usingMaster && usingSlave) {
					String message = "[Consumer Server] [ip]" + consumerMaster.getValue() + " [port] "
							+ CONSUMER_MASTER_SERVICE_PORT + "  [slave ip]" + cmdbConsumerSlaves.get(slaveName)
							+ "[port]" + CONSUMER_SLAVE_SERVICE_PORT + " is not Occupied. Please handle immediately.";
					alarmService.sendAll(consumerMaster.getValue(), "[Consumer master and slave both working]", message);
				}
			}

		}

	}

	@Override
	public void doCheckService() {
		

	}
	
	@Override
	public void doCheckSender() {
		

	}
	

}
