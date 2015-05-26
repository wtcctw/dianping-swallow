package com.dianping.swallow.web.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.AccessControlService;
import com.dianping.swallow.web.service.FilterMetaDataService;

/**
 * @author mingdongli 2015年5月12日 上午11:42:49
 */
@Service("accessControlService")
public class AccessControlServiceImpl extends AbstractSwallowService implements AccessControlService {
	
	@Resource(name = "filterMetaDataService")
	private FilterMetaDataService filterMetaDataService;

	@Override
	public boolean checkVisitIsValid(String username) {
		return checkVisitIsValid(username, null);
	}

	@Override
	public boolean checkVisitIsValid(String username, String topic) {

		return checkVisit(username, topic); 
	}

	private boolean checkVisit(String name, String topic) {
		boolean admin = filterMetaDataService.loadAdminSet().contains(name);
		boolean env = filterMetaDataService.isShowContentToAll();
		if (topic != null) {
			boolean whiteList = filterMetaDataService.loadTopicToWhiteList().get(topic).contains(name);
			return env || admin || whiteList;
		} else {
			return env || admin;
		}
	}

}
