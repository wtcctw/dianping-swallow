package com.dianping.swallow.web.storager;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
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

/**
 *
 * @author qiyin
 *
 */
@Component
public class ConsumerStatisStorager extends AbstractStatisStorager implements MonitorDataListener {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerDataWapper consumerDataWapper;

	@Autowired
	private ConsumerMachineStatisDataService machineStatisDataService;

	@Autowired
	private ConsumerTopicStatisDataService topicStatisDataService;

	@Autowired
	private ConsumerIdStatisDataService consumerIdStatisDataService;

	public ConsumerStatisStorager() {
		storageType = getClass().getSimpleName();
	}

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
			dataCount.decrementAndGet();
			ConsumerServerStatsData serverStatisData = consumerDataWapper.getServerStatsData(lastTimeKey.get());
			if (serverStatisData != null && serverStatisData.getTimeKey() != 0L) {
				logger.info("[doStorage] timeKey = " + serverStatisData.getTimeKey());
				lastTimeKey.set(serverStatisData.getTimeKey());
				List<ConsumerTopicStatsData> topicStatisDatas = consumerDataWapper.getTopicStatsData(lastTimeKey.get());
				Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap = consumerDataWapper
						.getConsumerIdStatsData(lastTimeKey.get());
				storageServerStatis(serverStatisData);
				storageTopicStatis(topicStatisDatas);
				storageConsumerIdStatis(consumerIdStatsDataMap);
			}
		}
	}

	private void storageServerStatis(final ConsumerServerStatsData serverStatisData) {
		logger.info("[storageServerStatis]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageServerStatis");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (serverStatisData == null || serverStatisData.getMachineStatisDatas() == null) {
					return;
				}
				for (ConsumerMachineStatsData consumerMachineStatsData : serverStatisData.getMachineStatisDatas()) {
					machineStatisDataService.insert(consumerMachineStatsData);
				}
			}
		});

	}

	private void storageTopicStatis(final List<ConsumerTopicStatsData> topicStatisDatas) {
		logger.info("[storageTopicStatis]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageTopicStatis");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (topicStatisDatas != null) {
					for (ConsumerTopicStatsData consumerTopicStatisData : topicStatisDatas) {
						topicStatisDataService.insert(consumerTopicStatisData);
					}
				}
			}
		});
	}

	private void storageConsumerIdStatis(final Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap) {
		logger.info("[storageConsumerIdStatis]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageConsumerIdStatis");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (consumerIdStatsDataMap == null) {
					return;
				}
				for (Map.Entry<String, List<ConsumerIdStatsData>> consumerIdStatsDataEntry : consumerIdStatsDataMap
						.entrySet()) {
					List<ConsumerIdStatsData> consumerIdStatsDatas = consumerIdStatsDataEntry.getValue();
					if (consumerIdStatsDatas == null) {
						continue;
					}
					for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
						consumerIdStatisDataService.insert(consumerIdStatsData);
					}
				}
			}
		});
	}

}
