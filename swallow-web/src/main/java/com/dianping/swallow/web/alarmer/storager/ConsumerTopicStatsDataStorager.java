package com.dianping.swallow.web.alarmer.storager;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.ConsumerTopicStatsData;
import com.dianping.swallow.web.service.ConsumerTopicStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午7:32:57
 */
public class ConsumerTopicStatsDataStorager extends AbstractConsumerStatsDataStorager {

	@Autowired
	private ConsumerTopicStatsDataService topicStatsDataService;

	@Override
	protected void doStorage() {
		ConsumerTopicStatsData topicStatsData = consumerStatsDataWapper.getTotalTopicStatsData(lastTimeKey.get());
		doStorageTopicStats(topicStatsData);
	}

	private void doStorageTopicStats(final ConsumerTopicStatsData topicStatsData) {
		logger.info("[doStorageTopicStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageTopicStats");
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
	
}
