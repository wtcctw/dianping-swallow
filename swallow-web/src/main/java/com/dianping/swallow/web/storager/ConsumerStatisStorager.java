package com.dianping.swallow.web.storager;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.model.statis.ConsumerIdStatsData;
import com.dianping.swallow.web.model.statis.ConsumerMachineStatsData;
import com.dianping.swallow.web.model.statis.ConsumerServerStatsData;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerDataWapper;
import com.dianping.swallow.web.service.ConsumerIdStatisDataService;
import com.dianping.swallow.web.service.ConsumerMachineStatisDataService;
import com.dianping.swallow.web.service.ConsumerTopicStatisDataService;

public class ConsumerStatisStorager extends AbstractStatisStorager implements MonitorDataListener {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerDataWapper consumerDataWapper;

	private volatile ConsumerServerStatsData serverStatisData;

	private volatile List<ConsumerTopicStatsData> topicStatisDatas;

	private volatile Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap;

	@Autowired
	private ConsumerMachineStatisDataService machineStatisDataService;

	@Autowired
	private ConsumerTopicStatisDataService topicStatisDataService;

	@Autowired
	private ConsumerIdStatisDataService consumerIdStatisDataService;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	public void doStorage() {
		if (dataCount.get() > 0) {
			dataCount.incrementAndGet();
			serverStatisData = consumerDataWapper.getServerStatsData(lastTimeKey.get());
			topicStatisDatas = consumerDataWapper.getTopicStatsData(lastTimeKey.get());
			consumerIdStatsDataMap = consumerDataWapper.getConsumerIdStatsData(lastTimeKey.get());
			storageServerStatis();
			storageTopicStatis();
			storageConsumerIdStatis();
		}
	}

	private void storageServerStatis() {
		if (serverStatisData != null) {
			return;
		}
		for (ConsumerMachineStatsData consumerMachineStatsData : serverStatisData.getMachineStatisDatas()) {
			machineStatisDataService.insert(consumerMachineStatsData);
		}
	}

	private void storageTopicStatis() {
		if (topicStatisDatas != null) {
			for (ConsumerTopicStatsData consumerTopicStatisData : topicStatisDatas)
				topicStatisDataService.insert(consumerTopicStatisData);
		}
	}

	private void storageConsumerIdStatis() {
		for (Map.Entry<String, List<ConsumerIdStatsData>> consumerIdStatsDataEntry : consumerIdStatsDataMap.entrySet()) {
			List<ConsumerIdStatsData> consumerIdStatsDatas = consumerIdStatsDataEntry.getValue();
			if (consumerIdStatsDatas == null) {
				continue;
			}
			for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
				consumerIdStatisDataService.insert(consumerIdStatsData);
			}
		}
	}

}
