package com.dianping.swallow.web.alarmer.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;
import com.dianping.swallow.common.server.monitor.data.statis.ProducerServerStatisData;
import com.dianping.swallow.web.alarmer.ProducerStatisAlarmer;
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.model.statis.ProducerBaseStatsData;
import com.dianping.swallow.web.model.statis.ProducerMachineStatsData;
import com.dianping.swallow.web.model.statis.ProducerServerStatsData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.impl.DefaultProducerDataRetriever;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerStatisDataService;
import com.dianping.swallow.web.service.ProducerTopicStatisDataService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
public class DefaultProducerStatisAlarmer extends AbstractStatisAlarmer implements ProducerStatisAlarmer,
		MonitorDataListener {

	private static final Logger logger = LoggerFactory.getLogger(DefaultProducerStatisAlarmer.class);

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerServerStatisDataService producerServerStatisDataService;

	@Autowired
	private ProducerServerAlarmSettingService producerServerAlarmSettingService;

	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;

	@Autowired
	private ProducerTopicStatisDataService producerTopicStatisDataService;

	private volatile ProducerServerStatsData serverStatisData;

	private volatile List<ProducerTopicStatsData> producerTopicStatisDatas;

	private volatile AtomicLong dataCount;

	private volatile AtomicLong lastTimeKey;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
		dataCount.set(0);
		lastTimeKey.set(-1);
	}

	@Override
	protected void doAlarm() {
		if (dataCount.getAndDecrement() > 0) {
			serverStatisData = producerDataRetriever.getServerStatis(lastTimeKey.get(), StatisType.SAVE);
			producerTopicStatisDatas = producerDataRetriever.getTopicStatis(lastTimeKey.get(), StatisType.SAVE);
			if (serverStatisData == null) {
				return;
			}
			lastTimeKey.set(serverStatisData.getTimeKey());
			doServerAlarm();
			doTopicAlarm();
			storageServerStatis();
			storageTopicStatis();
		}
	}

	public void doServerAlarm() {
		List<ProducerServerAlarmSetting> producerServerAlarmSettings = producerServerAlarmSettingService.findAll();
		ProducerServerAlarmSetting producerServerAlarmSetting = null;
		if (producerServerAlarmSettings != null && producerServerAlarmSettings.size() > 0) {
			producerServerAlarmSetting = producerServerAlarmSettings.get(0);
		}
		if (producerServerAlarmSetting == null) {
			return;
		}
		QPSAlarmSetting qps = producerServerAlarmSetting.getDefaultAlarmSetting();
		List<String> whiteList = producerServerAlarmSetting.getWhiteList();

		if (qps == null || serverStatisData == null || serverStatisData.getStatisDatas() == null) {
			return;
		}
		List<ProducerMachineStatsData> machineStatisDatas = serverStatisData.getStatisDatas();
		if (machineStatisDatas == null || machineStatisDatas.size() == 0) {
			return;
		}

		for (ProducerMachineStatsData machineStatisData : machineStatisDatas) {
			ProducerBaseStatsData baseStatisData = machineStatisData.getStatisData();
			if (whiteList == null || (!whiteList.contains(machineStatisData.getIp()) && baseStatisData != null)) {
				long qpx = baseStatisData.getQpx();
				if (qpx > qps.getPeak() || qpx < qps.getValley()) {
					// alram
				}
			}
		}
	}

	public void doTopicAlarm() {
		List<TopicAlarmSetting> topicAlarmSettings = topicAlarmSettingService.findAll();
		TopicAlarmSetting topicAlarmSetting = null;
		if (topicAlarmSettings != null && topicAlarmSettings.size() > 0) {
			topicAlarmSetting = topicAlarmSettings.get(0);
		}
		if (topicAlarmSetting == null || topicAlarmSetting.getProducerAlarmSetting() == null) {
			return;
		}
		ProducerBaseAlarmSetting producerAlarmSetting = topicAlarmSetting.getProducerAlarmSetting();
		QPSAlarmSetting qps = producerAlarmSetting.getQpsAlarmSetting();
		List<String> whiteList = topicAlarmSetting.getWhiteList();
		long delay = producerAlarmSetting.getDelay();
		if (producerTopicStatisDatas == null) {
			return;
		}
		for (ProducerTopicStatsData producerTopicStatisData : producerTopicStatisDatas) {
			if (whiteList == null || !whiteList.contains(producerTopicStatisData.getTopicName())) {
				ProducerBaseStatsData producerBaseStatisData = producerTopicStatisData.getProducerStatisData();
				if (producerBaseStatisData == null) {
					continue;
				}
				if (qps != null) {
					qpsAlarm(producerBaseStatisData.getQpx(), qps.getPeak(), qps.getValley());
					fluctuationAlarm(producerBaseStatisData.getQpx(), qps.getFluctuation(),
							producerTopicStatisData.getTimeKey());
				}
				delayAlarm(delay, producerAlarmSetting.getDelay());
			}
		}

	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	private void storageServerStatis() {
		if (serverStatisData != null) {
			producerServerStatisDataService.insert(serverStatisData);
		}
	}

	private void storageTopicStatis() {
		if (producerTopicStatisDatas != null) {
			for (ProducerTopicStatsData producerTopicStatisData : producerTopicStatisDatas)
				producerTopicStatisDataService.insert(producerTopicStatisData);
		}
	}

	private void qpsAlarm(long qpx, long peak, long valley) {
		if (qpx > peak || qpx < valley) {
			// alarm
		}

	}

	private void fluctuationAlarm(long qpx, long fluctuation, long timeKey) {
		List<ProducerTopicStatsData> topicStatsDatas = producerTopicStatisDataService.findSectionData(timeKey
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
