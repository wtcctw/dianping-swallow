package com.dianping.swallow.common.internal.dao;

import java.io.Serializable;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午3:46:08
 */
public interface DAO<T extends Cluster> extends Serializable{
	
	T getCluster();
	
	public static final String GET_CLUSTER = "getCluster";

}
