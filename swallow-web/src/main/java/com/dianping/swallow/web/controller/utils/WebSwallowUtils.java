package com.dianping.swallow.web.controller.utils;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class WebSwallowUtils {
	
	private static final String 						LOGINDELIMITOR						= "\\|";
	
	public static List<String> getVisitInfo(HttpServletRequest request){
		List<String> info = new ArrayList<String>(); 
		String tmpusername = request.getRemoteUser();
		if (tmpusername == null){ 
		      info.add("");
		      info.add("");
		}
	    else{
	    	String[] userinfo = tmpusername.split(LOGINDELIMITOR);
	    	info.add(userinfo[userinfo.length - 1]);
	    	info.add(userinfo[0]);
	    }
		return info;
	}
	
	public static String getUsername(HttpServletRequest request){
		
		return getVisitInfo(request).get(0);
		
	}
	
	public static String getTxz(HttpServletRequest request){
		
		return getVisitInfo(request).get(1);
		
	}
	
}
