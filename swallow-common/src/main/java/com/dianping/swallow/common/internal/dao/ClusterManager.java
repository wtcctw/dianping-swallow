package com.dianping.swallow.common.internal.dao;

import java.util.Set;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午9:24:08
 */
public interface ClusterManager {
	
	public static int ORDER = ClusterFactory.ORDER + 1;
	
	Cluster getCluster(String url);
	
	Set<Cluster> allClusters();

}
