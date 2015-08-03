package com.dianping.swallow.web.storager;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.stats.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.stats.ConsumerServerStatsDataService;

public class ConsumerStatsDataStorager extends AbstractStatsDataStorager implements MonitorDataListener {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ConsumerServerStatsDataService serverStatsDataService;

	@Autowired
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
		storagerName = getClass().getSimpleName();
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	protected void doStorage() {
		if (dataCount.get() <= 0) {
			return;
		}
		dataCount.decrementAndGet();
		List<ConsumerServerStatsData> serverStatisDatas = consumerStatsDataWapper
				.getServerStatsDatas(lastTimeKey.get());
		Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap = consumerStatsDataWapper
				.getConsumerIdStatsDatas(lastTimeKey.get());
		storageServerStatis(serverStatisDatas);
		storageConsumerIdStatis(consumerIdStatsDataMap);
	}

	private void storageServerStatis(final List<ConsumerServerStatsData> serverStatsDatas) {
		logger.info("[storageServerStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageServerStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (serverStatsDatas == null) {
					return;
				}
				for (ConsumerServerStatsData serverStatsData : serverStatsDatas) {
					lastTimeKey.set(serverStatsData.getTimeKey());
					serverStatsDataService.insert(serverStatsData);
				}
			}
		});

	}

	private void storageConsumerIdStatis(final Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap) {
		logger.info("[storageConsumerIdStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageConsumerIdStats");
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
						consumerIdStatsDataService.insert(consumerIdStatsData);
					}
				}
			}
		});
	}
}
