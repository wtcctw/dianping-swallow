package com.dianping.swallow.common.internal.dao;


import java.util.Set;

import com.dianping.swallow.common.internal.config.SwallowServerConfig;
import com.dianping.swallow.common.internal.dao.impl.ClusterCreateException;
import com.dianping.swallow.common.internal.observer.Observable;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:24:08
 */
public interface ClusterManager extends Observable{
	
	public static int ORDER = Math.max(ClusterFactory.ORDER, SwallowServerConfig.ORDER) + 1;
	
	Cluster getCluster(String url) throws ClusterCreateException;
	
	Set<Cluster> allClusters();

}
