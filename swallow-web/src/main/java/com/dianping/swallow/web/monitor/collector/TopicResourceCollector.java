package com.dianping.swallow.web.monitor.collector;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.TopicResourceService;

/**
 * 
 * @author qiyin
 *
 *         2015年9月30日 上午11:24:59
 */
@Component
public class TopicResourceCollector extends AbstractResourceCollector {

	@Autowired
	private TopicResourceService topicResourceService;

	@Autowired
	private ProducerStatsDataWapper producerStatsDataWapper;
	

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		collectorName = getClass().getSimpleName();
		collectorInterval = 20;
		collectorDelay = 1;
	}

	@Override
	public void doCollector() {
		logger.info("[doCollector] start collect topicResource.");
		doTopicCollector();
	}

	private void doTopicCollector() {

	}

	@Override
	public int getCollectorDelay() {
		return collectorDelay;
	}

	@Override
	public int getCollectorInterval() {
		return collectorInterval;
	}
	
	static class ActiveIp{
		
		private Map<String,Long> ips = new HashMap<String,Long>();
		
		
		
	}

}
