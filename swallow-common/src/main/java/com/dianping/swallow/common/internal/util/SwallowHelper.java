package com.dianping.swallow.common.internal.util;

import com.dianping.cat.Cat;
import com.dianping.swallow.common.internal.config.LoggerLoader;
import com.dianping.swallow.common.internal.pool.DefaultThreadExceptionHandler;

import java.io.File;

/**
 * @author mengwenchao
 *
 * 2015年3月10日 下午4:12:49
 */
public class SwallowHelper {
	
	public static void initialize(){
		
		new DefaultThreadExceptionHandler();
		Cat.initialize(new File(Cat.getCatHome(), "client.xml"));
	}

	public static void clientInitialize(){

		initialize();
		LoggerLoader.init();
	}

}
