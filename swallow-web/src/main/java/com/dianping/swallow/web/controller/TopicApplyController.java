package com.dianping.swallow.web.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.controller.chain.config.Configure;
import com.dianping.swallow.web.controller.chain.config.ConsumerServerConfigure;
import com.dianping.swallow.web.controller.chain.config.MongoConfigure;
import com.dianping.swallow.web.controller.chain.config.QuoteConfigure;
import com.dianping.swallow.web.controller.chain.lion.ConsumerServerLionEditor;
import com.dianping.swallow.web.controller.chain.lion.TopicCfgLionEditor;
import com.dianping.swallow.web.controller.chain.lion.TopicWhiteListLionEditor;
import com.dianping.swallow.web.controller.chain.validator.AuthenticationValidator;
import com.dianping.swallow.web.controller.chain.validator.NameValidator;
import com.dianping.swallow.web.controller.chain.validator.QuoteValidator;
import com.dianping.swallow.web.controller.chain.validator.TypeValidator;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.model.dom.LionConfigBean;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月9日下午12:04:31
 */
@Controller
public class TopicApplyController {

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	@Autowired
	private TopicWhiteList topicWhiteList;
	
	@Autowired
	private AuthenticationValidator authenticationValidator;

	@Autowired
	private NameValidator nameValidator;
	
	@Autowired
	private QuoteValidator quoteValidator;

	@Autowired
	private TypeValidator typeValidator;

	@Autowired
	private MongoConfigure mongoConfigure;
	
	@Autowired
	private ConsumerServerConfigure consumerServerConfigure;
	
	@Autowired
	private QuoteConfigure quoteConfigure;
	
	@Autowired
	private TopicWhiteListLionEditor topicWhiteListLionEditor;

	@Autowired
	private ConsumerServerLionEditor consumerServerLionEditor;
	
	@Autowired
	private TopicCfgLionEditor topicCfgLionEditor;

	@RequestMapping(value = "/api/topic/apply", method = RequestMethod.POST)
	@ResponseBody
	public Object applyTopic(@RequestBody TopicApplyDto topicApplyDto) {

		authenticationValidator.setNextSuccessor(nameValidator.setNextSuccessor(quoteValidator.setNextSuccessor(typeValidator)));
		
		ResponseStatus responseStatus = authenticationValidator.ValidateTopicApplyDto(topicApplyDto);
		if (responseStatus != ResponseStatus.SUCCESS) {
			return responseStatus;
		}

		mongoConfigure.setNextSuccessor(consumerServerConfigure.setNextSuccessor(quoteConfigure));
		Configure.ConfigureResult configureResult = new Configure.ConfigureResult();
		
		mongoConfigure.buildConfigure(topicApplyDto, configureResult);
		if(configureResult.getResponseStatus() != ResponseStatus.SUCCESS){
			return configureResult.getResponseStatus();
		}

		LionConfigBean lionConfigBean = new LionConfigBean();
		String topic = topicApplyDto.getTopic().trim();
		
		lionConfigBean.setTopic(topic);
		lionConfigBean.setConfigureResult(configureResult);
		lionConfigBean.setTest(topicApplyDto.isTest());

		topicWhiteListLionEditor.setNextSuccessor(consumerServerLionEditor.setNextSuccessor(topicCfgLionEditor));
		responseStatus = topicWhiteListLionEditor.editLion(lionConfigBean);
		if (responseStatus != ResponseStatus.SUCCESS) {
			return responseStatus;
		}

		boolean success = topicResourceService.updateTopicAdministrator(topic, topicApplyDto.getApplicant());
		if (!success) {
			return responseStatus;
		}

		return ResponseStatus.SUCCESS;
	}
	
}
