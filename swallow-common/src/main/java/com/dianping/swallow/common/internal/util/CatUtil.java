package com.dianping.swallow.common.internal.util;

import com.dianping.cat.Cat;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午5:14:44
 */
public class CatUtil {
	
	public static void logException(Throwable th){
		Cat.logError(th);
	}

}
