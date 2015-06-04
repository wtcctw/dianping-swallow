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
import com.dianping.swallow.web.service.IdentityRecognitionService;
 

/**
 * @author mingdongli
 *
 * 2015年6月4日下午4:49:45
 */
public class ModelAndViewInterceptor extends HandlerInterceptorAdapter{
	
	private static final String USERNAME = "username";
	private static final String ISADMIN = "isadmin";
	private static final String ISVISITOR = "isvisitor";
	private static final String ISUSER = "isuser";
	private static final String LOGOUTURL = "logouturl";
	
	@Resource(name = "filterMetaDataService")
	private FilterMetaDataService filterMetaDataService;
	
	@Resource(name = "identityRecognitionService")
	private IdentityRecognitionService identityRecognitionService;
	
	@Autowired
	ExtractUsernameUtils extractUsernameUtils;
 
	private static final Logger logger = Logger.getLogger(ModelAndViewInterceptor.class);
 
	public boolean preHandle(HttpServletRequest request, 
		HttpServletResponse response, Object handler)
	    throws Exception {
 
		return true;
	}
 
	public void postHandle(
		HttpServletRequest request, HttpServletResponse response, 
		Object handler, ModelAndView modelAndView)
		throws Exception {
 
		String logoutUrl = filterMetaDataService.loadLogoutUrl();
		String username = extractUsernameUtils.getUsername(request);
		boolean showadmin = identityRecognitionService.isAdmin(username);
		boolean showuser = identityRecognitionService.isUser(username);
		if(showadmin){
			modelAndView.addObject(ISADMIN,true);
			modelAndView.addObject(ISUSER,false);
			modelAndView.addObject(ISVISITOR,false);
		}
		else if(showuser){
			modelAndView.addObject(ISADMIN,false);
			modelAndView.addObject(ISUSER,true);
			modelAndView.addObject(ISVISITOR,false);
		}
		else{
			modelAndView.addObject(ISADMIN,false);
			modelAndView.addObject(ISUSER,false);
			modelAndView.addObject(ISVISITOR,true);
		}
 
		modelAndView.addObject(USERNAME,username);
		modelAndView.addObject(LOGOUTURL,logoutUrl);
		
		logger.info(String.format("Add [username : %s], [isadmin : %s], [isuser : %s], [logouturl : %s] to ModelAndView", username,showadmin,showuser,logoutUrl));
	}
	
}