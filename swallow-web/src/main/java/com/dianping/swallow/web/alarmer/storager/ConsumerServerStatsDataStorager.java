package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年10月13日 下午3:05:17
 */
public class ConsumerServerStatsDataStorager extends AbstractConsumerStatsDataStorager {

	@Autowired
	private ConsumerDataRetriever cDataRetriever;

	@Autowired
	private ConsumerStatsDataWapper cStatsDataWapper;

	@Autowired
	private ConsumerServerStatsDataService statsDataService;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		cDataRetriever.registerListener(this);
		storagerName = getClass().getSimpleName();
	}

	@Override
	protected void doStorage() {
		List<ConsumerServerStatsData> serverStatsDatas = cStatsDataWapper.getServerStatsDatas(lastTimeKey.get(), true);
		doStorageServerStats(serverStatsDatas);
	}

	private void doStorageServerStats(final List<ConsumerServerStatsData> serverStatsDatas) {
		logger.info("[doStorageServerStats]");
		SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, getClass().getSimpleName()
				+ "-doStorageServerStats");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				if (serverStatsDatas == null) {
					return;
				}
				statsDataService.insert(serverStatsDatas);
				boolean isFirstTime = true;
				for (ConsumerServerStatsData serverStatsData : serverStatsDatas) {
					if (isFirstTime) {
						lastTimeKey.set(serverStatsData.getTimeKey());
						isFirstTime = false;
					}
				}
			}
		});

	}

}
