package com.dianping.swallow.web.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.stats.ProducerServerStatsDataService;
import com.dianping.swallow.web.service.stats.ProducerTopicStatsDataService;

public class ProducerStatsDataStorager extends AbstractStatsDataStorager implements MonitorDataListener {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;

	@Autowired
	private ProducerServerStatsDataService serverStatsDataService;

	@Autowired
	private ProducerTopicStatsDataService topicStatsDataService;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
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
		List<ProducerServerStatsData> serverStatsDatas = producerStatsDataWapper.getServerStatsDatas(lastTimeKey.get());
		List<ProducerTopicStatsData> topicStatisDatas = producerStatsDataWapper.getTopicStatsDatas(lastTimeKey.get());
		storageServerStatis(serverStatsDatas);
		storageTopicStatis(topicStatisDatas);
	}

	private void storageServerStatis(final List<ProducerServerStatsData> serverStatsDatas) {
		logger.info("[storageServerStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageServerStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (serverStatsDatas != null) {
					for (ProducerServerStatsData serverStatsData : serverStatsDatas) {
						lastTimeKey.set(serverStatsData.getTimeKey());
						serverStatsDataService.insert(serverStatsData);
					}
				}
			}
		});
	}

	private void storageTopicStatis(final List<ProducerTopicStatsData> topicStatisDatas) {
		logger.info("[storageTopicStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageTopicStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (topicStatisDatas != null) {
					for (ProducerTopicStatsData producerTopicStatisData : topicStatisDatas) {
						topicStatsDataService.insert(producerTopicStatisData);
					}
				}
			}
		});
	}

}
