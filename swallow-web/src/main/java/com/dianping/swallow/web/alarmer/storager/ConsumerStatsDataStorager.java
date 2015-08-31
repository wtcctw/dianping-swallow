package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月4日 下午1:22:31
 */
@Component
public class ConsumerStatsDataStorager extends AbstractStatsDataStorager implements MonitorDataListener {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ConsumerServerStatsDataService serverStatsDataService;

	@Autowired
	private ConsumerTopicStatsDataService topicStatsDataService;

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
		List<ConsumerIdStatsData> consumerIdStatsDatas = consumerStatsDataWapper.getConsumerIdStatsDatas(lastTimeKey
				.get());
		ConsumerTopicStatsData topicStatsData = consumerStatsDataWapper.getTotalTopicStatsData(lastTimeKey.get());
		storageServerStatis(serverStatisDatas);
		storageTopicStatis(topicStatsData);
		storageConsumerIdStatis(consumerIdStatsDatas);
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
				boolean isFirstTime = true;
				for (ConsumerServerStatsData serverStatsData : serverStatsDatas) {
					if (isFirstTime) {
						lastTimeKey.set(serverStatsData.getTimeKey());
					}
					serverStatsDataService.insert(serverStatsData);
				}
			}
		});

	}

	private void storageTopicStatis(final ConsumerTopicStatsData topicStatsData) {
		logger.info("[storageTopicStatis]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageTopicStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (topicStatsData == null) {
					return;
				}
				topicStatsDataService.insert(topicStatsData);
			}
		});

	}

	private void storageConsumerIdStatis(final List<ConsumerIdStatsData> consumerIdStatsDatas) {
		logger.info("[storageConsumerIdStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageConsumerIdStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (consumerIdStatsDatas == null) {
					return;
				}

				for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
					consumerIdStatsDataService.insert(consumerIdStatsData);
				}

			}
		});
	}
}
