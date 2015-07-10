package com.dianping.swallow.web.alarmer.impl;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.model.statis.ProducerBaseStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerDataWapper;
import com.dianping.swallow.web.service.ProducerTopicStatisDataService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
*
* @author qiyin
*
*/
public class ProducerTopicStatisAlarmFilter extends AbstractProducerAlarmFilter implements MonitorDataListener {

	private volatile AtomicLong dataCount;

	private static final int INIT_VALUE = 0;
	private static final long DEFAULT_VALUE = -1L;

	private volatile AtomicLong lastTimeKey;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerDataWapper producerDataWapper;

	private volatile List<ProducerTopicStatsData> topicStatisDatas;

	@Autowired
	private ProducerTopicStatisDataService topicStatisDataService;

	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;

	@PostConstruct
	public void initialize() {
		dataCount.set(INIT_VALUE);
		lastTimeKey.set(DEFAULT_VALUE);
		producerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
		topicStatisDatas = producerDataWapper.getTopicStatsDatas(lastTimeKey.get());
		storageTopicStatis();
	}

	@Override
	public boolean doAccept() {
		return dataCount.getAndDecrement() > 0 ? topicAlarm() : true;
	}

	private void storageTopicStatis() {
		if (topicStatisDatas != null) {
			for (ProducerTopicStatsData producerTopicStatisData : topicStatisDatas)
				topicStatisDataService.insert(producerTopicStatisData);
		}
	}

	public boolean topicAlarm() {
		TopicAlarmSetting topicAlarmSetting = topicAlarmSettingService.findOne();
		if (topicAlarmSetting == null || topicAlarmSetting.getProducerAlarmSetting() == null) {
			return true;
		}
		ProducerBaseAlarmSetting producerAlarmSetting = topicAlarmSetting.getProducerAlarmSetting();
		QPSAlarmSetting qps = producerAlarmSetting.getQpsAlarmSetting();
		List<String> whiteList = topicAlarmSetting.getWhiteList();
		long delay = producerAlarmSetting.getDelay();
		if (topicStatisDatas == null) {
			return true;
		}
		for (ProducerTopicStatsData topicStatisData : topicStatisDatas) {
			if (whiteList == null || !whiteList.contains(topicStatisData.getTopicName())) {
				ProducerBaseStatsData producerBaseStatisData = topicStatisData.getProducerStatisData();
				if (producerBaseStatisData == null) {
					continue;
				}
				if (qps != null) {
					qpsAlarm(producerBaseStatisData.getQpx(), qps.getPeak(), qps.getValley());
					fluctuationAlarm(producerBaseStatisData.getQpx(), qps.getFluctuation(),
							topicStatisData.getTimeKey());
				}
				delayAlarm(delay, producerAlarmSetting.getDelay());
			}
		}
		return true;

	}

	private void qpsAlarm(long qpx, long peak, long valley) {
		if (qpx > peak || qpx < valley) {
			// alarm
		}

	}

	private void fluctuationAlarm(long qpx, long fluctuation, long timeKey) {
		List<ProducerTopicStatsData> topicStatsDatas = topicStatisDataService.findSectionData(timeKey
				- getTimeSection(), timeKey + getTimeSection());
		int sampleCount = 0;
		int sumQpx = 0;
		if (topicStatsDatas == null || topicStatsDatas.size() == 0) {
			return;
		}
		for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
			if (topicStatsData == null || topicStatsData.getProducerStatisData() == null
					|| topicStatsData.getProducerStatisData().getQpx() == 0) {
				continue;
			}
			sumQpx += topicStatsData.getProducerStatisData().getQpx();
			sampleCount++;
		}
		if (sampleCount == 0) {
			return;
		}
		if (Math.abs(qpx - sumQpx / sampleCount) > fluctuation) {
			// alarm
		}
	}

	private void delayAlarm(long delay, long expectDelay) {
		if (delay > expectDelay) {
			// alarm
		}
	}

}
