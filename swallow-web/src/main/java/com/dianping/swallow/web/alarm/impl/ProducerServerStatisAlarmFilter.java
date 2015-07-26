package com.dianping.swallow.web.alarm.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerStatisEvent;
import com.dianping.swallow.web.model.statis.ProducerBaseStatsData;
import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;
import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerDataWapper;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerStatisDataService;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("producerServerStatisAlarmFilter")
public class ProducerServerStatisAlarmFilter extends AbstractStatisAlarmFilter implements MonitorDataListener {

	private ProducerServerStatsData serverStatisData;

	@Autowired
	private MessageManager alarmManager;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerDataWapper producerDataWapper;

	@Autowired
	private ProducerServerStatisDataService serverStatisDataService;

	@Autowired
	private ProducerServerAlarmSettingService serverAlarmSettingService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	@PostConstruct
	public void initialize() {
		super.initialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	public boolean doAccept() {
		if (dataCount.get() > 0) {
			dataCount.incrementAndGet();
			serverStatisData = producerDataWapper.getServerStatsData(lastTimeKey.get());
			return serverAlarm();
		}
		return true;
	}

	public boolean serverAlarm() {
		ProducerServerAlarmSetting serverAlarmSetting = serverAlarmSettingService.findDefault();
		if (serverAlarmSetting == null) {
			return true;
		}
		QPSAlarmSetting qps = serverAlarmSetting.getAlarmSetting();
		List<String> whiteList = globalAlarmSettingService.getProducerWhiteList();

		if (qps == null || serverStatisData == null || serverStatisData.getStatisDatas() == null) {
			return true;
		}
		List<ProducerMachineStatsData> machineStatisDatas = serverStatisData.getStatisDatas();
		for (ProducerMachineStatsData machineStatisData : machineStatisDatas) {
			ProducerBaseStatsData baseStatisData = machineStatisData.getStatisData();
			if (whiteList == null || (!whiteList.contains(machineStatisData.getIp()) && baseStatisData != null)) {
				qpsAlarm(baseStatisData.getQpx(), machineStatisData.getIp(), qps);
			}
		}
		return true;
	}

	private boolean qpsAlarm(long qpx, String ip, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				ServerStatisEvent statisEvent = new ServerStatisEvent();
				statisEvent.setAlarmType(AlarmType.PRODUCER_SERVER_QPS_PEAK);
				statisEvent.setIp(ip);
				statisEvent.setEventType(EventType.PRODUCER);
				statisEvent.setCurrentValue(qpx);
				statisEvent.setExpectedValue(qps.getPeak());
				statisEvent.setCreateTime(new Date());
				eventReporter.report(statisEvent);
				return false;
			}
			if (qpx < qps.getValley()) {
				ServerStatisEvent statisEvent = new ServerStatisEvent();
				statisEvent.setAlarmType(AlarmType.PRODUCER_SERVER_QPS_VALLEY);
				statisEvent.setIp(ip);
				statisEvent.setEventType(EventType.PRODUCER);
				statisEvent.setCurrentValue(qpx);
				statisEvent.setExpectedValue(qps.getPeak());
				statisEvent.setCreateTime(new Date());
				eventReporter.report(statisEvent);
				return false;
			}
		}
		return true;
	}

}
