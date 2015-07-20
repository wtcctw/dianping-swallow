package com.dianping.swallow.web.alarm.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.statis.ConsumerBaseStatsData;
import com.dianping.swallow.web.model.statis.ConsumerMachineStatsData;
import com.dianping.swallow.web.model.statis.ConsumerServerStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerDataWapper;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerServerStatisDataService;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerServerStatisAlarmFilter")
public class ConsumerServerStatisAlarmFilter extends AbstractStatisAlarmFilter implements MonitorDataListener {

	private ConsumerServerStatsData serverStatisData;

	@Autowired
	private AlarmManager alarmManager;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerDataWapper consumerDataWapper;

	@Autowired
	private ConsumerServerStatisDataService serverStatisDataService;

	@Autowired
	private ConsumerServerAlarmSettingService serverAlarmSettingService;

	@Autowired
	private GlobalAlarmSettingService swallowAlarmSettingService;

	@PostConstruct
	public void initialize() {
		super.initialize();
		consumerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	public boolean doAccept() {
		if (dataCount.get() > 0) {
			dataCount.incrementAndGet();
			serverStatisData = consumerDataWapper.getServerStatsData(lastTimeKey.get());
			return serverAlarm();
		}
		return true;
	}

	public boolean serverAlarm() {
		ConsumerServerAlarmSetting serverAlarmSetting = serverAlarmSettingService.findDefault();
		if (serverAlarmSetting == null) {
			return true;
		}
		QPSAlarmSetting ackQps = serverAlarmSetting.getAckAlarmSetting();
		QPSAlarmSetting sendQps = serverAlarmSetting.getSenderAlarmSetting();
		List<String> whiteList = swallowAlarmSettingService.getConsumerWhiteList();

		if (serverStatisData == null || serverStatisData.getMachineStatisDatas() == null) {
			return true;
		}
		List<ConsumerMachineStatsData> machineStatisDatas = serverStatisData.getMachineStatisDatas();
		for (ConsumerMachineStatsData machineStatisData : machineStatisDatas) {
			ConsumerBaseStatsData baseStatisData = machineStatisData.getStatisData();
			if (whiteList == null || (!whiteList.contains(machineStatisData.getIp()) && baseStatisData != null)) {
				long sendQpx = baseStatisData.getSendQpx();
				sendQpsAlarm(sendQpx, machineStatisData.getIp(), sendQps);
				long ackQpx = baseStatisData.getAckQpx();
				ackQpsAlarm(ackQpx, machineStatisData.getIp(), ackQps);
			}
		}
		return true;
	}

	private boolean sendQpsAlarm(long qpx, String ip, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				alarmManager.consumerServerStatisSQpsPAlarm(ip, qpx, qps.getPeak());
				return false;
			}
			if (qpx < qps.getValley()) {
				alarmManager.consumerServerStatisSQpsVAlarm(ip, qpx, qps.getValley());
				return false;
			}
		}
		return true;
	}

	private boolean ackQpsAlarm(long qpx, String ip, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0) {
			if (qpx > qps.getPeak()) {
				alarmManager.consumerServerStatisAQpsPAlarm(ip, qpx, qps.getPeak());
				return false;
			}
			if (qpx < qps.getValley()) {
				alarmManager.consumerServerStatisAQpsVAlarm(ip, qpx, qps.getValley());
				return false;
			}
		}
		return true;
	}

}
