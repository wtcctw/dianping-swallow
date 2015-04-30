package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.web.monitor.StatsDataType;

/**
 * @author mengwenchao
 *
 * 2015年4月29日 上午11:20:06
 */
public class ProducerServerDataDesc extends AbstractServerDataDesc{

	public ProducerServerDataDesc(String serverIp, String topic,
			StatsDataType dt) {
		super(serverIp, topic, dt);
	}


}
