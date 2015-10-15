package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午8:12:11
 */
public class ProducerServerStatsDataStorager extends AbstractProducerStatsDataStorager {

	@Autowired
	private ProducerServerStatsDataService serverStatsDataService;

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
		final List<ProducerServerStatsData> serverStatsDatas = producerStatsDataWapper.getServerStatsDatas(
				getLastTimeKey(), true);
		if (serverStatsDatas != null) {
			serverStatsDataService.insert(serverStatsDatas);
		}
	}

}
