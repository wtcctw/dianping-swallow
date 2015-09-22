package com.dianping.swallow.web.controller.chain.lion;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月22日上午8:45:08
 */
@Component
public class TopicWhiteListLionEditor extends AbstractLionEditor {

	@Autowired
	private TopicWhiteList topicWhiteList;

	public TopicWhiteListLionEditor() {
		super();
	}

	public TopicWhiteListLionEditor(AbstractLionEditor nextSuccessor) {
		super(nextSuccessor);
	}

	@Override
	protected ResponseStatus editLionHelper(LionConfigBean lionConfigBean) {

		String topic = lionConfigBean.getTopic();
		boolean test = lionConfigBean.isTest();

		Set<String> oldTopics = topicWhiteList.getTopics();
		if (oldTopics == null) {
			return ResponseStatus.INVALIDTOPICSIZE;
		}
		Set<String> newTopics = new LinkedHashSet<String>(oldTopics);
		newTopics.add(topic);
		if (test) {
			if (logger.isInfoEnabled() && oldTopics != null) {
				logger.info(String.format("Set value from \n[%s]\n to \n[%s]\n of lion key %s successfully",
						StringUtils.join(oldTopics, ";"), StringUtils.join(newTopics, ";"),
						TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY));
			}
			return ResponseStatus.SUCCESS;
		} else {
			String topicJoin = StringUtils.join(newTopics, ";");
			ResponseStatus responseStatus = doEditLion(TopicResourceServiceImpl.SWALLOW_TOPIC_WHITELIST_KEY, topicJoin,
					StringUtils.join(oldTopics, ";"));
			if (responseStatus != ResponseStatus.SUCCESS) {
				topicResourceService.loadCachedTopicToAdministrator().remove(topic);
			}
			return responseStatus;
		}
	}

	public void setTopicWhiteList(TopicWhiteList topicWhiteList) {
		this.topicWhiteList = topicWhiteList;
	}

}
