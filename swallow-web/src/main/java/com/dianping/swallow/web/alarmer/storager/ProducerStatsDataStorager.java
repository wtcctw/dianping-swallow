package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.alarmer.container.StatsDataContainer;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月4日 下午1:22:31
 */
@Component
public class ProducerStatsDataStorager extends AbstractStatsDataStorager {

	@Autowired
	private StatsDataContainer statsDataContainer;

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
	protected void doStorage() {
		List<ProducerServerStatsData> serverStatsDatas = producerStatsDataWapper.getServerStatsDatas(lastTimeKey.get(),
				true);
		List<ProducerTopicStatsData> topicStatsDatas = producerStatsDataWapper.getTopicStatsDatas(lastTimeKey.get(),
				true);
		storageServerStatis(serverStatsDatas);
		storageTopicStatis(topicStatsDatas);
	}

	private void storageServerStatis(final List<ProducerServerStatsData> serverStatsDatas) {
		logger.info("[storageServerStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageServerStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (serverStatsDatas != null) {
					boolean isFirstTime = true;
					for (ProducerServerStatsData serverStatsData : serverStatsDatas) {
						if (isFirstTime) {
							lastTimeKey.set(serverStatsData.getTimeKey());
							isFirstTime = false;
						}
						serverStatsDataService.insert(serverStatsData);
					}
				}
			}
		});
	}

	private void storageTopicStatis(final List<ProducerTopicStatsData> topicStatsDatas) {
		logger.info("[storageTopicStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "storageTopicStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (topicStatsDatas != null) {

					statsDataContainer.setProducerTopicTotalRatio(topicStatsDatas);

					for (ProducerTopicStatsData producerTopicStatisData : topicStatsDatas) {
						topicStatsDataService.insert(producerTopicStatisData);
					}
				}
			}
		});
	}

}
