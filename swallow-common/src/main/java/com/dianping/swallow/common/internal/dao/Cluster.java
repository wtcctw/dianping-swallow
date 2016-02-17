package com.dianping.swallow.common.internal.dao;


import java.net.InetSocketAddress;
import java.util.List;

import com.dianping.swallow.common.internal.config.SwallowServerConfig;
import com.dianping.swallow.common.internal.lifecycle.Lifecycle;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:22:23
 */
public interface Cluster extends Lifecycle{
	
	List<InetSocketAddress> getSeeds();
	
	List<InetSocketAddress> allServers();
	
	String getAddress();
	
	void setSwallowServerConfig(SwallowServerConfig swallowServerConfig);
	
	/**
	 * 判断两个集群是否为同一个集群（只要有一个server相同，即为同一个）
	 * @param other
	 * @return
	 */
	boolean sameCluster(Cluster other);
	
	
	MessageDAO<?> createMessageDao();

}
