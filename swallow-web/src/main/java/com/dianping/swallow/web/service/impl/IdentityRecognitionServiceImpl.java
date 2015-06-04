package com.dianping.swallow.web.service.impl;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.service.FilterMetaDataService;
import com.dianping.swallow.web.service.IdentityRecognitionService;


/**
 * @author mingdongli
 *
 * 2015年6月4日下午4:38:56
 */
@Service("identityRecognitionService")
public class IdentityRecognitionServiceImpl implements IdentityRecognitionService{
	
	@Resource(name = "filterMetaDataService")
	private FilterMetaDataService filterMetaDataService;

	@Override
	public boolean isAdmin(String username) {
		
		return filterMetaDataService.loadAdminSet().contains(username);
	}

	@Override
	public boolean isUser(String username) {
		
		Collection<Set<String>> topicUsers = filterMetaDataService.loadTopicToWhiteList().values();
		for(Set<String> set : topicUsers){
			if(set.contains(username)){
				return true;
			}
		}
		return false;
	}

}
