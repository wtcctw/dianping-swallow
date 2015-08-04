package com.dianping.swallow.web.alarm.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.ConsumerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.ServerStatisEvent;
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
//@Service("consumerServerStatisAlarmFilter")
public class ConsumerServerStatisAlarmFilter extends AbstractStatisAlarmFilter implements MonitorDataListener {

	@Autowired
	private MessageManager alarmManager;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerDataWapper consumerDataWapper;

	@Autowired
	private ConsumerServerStatisDataService serverStatisDataService;

	@Autowired
	private ConsumerServerAlarmSettingService serverAlarmSettingService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

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
			dataCount.decrementAndGet();
			ConsumerServerStatsData serverStatisData = consumerDataWapper.getServerStatsData(lastTimeKey.get());
			return serverAlarm(serverStatisData);
		}
		return true;
	}

	public boolean serverAlarm(ConsumerServerStatsData serverStatisData) {
		ConsumerServerAlarmSetting serverAlarmSetting = serverAlarmSettingService.findDefault();
		if (serverAlarmSetting == null) {
			return true;
		}
		QPSAlarmSetting ackQps = serverAlarmSetting.getAckAlarmSetting();
		QPSAlarmSetting sendQps = serverAlarmSetting.getSendAlarmSetting();
		List<String> whiteList = globalAlarmSettingService.getConsumerWhiteList();

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
				ServerStatisEvent statisEvent = new ServerStatisEvent();
				statisEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SENDQPS_PEAK);
				statisEvent.setIp(ip);
				statisEvent.setEventType(EventType.CONSUMER);
				statisEvent.setCurrentValue(qpx);
				statisEvent.setExpectedValue(qps.getPeak());
				statisEvent.setCreateTime(new Date());
				eventReporter.report(statisEvent);
				return false;
			}
			if (qpx < qps.getValley()) {
				ServerStatisEvent statisEvent = new ServerStatisEvent();
				statisEvent.setAlarmType(AlarmType.CONSUMER_SERVER_SENDQPS_VALLEY);
				statisEvent.setIp(ip);
				statisEvent.setEventType(EventType.CONSUMER);
				statisEvent.setCurrentValue(qpx);
				statisEvent.setExpectedValue(qps.getValley());
				statisEvent.setCreateTime(new Date());
				eventReporter.report(statisEvent);
				return false;
			}
		}
		return true;
	}

	private boolean ackQpsAlarm(long qpx, String ip, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0) {
			if (qpx > qps.getPeak()) {
				ServerStatisEvent statisEvent = new ServerStatisEvent();
				statisEvent.setAlarmType(AlarmType.CONSUMER_SERVER_ACKQPS_PEAK);
				statisEvent.setIp(ip);
				statisEvent.setEventType(EventType.CONSUMER);
				statisEvent.setCurrentValue(qpx);
				statisEvent.setExpectedValue(qps.getPeak());
				statisEvent.setCreateTime(new Date());
				eventReporter.report(statisEvent);
				return false;
			}
			if (qpx < qps.getValley()) {
				ServerStatisEvent statisEvent = new ServerStatisEvent();
				statisEvent.setAlarmType(AlarmType.CONSUMER_SERVER_ACKQPS_VALLEY);
				statisEvent.setIp(ip);
				statisEvent.setEventType(EventType.CONSUMER);
				statisEvent.setCurrentValue(qpx);
				statisEvent.setExpectedValue(qps.getValley());
				statisEvent.setCreateTime(new Date());
				eventReporter.report(statisEvent);
				return false;
			}
		}
		return true;
	}

}
