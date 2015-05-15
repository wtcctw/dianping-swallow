package com.dianping.swallow.web.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;


public abstract class AbstractAccessControlService extends AbstractSwallowService{

	protected volatile Map<String,Set<String>>  		topicToWhiteList 					= new ConcurrentHashMap<String,Set<String>>();
	protected volatile Set<String>						adminSet 							= new HashSet<String>();

	public Map<String, Set<String>> getTopicToWhiteList() {
		return topicToWhiteList;
	}

	public void setTopicToWhiteList(Map<String, Set<String>> topicToWhiteList) {
		this.topicToWhiteList = topicToWhiteList;
	}

	public Set<String> getAdminSet() {
		return adminSet;
	}

	public void setAdminSet(Set<String> adminSet) {
		this.adminSet = adminSet;
	}
	
}
