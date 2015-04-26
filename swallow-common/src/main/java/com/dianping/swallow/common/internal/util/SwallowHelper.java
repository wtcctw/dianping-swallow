package com.dianping.swallow.common.internal.util;

import java.io.File;

import com.dianping.cat.Cat;
import com.dianping.swallow.common.internal.pool.DefaultThreadExceptionHandler;

/**
 * @author mengwenchao
 *
 * 2015年3月10日 下午4:12:49
 */
public class SwallowHelper {
	
	public static void initialize(){
		
		Cat.initialize(new File(Cat.getCatHome(), "client.xml"));
		new DefaultThreadExceptionHandler();
	}

}
