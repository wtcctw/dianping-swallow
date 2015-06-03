package com.dianping.swallow.web.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.FilterMetaDataService;
 
public class ModelAndViewInterceptor extends HandlerInterceptorAdapter{
	
	private static final String USERNAME = "username";
	private static final String ISADMIN = "isadmin";
	private static final String LOGOUTURL = "logouturl";
	
	@Resource(name = "filterMetaDataService")
	private FilterMetaDataService filterMetaDataService;
	
	@Autowired
	ExtractUsernameUtils extractUsernameUtils;
 
	private static final Logger logger = Logger.getLogger(ModelAndViewInterceptor.class);
 
	//before the actual handler will be executed
	public boolean preHandle(HttpServletRequest request, 
		HttpServletResponse response, Object handler)
	    throws Exception {
 
		return true;
	}
 
	//after the handler is executed
	public void postHandle(
		HttpServletRequest request, HttpServletResponse response, 
		Object handler, ModelAndView modelAndView)
		throws Exception {
 
		String username = extractUsernameUtils.getUsername(request);
		boolean showadmin = filterMetaDataService.loadAdminSet().contains(username);
		String logoutUrl = filterMetaDataService.loadLogoutUrl();
 
		//modified the exisitng modelAndView
		modelAndView.addObject(USERNAME,username);
		modelAndView.addObject(ISADMIN,showadmin);
		modelAndView.addObject(LOGOUTURL,logoutUrl);
		
		logger.info(String.format("Add [username : %s], [isadmin : %s], [logouturl : %s] to ModelAndView", username,showadmin,logoutUrl));
	}
}