package com.dianping.swallow.web.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.controller.utils.WebSwallowUtils;


/**
 * @author mingdongli
 *		2015年5月12日 上午11:42:49
 */
@Service("accessControlService")
public class AccessControlService implements SwallowService{
	
	
	protected volatile Map<String,Set<String>>  		topicToWhiteList 					= new ConcurrentHashMap<String,Set<String>>();
	protected volatile boolean							showContentToAll 					= true;
	protected volatile Set<String>						adminSet 							= new HashSet<String>();
	
	public Map<String, Set<String>> getTopicToWhiteList() {
		return topicToWhiteList;
	}

	public void setTopicToWhiteList(Map<String, Set<String>> topicToWhiteList) {
		this.topicToWhiteList = topicToWhiteList;
	}

	public boolean getShowContentToAll() {
		return showContentToAll;
	}

	public void setShowContentToAll(boolean showContentToAll) {
		this.showContentToAll = showContentToAll;
	}

	public Set<String> getAdminSet() {
		return adminSet;
	}

	public void setAdminSet(Set<String> adminSet) {
		this.adminSet = adminSet;
	}

	private boolean checkVisit(String name, String topic){
		boolean admin = adminSet.contains(name);
		boolean env   = showContentToAll;
		if(topic != null){
			boolean whiteList = topicToWhiteList.get(topic).contains(name);
			return env || admin || whiteList;
		}
		else{
			return env || admin;
		}
	}
	
	public boolean checkVisitIsValid(HttpServletRequest request){
		return checkVisitIsValid(request, null);
	}
	
	public boolean checkVisitIsValid(HttpServletRequest request, String topic){
		StringBuffer username = new StringBuffer();
		StringBuffer txz = new StringBuffer();
		WebSwallowUtils.setVisitInfo(request, username, txz);
		return checkVisit(username.toString(), topic);
	}

}
