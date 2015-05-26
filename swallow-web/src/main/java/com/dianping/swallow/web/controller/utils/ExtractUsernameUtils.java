package com.dianping.swallow.web.controller.utils;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

@Component
public class ExtractUsernameUtils {

	private static final String LOGINDELIMITOR = "\\|";

	public String getUsername(HttpServletRequest request) {
		String tmpusername = request.getRemoteUser();

		if (tmpusername == null) {
			return "";
		} else {
			String[] userinfo = tmpusername.split(LOGINDELIMITOR);
			return userinfo[0];
		}
	}

}
