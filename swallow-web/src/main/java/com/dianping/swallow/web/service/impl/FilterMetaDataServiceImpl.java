package com.dianping.swallow.web.service.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.lion.client.ConfigCache;
import com.dianping.lion.client.LionException;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.FilterMetaDataService;

/**
 * @author mingdongli
 *
 *         2015年5月24日下午3:20:46
 */
@Service("filterMetaDataService")
public class FilterMetaDataServiceImpl extends AbstractSwallowService implements FilterMetaDataService {

	@Value("${swallow.web.env.notproduct}")
	private boolean showContentToAll;

	@Value("${swallow.web.admin.defaultadmin}")
	private String defaultAdmin;
	
	private String logoutUrl;
	
	private Map<String, Set<String>> topicToWhiteList = new ConcurrentHashMap<String, Set<String>>();

	private Set<String> adminSet = new HashSet<String>();
	
	private Set<String> allUsers = new HashSet<String>();
	
	@PostConstruct
	private void environment(){
		try {
			ConfigCache configCache = ConfigCache.getInstance(EnvZooKeeperConfig.getZKAddress());
			String prelogoutUrl = configCache.getProperty("cas-server-webapp.logoutUrl").trim();
			logoutUrl = configCache.getProperty("swallow.web.sso.url").trim();
			logoutUrl = prelogoutUrl + "?service=" + logoutUrl.replaceAll(":", "%3A").replaceAll("/", "%2F");
		} catch (LionException e) {
			logger.error("Use lion to get swallow.web.sso.url error.", e);
		}
	}

	@Override
	public Map<String, Set<String>> loadTopicToWhiteList() {
		return topicToWhiteList;
	}

	@Override
	public void setTopicToWhiteList(Map<String, Set<String>> topicToWhiteList) {
		this.topicToWhiteList = topicToWhiteList;
	}

	@Override
	public Collection<String> loadAdminSet() {
		Collection<String> collection = Collections.synchronizedCollection(adminSet);
		return collection;
	}

	@Override
	public void setAdminSet(Set<String> adminSet) {
		Collection<String> collection = Collections.synchronizedCollection(adminSet);
		this.adminSet = (Set<String>) collection;
	}
	
	@Override
	public Collection<String> loadAllUsers() {
		Collection<String> collection = Collections.synchronizedCollection(allUsers);
		return collection;
	}

	@Override
	public void setAllUsers(Set<String> allUsers) {
		Collection<String> collection = Collections.synchronizedCollection(allUsers);
		this.allUsers = (Set<String>) collection;
	}

	@Override
	public boolean isShowContentToAll() {
		return showContentToAll;
	}

	@Override
	public void setShowContentToAll(boolean showContentToAll) {
		this.showContentToAll = showContentToAll;
	}

	@Override
	public String loadDefaultAdmin() {
		return defaultAdmin;
	}

	@Override
	public void setDefaultAdmin(String defaultAdmin) {
		this.defaultAdmin = defaultAdmin;
	}

	@Override
	public String loadLogoutUrl() {
		return logoutUrl;
	}

	@Override
	public void setLogoutUrl(String logoutUrl) {
		this.logoutUrl = logoutUrl;
		
	}

}
