package com.dianping.swallow.common.internal.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.dao.DAO;


/**
 * @author mengwenchao
 * 
 *         2015年7月14日 下午6:23:33
 */
public class AbstractDao<T extends Cluster> implements DAO<T>{

	private static final long serialVersionUID = 1L;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	protected T cluster;
	
	public AbstractDao(T cluster){
		this.cluster = cluster;
	}
	
	public T getCluster(){
		return cluster;
	}

	
	@Override
	public String toString() {
		return getClass() + "," + cluster;
	}
}
