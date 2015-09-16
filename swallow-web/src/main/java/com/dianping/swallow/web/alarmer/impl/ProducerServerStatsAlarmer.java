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
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:54
 */
@Component
public class ProducerServerStatsAlarmer extends AbstractStatsAlarmer {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private ProducerServerStatsDataService serverStatsDataService;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void doAlarm() {
		final List<ProducerServerStatsData> serverStatsDatas = producerStatsDataWapper.getServerStatsDatas(
				getLastTimeKey(), false);
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
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
		for (ProducerServerStatsData serverStatsData : serverStatsDatas) {
			String ip = serverStatsData.getIp();
			ProducerServerResource pServerResource = resourceContainer.findProducerServerResource(ip);
			if (pServerResource == null || !pServerResource.isAlarm() || StringUtils.equals(TOTAL_KEY, ip)) {
				continue;
			}
			QPSAlarmSetting qps = pServerResource.getSaveAlarmSetting();
			qpsAlarm(serverStatsData, qps);
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
