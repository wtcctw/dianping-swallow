package com.dianping.swallow.common.internal.dao.impl;

import com.dianping.swallow.common.internal.exception.SwallowException;

/**
 * @author mengwenchao
 *
 * 2015年11月4日 下午7:30:08
 */
public class ClusterCreateException extends SwallowException{
	
	private static final long serialVersionUID = 1L;

	public ClusterCreateException(String url, Throwable th){
		this(url, null, th);
	}

	public ClusterCreateException(String url, String desc, Throwable th){
		super("url:" + url + (desc !=null ? "," + desc : ""), th);
	}

}
