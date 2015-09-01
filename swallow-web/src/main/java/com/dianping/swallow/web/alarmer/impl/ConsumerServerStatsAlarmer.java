package com.dianping.swallow.web.alarmer.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.container.AlarmResourceContainer;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;

@Component
public class ConsumerServerStatsAlarmer extends AbstractStatsAlarmer implements MonitorDataListener {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ConsumerServerStatsDataService serverStatsDataService;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	public void doAlarm() {
		if (dataCount.get() <= 0) {
			return;
		}
		dataCount.decrementAndGet();
		final List<ConsumerServerStatsData> serverStatsDatas = consumerStatsDataWapper.getServerStatsDatas(lastTimeKey
				.get());
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "doAlarm");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				serverAlarm(serverStatsDatas);
			}
		});

	}

	private void serverAlarm(List<ConsumerServerStatsData> serverStatsDatas) {
		if (serverStatsDatas == null) {
			return;
		}

		for (ConsumerServerStatsData serverStatsData : serverStatsDatas) {
			String ip = serverStatsData.getIp();
			ConsumerServerResource cServerResource = resourceContainer.findConsumerServerResource(ip);
			if (cServerResource == null || !cServerResource.isAlarm() || StringUtils.equals(TOTAL_KEY, ip)) {
				continue;
			}
			QPSAlarmSetting sendQps = cServerResource.getSendAlarmSetting();
			QPSAlarmSetting ackQps = cServerResource.getAckAlarmSetting();
			
			qpsSendAlarm(serverStatsData, sendQps);
			qpsAckAlarm(serverStatsData, ackQps);
		}
	}

	private boolean qpsSendAlarm(ConsumerServerStatsData serverStatsData, QPSAlarmSetting qps) {
		if (qps != null) {
			if (!serverStatsData.checkSendQpsPeak(qps.getPeak())) {
				return false;
			}
			if (!serverStatsData.checkSendQpsValley(qps.getValley())) {
				return false;
			}
		}
		return true;
	}

	private boolean qpsAckAlarm(ConsumerServerStatsData serverStatsData, QPSAlarmSetting qps) {
		if (qps != null) {
			if (!serverStatsData.checkAckQpsPeak(qps.getPeak())) {
				return false;
			}
			if (!serverStatsData.checkAckQpsValley(qps.getValley())) {
				return false;
			}
		}
		return true;
	}

}
