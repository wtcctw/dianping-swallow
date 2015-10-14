package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年10月13日 下午3:05:17
 */
public class ConsumerServerStatsDataStorager extends AbstractConsumerStatsDataStorager {

	@Autowired
	private ConsumerServerStatsDataService statsDataService;

	@Override
	protected void doStorage() {
		logger.info("[doStorageServerStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageServerStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				doStorageServerStats();
			}
		});
	}

	private void doStorageServerStats() {
		final List<ConsumerServerStatsData> serverStatsDatas = consumerStatsDataWapper.getServerStatsDatas(
				lastTimeKey.get(), true);
		if (serverStatsDatas == null) {
			return;
		}
		statsDataService.insert(serverStatsDatas);
	}

}
