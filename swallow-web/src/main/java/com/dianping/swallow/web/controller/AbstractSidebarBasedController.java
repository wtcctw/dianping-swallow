package com.dianping.swallow.web.controller;

import java.util.Map;

/**
 * 有左边栏
 * @author mengwenchao
 *
 * 2015年4月23日 下午2:45:17
 */
public abstract class AbstractSidebarBasedController extends AbstractMenuController{
	
	private String SIDE_KEY    =  "side"; 

	private String SUB_SIDE_KEY =  "subside";

	protected abstract String getSide();
	
	public abstract String getSubSide();
	

	protected Map<String, Object> createViewMap() {
		
		Map<String, Object> paras = super.createViewMap();
		
		paras.put(SIDE_KEY, getSide());
		paras.put(SUB_SIDE_KEY, getSubSide());
		
		return paras;
	}

	
}
