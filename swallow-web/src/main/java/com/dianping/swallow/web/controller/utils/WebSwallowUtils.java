package com.dianping.swallow.web.controller.utils;

import javax.servlet.http.HttpServletRequest;

public class WebSwallowUtils {
	
	private static final String 						LOGINDELIMITOR						= "\\|";
	
	public static String getVisitInfo(HttpServletRequest request){
		String tmpusername = request.getRemoteUser();
		if (tmpusername == null){ 
		      return "";
		}
	    else{
	    	String[] userinfo = tmpusername.split(LOGINDELIMITOR);
	    	return userinfo[0];
	    }
	}
	
}
