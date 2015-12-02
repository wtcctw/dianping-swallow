package com.dianping.swallow.common.internal.dao.impl;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.ClusterFactory;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;

/**
 * @author mengwenchao
 *
 * 2015年11月1日 下午10:19:22
 */
public abstract class AbstractClusterFactory extends AbstractLifecycle implements ClusterFactory{
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	protected String getTypeDesc(String url) {
		
		int index = url.indexOf("://");
		if(index >= 0){
			return url.substring(0, index + 3);
		}
		return null;
	}

	
	@Override
	public int getOrder() {
		return ORDER;
	}


}
