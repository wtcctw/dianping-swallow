package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.service.ProducerServerStatsDataService;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午8:12:11
 */
@Component
public class ProducerServerStatsDataStorager extends AbstractProducerStatsDataStorager {

	@Autowired
	private ProducerServerStatsDataService serverStatsDataService;

	@Override
	protected void doStorage() {
		doStorageServerStats();
	}

	private void doStorageServerStats() {
		logger.info("[doStorageServerStats].");
		final List<ProducerServerStatsData> serverStatsDatas = producerStatsDataWapper.getServerStatsDatas(
				getLastTimeKey(), true);
		if (serverStatsDatas != null) {
			serverStatsDataService.insert(serverStatsDatas);
		}
	}

}
