package com.dianping.swallow.web.controller.utils;

import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.service.UserService;


/**
 * @author mingdongli
 *
 * 2015年9月1日下午7:04:53
 */
@Component
public class UserUtils {

	private static final String LOGINDELIMITOR = "\\|";
	
	private static final String ALL = "all";
	
	@Resource(name = "userService")
	private UserService userService;


	public String getUsername(HttpServletRequest request) {
		String tmpusername = request.getRemoteUser();

		if (tmpusername == null) {
			return "";
		} else {
			String[] userinfo = tmpusername.split(LOGINDELIMITOR);
			return userinfo[0];
		}
	}
	
	public boolean isAdministrator(String username){
		
		Set<String> adminSet = userService.loadCachedAdministratorSet();
		boolean isAdmin = adminSet.contains(username) || adminSet.contains(ALL);
		return isAdmin;
	}

	public boolean isTrueAdministrator(String username){
		
		Set<String> adminSet = userService.loadCachedAdministratorSet();
		boolean isAdmin = adminSet.contains(username);
		return isAdmin;
	}

}
