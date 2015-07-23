package com.dianping.swallow.web.interceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.controller.utils.ExtractUsernameUtils;
import com.dianping.swallow.web.service.UserService;
import com.dianping.swallow.web.service.AuthenticationService;

/**
 * @author mingdongli
 *
 *         2015年6月4日下午4:49:45
 */
public class ModelAndViewInterceptor extends HandlerInterceptorAdapter {

	private static final String USERNAME = "username";
	private static final String ISADMIN = "isadmin";
	private static final String ISVISITOR = "isvisitor";
	private static final String ISUSER = "isuser";
	private static final String LOGOUTURL = "logouturl";

	@Resource(name = "authenticationService")
	private AuthenticationService authenticationService;
	
	@Resource(name = "userService")
	private UserService userService;

	@Autowired
	ExtractUsernameUtils extractUsernameUtils;
	
	private static final Logger logger = Logger
			.getLogger(ModelAndViewInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		return true;
	}

	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		
		String logoutUrl = null;
		try {
			ConfigCache configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
			String prelogoutUrl = configCache.getProperty("cas-server-webapp.logoutUrl").trim();
			logoutUrl = configCache.getProperty("swallow.web.sso.url").trim();
			logoutUrl = prelogoutUrl + "?service=" + logoutUrl.replaceAll(":", "%3A").replaceAll("/", "%2F");
		} catch (LionException e) {
			logger.error("Use lion to get swallow.web.sso.url error.", e);
		}
		String username = extractUsernameUtils.getUsername(request);
		
		int visittype = authenticationService.checkVisitType(username);
		
		if (visittype == AuthenticationService.ADMINI) {
			modelAndView.addObject(ISADMIN, true);
			modelAndView.addObject(ISUSER, false);
			modelAndView.addObject(ISVISITOR, false);
		} else if (visittype == AuthenticationService.USER) {
			modelAndView.addObject(ISADMIN, false);
			modelAndView.addObject(ISUSER, true);
			modelAndView.addObject(ISVISITOR, false);
		} else {
			modelAndView.addObject(ISADMIN, false);
			modelAndView.addObject(ISUSER, false);
			modelAndView.addObject(ISVISITOR, true);
		}
		modelAndView.addObject(USERNAME, username);
		modelAndView.addObject(LOGOUTURL, logoutUrl);
		
		userService.createOrUpdateUser(username);

		logger.info(String
				.format("Add [username : %s], [visittype : %d],  [logouturl : %s] to ModelAndView",
						username, visittype, logoutUrl));
	}

}