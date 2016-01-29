package com.dianping.swallow.common.internal.config;

import com.dianping.swallow.common.internal.lifecycle.Ordered;

/**
 * @author mengwenchao
 *
 * 2016年1月29日 下午5:34:43
 */
public interface SwallowServerConfig extends SwallowConfig{

	public static int ORDER = Ordered.FIRST;
	

	String getHeartBeatMongo();
	
	boolean isSupported();

}
