package com.dianping.swallow.web.alarmer.container;

import java.util.Map;

import com.dianping.swallow.web.model.server.ConsumerHAServer;
import com.dianping.swallow.web.model.server.ProducerServer;

/**
 * 
 * @author qiyin
 *
 *         2015年10月19日 下午2:30:25
 */
public interface ServerContainer {

	Map<String, ConsumerHAServer> getConsumerHAServers();

	Map<String, ProducerServer> getProducerServers();

}
