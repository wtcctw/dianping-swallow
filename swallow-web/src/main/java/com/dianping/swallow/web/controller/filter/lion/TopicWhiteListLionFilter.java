package com.dianping.swallow.web.controller.filter.lion;

import java.util.LinkedHashSet;
import java.util.Set;

import com.dianping.swallow.common.internal.util.EnvUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

import javax.annotation.PostConstruct;

/**
 * @author mingdongli
 *
 *         2015年9月24日下午3:27:31
 */
@Component
public class TopicWhiteListLionFilter extends AbstractLionFilter {

	@Autowired
	private TopicWhiteList topicWhiteList;

	@Autowired
	private LionConfigManager lionConfigManager;

	@Override
	public synchronized ResponseStatus doFilterHelper(LionFilterEntity lionFilterEntity, LionFilterResult result,
			FilterChain<LionFilterEntity, LionFilterResult> chain) {

		String topic = lionFilterEntity.getTopic();
		boolean isTest = lionFilterEntity.isTest();

		Set<String> newTopics = (Set<String>)getValue(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, Boolean.TRUE);
		Set<String> oldTopics = topicWhiteList.getTopics();
		if(newTopics == null || oldTopics == null || (oldTopics.size() - newTopics.size()) > TopicWhiteList.MAX_TOPIC_WHILTE_LIST_DECREASE){
			return ResponseStatus.INVALIDLENGTH;
		}

		Set<String> newTopicsCopy = new LinkedHashSet<String>(newTopics);
		newTopicsCopy.add(topic);
		String topicJoin = StringUtils.join(newTopicsCopy, ";");
		if (topicJoin.length() < lionConfigManager.getWhitelistLength()) {
			topicResourceService.loadCachedTopicToAdministrator().remove(topic);
			return ResponseStatus.INVALIDLENGTH;
		}

		ResponseStatus status = null;

		if(EnvUtil.isProduct()){
			Set<String> envs = EnvUtil.allEnv();
			for(String env : envs){
				status	=  doEditLion(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, topicJoin,
						StringUtils.join(newTopics, ";"), isTest, env);
				if(status != ResponseStatus.SUCCESS){
					return ResponseStatus.LIONEXCEPTION;
				}
			}
		}else{
			status	=  doEditLion(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, topicJoin,
					StringUtils.join(newTopics, ";"), isTest, null);

		}

		return status;
	}

	public void setTopicWhiteList(TopicWhiteList topicWhiteList) {
		this.topicWhiteList = topicWhiteList;
	}

}
