package com.dianping.swallow.web.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.service.FilterMetaDataService;

/**
 * @author mingdongli
 *
 *         2015年5月24日下午3:20:46
 */
@Service("filterMetaDataService")
public class FilterMetaDataServiceImpl implements FilterMetaDataService {

	@Value("${swallow.web.env.notproduct}")
	private boolean showContentToAll;

	@Value("${swallow.web.admin.defaultadmin}")
	private String defaultAdmin;

	private Map<String, Set<String>> topicToWhiteList = new ConcurrentHashMap<String, Set<String>>();

	private Set<String> adminSet = new HashSet<String>();

	public Map<String, Set<String>> loadTopicToWhiteList() {
		return topicToWhiteList;
	}

	public void setTopicToWhiteList(Map<String, Set<String>> topicToWhiteList) {
		this.topicToWhiteList = topicToWhiteList;
	}

	public Collection<String> loadAdminSet() {
		Collection<String> collection = Collections.synchronizedCollection(adminSet);
		return collection;
	}

	public void setAdminSet(Set<String> adminSet) {
		Collection<String> collection = Collections.synchronizedCollection(adminSet);
		this.adminSet = (Set<String>) collection;
	}

	public boolean isShowContentToAll() {
		return showContentToAll;
	}

	public void setShowContentToAll(boolean showContentToAll) {
		this.showContentToAll = showContentToAll;
	}

	public String getDefaultAdmin() {
		return defaultAdmin;
	}

	public void setDefaultAdmin(String defaultAdmin) {
		this.defaultAdmin = defaultAdmin;
	}

}
