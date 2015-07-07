package com.dianping.swallow.web.alarmer.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.AbstractAllData;
import com.dianping.swallow.web.alarmer.ProducerStatisAlarmer;
import com.dianping.swallow.web.model.statis.ProducerBaseStatisData;
import com.dianping.swallow.web.model.statis.ProducerServerMachineStatisData;
import com.dianping.swallow.web.model.statis.ProducerServerStatisData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.impl.DefaultProducerDataRetriever;
import com.dianping.swallow.web.service.ProducerServerAlarmSettingService;
import com.dianping.swallow.web.service.ProducerServerStatisDataService;
import com.dianping.swallow.web.service.SwallowAlarmSettingService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;
import com.dianping.swallow.web.service.TopicStatisDataService;

/**
 *
 * @author qiyin
 *
 */
public class DefaultProducerStatisAlarmer extends AbstractStatisAlarmer implements ProducerStatisAlarmer,
		MonitorDataListener {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerServerStatisDataService producerServerStatisDataService;

	@Autowired
	private ProducerServerAlarmSettingService producerServerAlarmSettingService;

	@Autowired
	private SwallowAlarmSettingService swallowAlarmSettingService;

	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;

	@Autowired
	private TopicStatisDataService topicStatisDataService;

	@SuppressWarnings("rawtypes")
	private volatile AbstractAllData allData;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}

	@Override
	protected void doAlarm() {

	}

	public void doServerQpsAlarm() {
		Map<String, StatsData> serverQpxs = producerDataRetriever.getServerQpx(QPX.SECOND);
		for (Map.Entry<String, StatsData> serverQpx : serverQpxs.entrySet()) {

		}
	}

	public void doTopicQpsAlarm() {
		Set<String> topics = producerDataRetriever.getTopics();
		@SuppressWarnings("rawtypes")
		Iterator itetator = topics.iterator();
		while (itetator.hasNext()) {
			String topic = String.valueOf(itetator.next());
			producerDataRetriever.getQpx(topic, QPX.SECOND);
		}
	}

	public void doIPQpsAlarm() {

	}

	public void doServerDelayAlarm() {

	}

	public void doTopicDelayAlarm() {

	}

	@Override
	public void achieveMonitorData() {
		allData = producerDataRetriever.getAlldata();
	}

	private void storageServerStatis() {
		@SuppressWarnings("unchecked")
		Map<String, NavigableMap<Long, Long>> qpxForServers = allData.getQpxForServers(StatisType.SAVE);
		if (qpxForServers != null) {
			ProducerServerStatisData statisData = new ProducerServerStatisData();
			List<ProducerServerMachineStatisData> machineStatisDatas = new ArrayList<ProducerServerMachineStatisData>();
			for (Map.Entry<String, NavigableMap<Long, Long>> statis : qpxForServers.entrySet()) {
				String serverIp = statis.getKey();
				Long timekey = statis.getValue().floorKey(
						DefaultProducerDataRetriever.getKey(System.currentTimeMillis()));
				statisData.setTimeKey(timekey);
				ProducerServerMachineStatisData machineStatisData = new ProducerServerMachineStatisData();
				machineStatisData.setIp(serverIp);
				ProducerBaseStatisData baseStatisData = new ProducerBaseStatisData();
				baseStatisData.setDelay(0);
				baseStatisData.setQpx(statis.getValue().get(timekey));
				machineStatisData.setStatisData(baseStatisData);
			}
			statisData.setStatisDatas(machineStatisDatas);
			producerServerStatisDataService.insert(statisData);
		}
	}

	private void storageTopicStatis() {
		@SuppressWarnings("unchecked")
		Set<String> topics = allData.getTopics(true);
		if (topics != null) {
			Iterator iterator = topics.iterator();
			while(iterator.hasNext()){
				String topic = String.valueOf(iterator.next());
				
			}
		}
	}
}
