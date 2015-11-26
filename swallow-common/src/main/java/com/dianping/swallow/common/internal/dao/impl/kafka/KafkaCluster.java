package com.dianping.swallow.common.internal.dao.impl.kafka;

import java.net.InetSocketAddress;
import java.util.List;

import com.dianping.swallow.common.internal.dao.impl.AbstractCluster;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午10:08:56
 */
public class KafkaCluster extends AbstractCluster{
	
	public static String schema = "kafka://";

	public KafkaCluster(String address) {
		super(address);
	}

	@Override
	protected List<InetSocketAddress> build(String url) {
		// TODO Auto-generated method stub
		return null;
	}

}
