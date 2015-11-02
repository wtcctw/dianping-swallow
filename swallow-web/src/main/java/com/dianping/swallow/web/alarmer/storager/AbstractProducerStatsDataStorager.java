package com.dianping.swallow.web.alarmer.storager;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;

/**
 * 
 * @author qiyin
 *
 *         2015年10月14日 下午8:12:40
 */
public abstract class AbstractProducerStatsDataStorager extends AbstractStatsDataStorager {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	protected ProducerStatsDataWapper producerStatsDataWapper;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		producerDataRetriever.registerListener(this);
	}
}
