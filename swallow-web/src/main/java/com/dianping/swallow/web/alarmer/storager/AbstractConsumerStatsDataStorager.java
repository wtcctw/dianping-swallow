package com.dianping.swallow.web.alarmer.storager;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;

/**
 * 
 * @author qiyin
 *
 *         2015年10月13日 下午3:53:50
 */
public abstract class AbstractConsumerStatsDataStorager extends AbstractStatsDataStorager {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	protected ConsumerStatsDataWapper consumerStatsDataWapper;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
		storagerName = getClass().getSimpleName();
	}
}
