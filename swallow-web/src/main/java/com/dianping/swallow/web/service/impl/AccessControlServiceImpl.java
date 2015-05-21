package com.dianping.swallow.web.service.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.controller.utils.WebSwallowUtils;
import com.dianping.swallow.web.service.AbstractAccessControlService;
import com.dianping.swallow.web.service.AccessControlService;


/**
 * @author mingdongli
 *		2015年5月12日 上午11:42:49
 */
@Service("accessControlService")
public class AccessControlServiceImpl extends AbstractAccessControlService implements AccessControlService{

	@Override
	public boolean checkVisitIsValid(HttpServletRequest request){
		return checkVisitIsValid(request, null);
	}
	
	@Override
	public boolean checkVisitIsValid(HttpServletRequest request, String topic){
		
		return checkVisit(WebSwallowUtils.getVisitInfo(request), topic);  //based on tongxingzheng
	}
	
	private boolean checkVisit(String name, String topic){
		boolean admin = adminSet.contains(name);
		boolean env   = showContentToAll;
		if(topic != null){
			boolean whiteList = topicToWhiteList.get(topic).contains(name);
			return  env || admin || whiteList;  
		}
		else{
			return  env || admin; 
		}
	}

}
