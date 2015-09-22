package com.dianping.swallow.web.controller.chain.lion;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.service.ConsumerServerResourceService;
import com.dianping.swallow.web.service.impl.TopicResourceServiceImpl;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月22日上午8:52:24
 */
@Component
public class ConsumerServerLionEditor extends AbstractLionEditor{

	@Resource(name = "consumerServerResourceService")
	private ConsumerServerResourceService consumerServerResourceService;

	public ConsumerServerLionEditor() {
		super();
	}

	public ConsumerServerLionEditor(AbstractLionEditor nextSuccessor) {
		super(nextSuccessor);
	}

	@Override
	protected ResponseStatus editLionHelper(final LionConfigBean lionConfigBean) {
		
		String topic = lionConfigBean.getTopic();
		boolean test = lionConfigBean.isTest();
		String consumerServer = lionConfigBean.getConfigureResult().getConsumerServer();
		StringBuilder stringBuilder = new StringBuilder();
		
		String oldConsumerServerLionConfig = consumerServerResourceService.loadConsumerServerLionConfig();
		stringBuilder.append(oldConsumerServerLionConfig).append(";\n").append(topic).append("=")
				.append(consumerServer);
		String newConsumerServerLionConfig = stringBuilder.toString();
		if (test) {
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Set value from \n[%s]\n to \n[%s]\n of lion key %s successfully",
						oldConsumerServerLionConfig, newConsumerServerLionConfig,
						TopicResourceServiceImpl.SWALLOW_CONSUMER_SERVER_URI));
			}
			return ResponseStatus.SUCCESS;
		} else {
			ResponseStatus responseStatus = doEditLion(TopicResourceServiceImpl.SWALLOW_CONSUMER_SERVER_URI,
					newConsumerServerLionConfig, oldConsumerServerLionConfig);
			if (responseStatus != ResponseStatus.SUCCESS) {
				topicResourceService.loadCachedTopicToAdministrator().remove(topic);
			}
			return responseStatus;
		}
	}

	public void setConsumerServerResourceService(ConsumerServerResourceService consumerServerResourceService) {
		this.consumerServerResourceService = consumerServerResourceService;
	}
	
}
