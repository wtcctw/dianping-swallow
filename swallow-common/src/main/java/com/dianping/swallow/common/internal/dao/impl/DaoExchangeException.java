package com.dianping.swallow.common.internal.dao.impl;

import com.dianping.swallow.common.internal.dao.Cluster;
import com.dianping.swallow.common.internal.exception.SwallowRuntimeException;

/**
 * @author mengwenchao
 *
 * 2015年11月10日 下午6:10:01
 */
public class DaoExchangeException extends SwallowRuntimeException{

	private static final long serialVersionUID = 1L;

	public DaoExchangeException(Cluster oldCluster, Cluster newCluster) {
		super(oldCluster + "->" + newCluster);
	}

}
