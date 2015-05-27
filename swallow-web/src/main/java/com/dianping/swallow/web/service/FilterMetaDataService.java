package com.dianping.swallow.web.service;

import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * @author mingdongli
 *
 * 2015年5月24日下午3:19:24
 */
public interface FilterMetaDataService extends SwallowService{
	
	Map<String, Set<String>> loadTopicToWhiteList();

	void setTopicToWhiteList(Map<String, Set<String>> topicToWhiteList);

	Collection<String> loadAdminSet();

	void setAdminSet(Set<String> adminSet);

	boolean isShowContentToAll();

	void setShowContentToAll(boolean showContentToAll);

	String loadDefaultAdmin();
	
	void setDefaultAdmin(String defaultAdmin);
	
	String loadLogoutUrl();
	
	void setLogoutUrl(String env);

}
