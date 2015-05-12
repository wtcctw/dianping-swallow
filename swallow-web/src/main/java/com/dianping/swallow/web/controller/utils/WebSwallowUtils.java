package com.dianping.swallow.web.controller.utils;

import javax.servlet.http.HttpServletRequest;

public class WebSwallowUtils {
	
	private static final String 						LOGINDELIMITOR						= "\\|";
	
	public static void setVisitInfo(HttpServletRequest request, StringBuffer username, StringBuffer txz){
		String tmpusername = request.getRemoteUser();
		if (tmpusername == null){ 
		      username.append("");
		      txz.append("");
		}
	    else{
	    	String[] userinfo = tmpusername.split(LOGINDELIMITOR);
	    	username.append(userinfo[userinfo.length - 1]);
	    	txz.append(userinfo[0]);
	    }
	}
}
