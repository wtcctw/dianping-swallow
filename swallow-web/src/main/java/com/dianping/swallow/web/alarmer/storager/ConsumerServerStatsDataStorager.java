package com.dianping.swallow.web.alarmer.storager;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

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
		doStorageServerStats();
	}

	private void doStorageServerStats() {
		logger.info("[doStorageServerStats].");
		final List<ConsumerServerStatsData> serverStatsDatas = consumerStatsDataWapper.getServerStatsDatas(
				getLastTimeKey(), true);
		if (serverStatsDatas != null) {
			statsDataService.insert(serverStatsDatas);
		}
	}

}
