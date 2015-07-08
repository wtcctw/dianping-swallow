package com.dianping.swallow.web.alarmer.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.web.alarmer.ProducerStatisAlarmer;
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ProducerServerAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.model.statis.ProducerBaseStatisData;
import com.dianping.swallow.web.model.statis.ProducerMachineStatisData;
import com.dianping.swallow.web.model.statis.ProducerServerStatisData;
import com.dianping.swallow.web.model.statis.ProducerTopicStatisData;
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
	private ProducerTopicStatisDataService producertopicStatisDataService;

	private volatile ProducerServerStatisData serverStatisData;

	private volatile List<ProducerTopicStatisData> producerTopicStatisDatas;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	protected void doAlarm() {
		doServerAlarm();
		doTopicAlarm();
		storageServerStatis();
		storageTopicStatis();
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
		List<ProducerMachineStatisData> machineStatisDatas = serverStatisData.getStatisDatas();
		if (machineStatisDatas == null || machineStatisDatas.size() == 0) {
			return;
		}

		for (ProducerMachineStatisData machineStatisData : machineStatisDatas) {
			ProducerBaseStatisData baseStatisData = machineStatisData.getStatisData();
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
		for (ProducerTopicStatisData producerTopicStatisData : producerTopicStatisDatas) {
			if (whiteList == null || !whiteList.contains(producerTopicStatisData.getTopicName())) {
				ProducerBaseStatisData producerBaseStatisData = producerTopicStatisData.getProducerStatisData();
				if (producerBaseStatisData == null) {
					continue;
				}
				if (qps != null) {
					if (producerBaseStatisData.getQpx() > qps.getPeak()
							|| producerBaseStatisData.getQpx() < qps.getValley()) {
						// alarm
					}
				}
				if (producerBaseStatisData.getDelay() > delay) {
					// alarm
				}

			}
		}

	}

	@Override
	public void achieveMonitorData() {
		try {
			@SuppressWarnings("rawtypes")
			AbstractAllData allData = producerDataRetriever.getAlldata();
			achieveServerStatis(allData);
			achieveTopicStatis(allData);
		} catch (Exception e) {
			logger.error("achieve monitor Data exception", e);
		}
	}

	private void achieveServerStatis(@SuppressWarnings("rawtypes") AbstractAllData allData) {
		@SuppressWarnings("unchecked")
		Map<String, NavigableMap<Long, Long>> qpxForServers = allData.getQpxForServers(StatisType.SAVE);
		if (qpxForServers != null) {
			ProducerServerStatisData serverStatisDataTemp = new ProducerServerStatisData();
			List<ProducerMachineStatisData> machineStatisDatas = new ArrayList<ProducerMachineStatisData>();
			for (Map.Entry<String, NavigableMap<Long, Long>> statis : qpxForServers.entrySet()) {
				String serverIp = statis.getKey();
				Long timekey = statis.getValue().floorKey(
						DefaultProducerDataRetriever.getKey(System.currentTimeMillis()));
				if (timekey == null) {
					continue;
				}
				serverStatisDataTemp.setTimeKey(timekey);
				ProducerMachineStatisData machineStatisData = new ProducerMachineStatisData();
				machineStatisData.setIp(serverIp);
				ProducerBaseStatisData baseStatisData = new ProducerBaseStatisData();
				baseStatisData.setDelay(0);
				baseStatisData.setQpx(statis.getValue().get(timekey));
				machineStatisData.setStatisData(baseStatisData);
				machineStatisDatas.add(machineStatisData);
			}
			serverStatisDataTemp.setStatisDatas(machineStatisDatas);
			this.serverStatisData = serverStatisDataTemp;
		}
	}

	private void achieveTopicStatis(@SuppressWarnings("rawtypes") AbstractAllData allData) {
		List<ProducerTopicStatisData> producerTopicStatisDataTemps = new ArrayList<ProducerTopicStatisData>();
		@SuppressWarnings("unchecked")
		Set<String> topics = allData.getTopics(true);
		if (topics != null) {
			@SuppressWarnings("rawtypes")
			Iterator iterator = topics.iterator();
			while (iterator.hasNext()) {
				ProducerTopicStatisData producerTopicStatisData = new ProducerTopicStatisData();
				String topicName = String.valueOf(iterator.next());
				producerTopicStatisData.setTopicName(topicName);
				@SuppressWarnings("unchecked")
				NavigableMap<Long, Long> topicQpxs = allData.getQpxForTopic(topicName, StatisType.SAVE);
				Long timekey = topicQpxs.floorKey(DefaultProducerDataRetriever.getKey(System.currentTimeMillis()));
				if (timekey == null) {
					continue;
				}
				producerTopicStatisData.setTimeKey(timekey);
				@SuppressWarnings("unchecked")
				NavigableMap<Long, Long> topicDelays = allData.getDelayForTopic(topicName, StatisType.SAVE);
				ProducerBaseStatisData producerBaseStatisData = new ProducerBaseStatisData();
				producerBaseStatisData.setQpx(topicQpxs.get(timekey));
				producerBaseStatisData.setDelay(topicDelays.get(timekey));
				producerTopicStatisData.setProducerStatisData(producerBaseStatisData);
				producerTopicStatisDataTemps.add(producerTopicStatisData);
			}
			this.producerTopicStatisDatas = producerTopicStatisDataTemps;
		}
	}

	private void storageServerStatis() {
		if (serverStatisData != null) {
			producerServerStatisDataService.insert(serverStatisData);
		}
	}

	private void storageTopicStatis() {
		if (producerTopicStatisDatas != null) {
			for (ProducerTopicStatisData producerTopicStatisData : producerTopicStatisDatas)
				producertopicStatisDataService.insert(producerTopicStatisData);
		}

	}
}
