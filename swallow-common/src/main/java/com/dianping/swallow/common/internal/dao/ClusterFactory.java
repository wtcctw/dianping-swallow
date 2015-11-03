package com.dianping.swallow.common.internal.dao;

import com.dianping.swallow.common.internal.lifecycle.Ordered;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午10:18:31
 */
public interface ClusterFactory {

	public static int ORDER = Ordered.FIRST; 
	
	Cluster createCluster(String address);
	
	boolean accepts(String url);
}
