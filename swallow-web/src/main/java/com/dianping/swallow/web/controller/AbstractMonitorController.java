package com.dianping.swallow.web.controller;

/**
 * 有左边栏
 * 
 * @author mengwenchao
 * 
 *         2015年4月23日 下午2:45:17
 */
public abstract class AbstractMonitorController extends AbstractSidebarBasedController {

	@Override
	protected String getMenu() {
		
		return "monitor";
	}

}
