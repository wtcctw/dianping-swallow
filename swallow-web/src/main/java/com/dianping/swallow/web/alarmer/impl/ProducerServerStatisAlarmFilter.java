package com.dianping.swallow.web.alarmer.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.statis.ProducerBaseStatsData;
import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;
import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerDataWapper;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerStatisDataService;

public class ProducerServerStatisAlarmFilter extends AbstractProducerAlarmFilter implements MonitorDataListener {

	private volatile ProducerServerStatsData serverStatisData;

	private volatile AtomicLong dataCount;

	private static final int INIT_VALUE = 0;
	private static final long DEFAULT_VALUE = -1L;

	private volatile AtomicLong lastTimeKey;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerDataWapper producerDataWapper;

	@Autowired
	private ProducerServerStatisDataService serverStatisDataService;

	@Autowired
	private ProducerServerAlarmSettingService serverAlarmSettingService;

	@PostConstruct
	public void initialize() {
		dataCount.set(INIT_VALUE);
		lastTimeKey.set(DEFAULT_VALUE);
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
		serverStatisData = producerDataWapper.getServerStatsData(lastTimeKey.get());
		storageServerStats();
	}

	@Override
	public boolean doAccept() {
		return dataCount.getAndDecrement() > 0 ? serverAlarm() : true;
	}

	public boolean serverAlarm() {
		ProducerServerAlarmSetting serverAlarmSetting = serverAlarmSettingService.findOne();
		if (serverAlarmSetting == null) {
			return true;
		}
		QPSAlarmSetting qps = serverAlarmSetting.getDefaultAlarmSetting();
		List<String> whiteList = serverAlarmSetting.getWhiteList();

		if (qps == null || serverStatisData == null || serverStatisData.getStatisDatas() == null) {
			return true;
		}
		List<ProducerMachineStatsData> machineStatisDatas = serverStatisData.getStatisDatas();
		if (machineStatisDatas == null || machineStatisDatas.size() == 0) {
			return true;
		}

		for (ProducerMachineStatsData machineStatisData : machineStatisDatas) {
			ProducerBaseStatsData baseStatisData = machineStatisData.getStatisData();
			if (whiteList == null || (!whiteList.contains(machineStatisData.getIp()) && baseStatisData != null)) {
				long qpx = baseStatisData.getQpx();
				if (qpx > qps.getPeak() || qpx < qps.getValley()) {
					// alram
					return false;
				}
			}
		}
		return true;
	}

	private void storageServerStats() {
		if (serverStatisData != null) {
			serverStatisDataService.insert(serverStatisData);
		}
	}

}
