package com.dianping.swallow.common.internal.dao.impl.kafka;


import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.impl.AbstractClusterFactory;

/**
 * @author mengwenchao
 *
 * 2015年11月2日 下午4:03:45
 */
public class KafkaClusterFactory extends AbstractClusterFactory{

	@Override
	public Cluster createCluster(String address) {
		return null;
	}

	@Override
	public boolean accepts(String url) {
		return isKafkaUrl(getTypeDesc(url));
	}

	
	private boolean isKafkaUrl(String type) {
		
		if(type != null && type.equalsIgnoreCase(KafkaCluster.schema)){
			return true;
		}
		
		return false;
	}


}
