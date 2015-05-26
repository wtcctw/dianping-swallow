package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.common.server.monitor.data.StatisDetailType;


/**
 * @author mengwenchao
 *
 * 2015年4月29日 上午11:20:06
 */
public class ProducerServerDataDesc extends AbstractServerDataDesc{

	public ProducerServerDataDesc(String serverIp, String topic,
			StatisDetailType dt) {
		super(serverIp, topic, dt);
	}


}
