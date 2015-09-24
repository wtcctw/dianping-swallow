package com.dianping.swallow.web.controller.filter.lion;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月24日下午3:27:31
 */
@Component
public class TopicWhiteListLionFilter extends AbstractLionFilter{

	@Value("${swallow.web.lion.topicwhitelistlength}")
	private int whiteListLengthThreshold;

	@Autowired
	private TopicWhiteList topicWhiteList;

	@Override
	public ResponseStatus doFilterHelper(LionFilterEntity lionFilterEntity, LionFilterResult result,
			FilterChain<LionFilterEntity, LionFilterResult> chain) {

		String topic = lionFilterEntity.getTopic();
		boolean isTest = lionFilterEntity.isTest();

		Set<String> oldTopics = topicWhiteList.getTopics();
		if (oldTopics == null) {
			return ResponseStatus.INVALIDLENGTH;
		}
		Set<String> newTopics = new LinkedHashSet<String>(oldTopics);
		newTopics.add(topic);
		String topicJoin = StringUtils.join(newTopics, ";");
		if (topicJoin.length() < whiteListLengthThreshold) {
			topicResourceService.loadCachedTopicToAdministrator().remove(topic);
			return ResponseStatus.INVALIDLENGTH;
		}
		return doEditLion(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, topicJoin,
				StringUtils.join(oldTopics, ";"), isTest);

	}

	public void setTopicWhiteList(TopicWhiteList topicWhiteList) {
		this.topicWhiteList = topicWhiteList;
	}

}
