package com.dianping.swallow.common.internal.config;

import com.dianping.swallow.common.internal.config.impl.LionUtilImpl.LionRet;


/**
 * @author mingdongli
 *
 * 2015年9月22日下午5:52:19
 */
public interface HttpMethod {

	<T extends LionRet> T setValue(String urlAddress, Class<T> clazz);
}
