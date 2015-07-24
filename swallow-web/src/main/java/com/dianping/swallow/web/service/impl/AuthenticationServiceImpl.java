package com.dianping.swallow.web.service.impl;

import java.util.Collection;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.swallow.web.service.AbstractSwallowService;
import com.dianping.swallow.web.service.AuthenticationService;
import com.dianping.swallow.web.service.UserService;
import com.dianping.swallow.web.service.TopicService;

/**
 * @author mingdongli 2015年5月12日 上午11:42:49
 */
@Service("authenticationService")
public class AuthenticationServiceImpl extends AbstractSwallowService implements AuthenticationService {

	private static final String MESSAGEURI = "/console/message/auth";

	private static final String DOWNLOAD = "/console/download/auth";

	private static final String ADMINURI = "/console/admin/auth";

	private static final String ALL = "all";

	@Value("${swallow.web.env.notproduct}")
	private boolean showContentToAll;

	@Resource(name = "userService")
	private UserService userService;

	@Resource(name = "topicService")
	private TopicService topicService;

	@PostConstruct
	private void addAllToAdmin() {

		boolean env = EnvZooKeeperConfig.getEnv().equals("product");
		if (showContentToAll && !env) {
			userService.loadCachedAdministratorSet().add(ALL);
		}
	}

	@Override
	public boolean isValid(String username, String topic, String uri) {
		logger.info(String.format("%s request %s", username, uri));

		Set<String> loadAdminSet = userService.loadCachedAdministratorSet();

		if (loadAdminSet.contains(username)) {
			return true;
		} else if (loadAdminSet.contains(ALL) && !uri.startsWith(ADMINURI)) {
			return true;
		} else if (uri.startsWith(MESSAGEURI) || uri.startsWith(DOWNLOAD)) {
			if (StringUtils.isNotBlank(topic) && topicService.loadTopicToWhiteList().get(topic).contains(username)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int checkVisitType(String username) {

		if (userService.loadCachedAdministratorSet().contains(username)) {
			return AuthenticationService.ADMINI;
		} else {
			Collection<Set<String>> topicUsers = topicService.loadTopicToWhiteList().values();
			for (Set<String> set : topicUsers) {
				if (set.contains(username)) {
					return AuthenticationService.USER;
				}
			}
			return AuthenticationService.VISITOR;
		}
	}

}
