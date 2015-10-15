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
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:07:16
 */
@Component
public class ProducerTopicStatsAlarmer extends AbstractStatsAlarmer {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private ProducerTopicStatsDataService topicStatsDataService;

	@Autowired
	private AlarmResourceContainer resourceContainer;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void doAlarm() {
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName() + FUNCTION_DOALARM);
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				topicAlarm();
			}
		});
	}

	private void topicAlarm() {
		List<ProducerTopicStatsData> topicStatsDatas = producerStatsDataWapper.getTopicStatsDatas(getLastTimeKey(),
				false);
		if (topicStatsDatas == null || topicStatsDatas.isEmpty()) {
			return;
		}
		for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
			try {
				String topicName = topicStatsData.getTopicName();
				TopicResource topicResource = resourceContainer.findTopicResource(topicName);
				if (topicResource == null || !topicResource.isProducerAlarm()
						|| StringUtils.equals(TOTAL_KEY, topicName)) {
					continue;
				}
				ProducerBaseAlarmSetting pBaseAlarmSetting = topicResource.getProducerAlarmSetting();
				if (pBaseAlarmSetting == null) {
					continue;
				}
				QPSAlarmSetting qps = pBaseAlarmSetting.getQpsAlarmSetting();
				long delay = pBaseAlarmSetting.getDelay();
				qpsAlarm(topicStatsData, qps);
				topicStatsData.checkDelay(delay);
			} catch (Exception e) {
				logger.error("[topicAlarm] topicStatsData {} error.", topicStatsData);
			}
		}
	}

	public boolean qpsAlarm(ProducerTopicStatsData topicStatsData, QPSAlarmSetting qps) {
		if (qps != null) {
			if (!topicStatsData.checkQpsPeak(qps.getPeak())) {
				return false;
			}
			if (!topicStatsData.checkQpsValley(qps.getValley())) {
				return false;
			}
			long preQps = getExpectQps(topicStatsData.getTopicName(), topicStatsData.getTimeKey());
			if (!topicStatsData.checkQpsFlu(qps.getFluctuationBase(), preQps, qps.getFluctuation())) {
				return false;
			}
		}
		return true;
	}

	public long getExpectQps(String topicName, long timeKey) {
		long preDayTimeKey = getPreDayKey(timeKey);
		long startKey = preDayTimeKey - getTimeSection();
		long endKey = preDayTimeKey + getTimeSection();
		List<ProducerTopicStatsData> topicStatsDatas = topicStatsDataService.findSectionData(topicName, startKey,
				endKey);
		int dataCount = 0;
		long sumQps = 0L;
		if (topicStatsDatas != null) {
			for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
				if (topicStatsData.getQps() > 0L) {
					sumQps += topicStatsData.getQps();
					dataCount++;
				}
			}
			if (dataCount > 0) {
				return sumQps / dataCount;
			}
		}
		return 0L;
	}
}
