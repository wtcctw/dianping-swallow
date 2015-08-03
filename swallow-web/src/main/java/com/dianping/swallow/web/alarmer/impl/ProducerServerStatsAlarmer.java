package com.dianping.swallow.web.alarmer.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.GlobalAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.stats.ProducerServerStatsDataService;
/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午6:06:54
 */
@Component
public class ProducerServerStatsAlarmer extends AbstractStatsAlarmer implements MonitorDataListener {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private ProducerServerStatsDataService serverStatsDataService;

	@Autowired
	private GlobalAlarmSettingService globalAlarmSettingService;

	@Autowired
	private ProducerServerAlarmSettingService serverAlarmSettingService;

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}
	
	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void doAlarm() {
		if (dataCount.get() <= 0) {
			return;
		}
		dataCount.decrementAndGet();
		final List<ProducerServerStatsData> serverStatsDatas = producerStatsDataWapper.getServerStatsDatas(lastTimeKey
				.get());
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "doAlarm");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				serverAlarm(serverStatsDatas);
			}
		});
	}

	private void serverAlarm(List<ProducerServerStatsData> serverStatsDatas) {
		if (serverStatsDatas == null) {
			return;
		}
		ProducerServerAlarmSetting serverAlarmSetting = serverAlarmSettingService.findDefault();
		if (serverAlarmSetting == null) {
			return;
		}
		QPSAlarmSetting qps = serverAlarmSetting.getAlarmSetting();
		List<String> whiteList = globalAlarmSettingService.getProducerWhiteList();
		for (ProducerServerStatsData serverStatsData : serverStatsDatas) {
			if (whiteList == null || (!whiteList.contains(serverStatsData.getIp()))) {
				qpsAlarm(serverStatsData, qps);
			}
		}
	}

	private boolean qpsAlarm(ProducerServerStatsData serverStatsData, QPSAlarmSetting qps) {
		if (qps != null) {
			if (!serverStatsData.checkQpsPeak(qps.getPeak())) {
				return false;
			}
			if (!serverStatsData.checkQpsValley(qps.getValley())) {
				return false;
			}
		}
		return true;
	}

}
