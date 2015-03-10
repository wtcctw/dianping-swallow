package com.dianping.swallow.common.internal.util;

import com.dianping.cat.Cat;

/**
 * @author mengwenchao
 *
 * 2015年3月10日 下午4:12:49
 */
public class SwallowHelper {
	
	public static void initialize(){
		
		Cat.initialize(Cat.getCatHome(), "client.xml");
		new DefaultThreadExceptionHandler().setExceptionCaughtHandler();
	}

}
