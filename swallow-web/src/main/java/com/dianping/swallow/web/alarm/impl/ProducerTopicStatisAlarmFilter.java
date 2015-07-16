package com.dianping.swallow.web.alarm.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.model.statis.ProducerBaseStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerDataWapper;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerTopicStatisDataService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("producerTopicStatisAlarmFilter")
public class ProducerTopicStatisAlarmFilter extends AbstractStatisAlarmFilter implements MonitorDataListener {

	@Autowired
	private AlarmManager alarmManager;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerDataWapper producerDataWapper;

	private List<ProducerTopicStatsData> topicStatisDatas;

	@Autowired
	private ProducerTopicStatisDataService topicStatisDataService;

	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;

	@Autowired
	private ProducerServerAlarmSettingService serverAlarmSettingService;

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
			topicStatisDatas = producerDataWapper.getTopicStatsDatas(lastTimeKey.get());
			return topicAlarm();
		}
		return true;
	}

	public boolean topicAlarm() {
		TopicAlarmSetting topicAlarmSetting = topicAlarmSettingService.findOne();
		if (topicAlarmSetting == null || topicAlarmSetting.getProducerAlarmSetting() == null) {
			return true;
		}
		ProducerBaseAlarmSetting producerAlarmSetting = topicAlarmSetting.getProducerAlarmSetting();
		QPSAlarmSetting qps = producerAlarmSetting.getQpsAlarmSetting();
		List<String> whiteList = serverAlarmSettingService.getTopicWhiteList();
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
				qpsAlarm(producerBaseStatisData.getQpx(), topicStatisData.getTopicName(), qps,
						topicStatisData.getTimeKey());
				delayAlarm(topicStatisData.getTopicName(), producerBaseStatisData.getDelay(), delay);
			}
		}
		return true;

	}

	private boolean qpsAlarm(long qpx, String topicName, QPSAlarmSetting qps, long timeKey) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				alarmManager.producerTopicStatisQpsPAlarm(topicName, qpx, qps.getPeak());
				return false;
			}
			if (qpx < qps.getValley()) {
				alarmManager.producerTopicStatisQpsVAlarm(topicName, qpx, qps.getValley());
				return false;
			}
			fluctuationAlarm(topicName, qpx, qps.getFluctuation(), timeKey);
		}

		return true;

	}

	private boolean fluctuationAlarm(String topicName, long qpx, int fluctuation, long timeKey) {
		long preDayTimeKey = getPreDayKey(timeKey);
		List<ProducerTopicStatsData> topicStatsDatas = topicStatisDataService.findSectionData(topicName, preDayTimeKey
				- getTimeSection(), preDayTimeKey + getTimeSection());
		int sampleCount = 0;
		int sumQpx = 0;
		if (topicStatsDatas == null || topicStatsDatas.size() == 0) {
			return true;
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
			return true;
		}
		int expectedQpx = sumQpx / sampleCount;
		if (qpx > expectedQpx && (qpx / expectedQpx) > fluctuation) {
			alarmManager.producerTopicStatisQpsFAlarm(topicName, qpx, expectedQpx);
			return false;
		}
		if (qpx < expectedQpx && (expectedQpx / qpx) > fluctuation) {
			alarmManager.producerTopicStatisQpsFAlarm(topicName, qpx, expectedQpx);
			return false;
		}
		return true;
	}

	private boolean delayAlarm(String topicName, long delay, long expectDelay) {
		if (delay > expectDelay) {
			alarmManager.producerTopicStatisQpsDAlarm(topicName, delay, expectDelay);
			return false;
		}
		return true;
	}

}
