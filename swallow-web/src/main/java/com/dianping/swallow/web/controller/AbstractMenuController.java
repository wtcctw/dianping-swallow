package com.dianping.swallow.web.controller;


import java.util.HashMap;
import java.util.Map;


import com.dianping.swallow.common.internal.codec.JsonBinder;

/**
 * @author mengwenchao
 *
 * 2015年4月2日 下午6:24:25
 */
public abstract class AbstractMenuController extends AbstractController{

	protected JsonBinder jsonBinder = JsonBinder.getNonEmptyBinder();	
	
	public static final String MENU_KEY = "menu";
	public static final String CONTENT_PATH_KEY = "contextPath";
	
	protected abstract String getMenu();
	
	protected Map<String, Object> createViewMap() {
		
		Map<String, Object> paras = new HashMap<String, Object>();
		
		paras.put(MENU_KEY, getMenu());
		paras.put(CONTENT_PATH_KEY, "");
		return paras;
	}

}