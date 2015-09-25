package com.dianping.swallow.web.servlet.listener;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;


/**
 * @author mingdongli
 *
 * 2015年9月17日上午9:13:22
 */
public class LoggerContextLoaderListener implements ServletContextListener {

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		
//		try {
//			Class.forName("com.dianping.swallow.web.common.LoggerLoader");
//		} catch (ClassNotFoundException e) {
//
//			e.printStackTrace();
//		}

	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		// ignore

	}

}
