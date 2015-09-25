package com.dianping.swallow.web.controller;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.FilterChainFactory;
import com.dianping.swallow.web.controller.filter.config.ConfigureFilterChain;
import com.dianping.swallow.web.controller.filter.config.ConsumerServerConfigureFilter;
import com.dianping.swallow.web.controller.filter.config.MongoConfigureFilter;
import com.dianping.swallow.web.controller.filter.config.QuoteConfigureFilter;
import com.dianping.swallow.web.controller.filter.lion.ConsumerServerLionFilter;
import com.dianping.swallow.web.controller.filter.lion.LionFilterChain;
import com.dianping.swallow.web.controller.filter.lion.LionFilterEntity;
import com.dianping.swallow.web.controller.filter.lion.TopicCfgLionFilter;
import com.dianping.swallow.web.controller.filter.lion.TopicWhiteListLionFilter;
import com.dianping.swallow.web.controller.filter.result.ConfigureFilterResult;
import com.dianping.swallow.web.controller.filter.result.LionFilterResult;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.controller.filter.validator.AuthenticationValidatorFilter;
import com.dianping.swallow.web.controller.filter.validator.NameValidatorFilter;
import com.dianping.swallow.web.controller.filter.validator.QuoteValidatorFilter;
import com.dianping.swallow.web.controller.filter.validator.TypeValidatorFilter;
import com.dianping.swallow.web.controller.filter.validator.ValidatorFilterChain;
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
	private FilterChainFactory filterChainFactory;

	@Autowired
	private AuthenticationValidatorFilter authenticationValidatorFilter;

	@Autowired
	private NameValidatorFilter nameValidatorFilter;

	@Autowired
	private QuoteValidatorFilter quoteValidatorFilter;

	@Autowired
	private TypeValidatorFilter typeValidatorFilter;

	@Autowired
	private MongoConfigureFilter mongoConfigureFilter;

	@Autowired
	private ConsumerServerConfigureFilter consumerServerConfigureFilter;

	@Autowired
	private QuoteConfigureFilter quoteConfigureFilter;
	
	@Autowired
	private TopicWhiteListLionFilter topicWhiteListLionFilter;

	@Autowired
	private ConsumerServerLionFilter consumerServerLionFilter;

	@Autowired
	private TopicCfgLionFilter topicCfgLionFilter;
	
	@RequestMapping(value = "/api/topic/apply", method = RequestMethod.POST)
	@ResponseBody
	public Object applyTopic(@RequestBody TopicApplyDto topicApplyDto) {

		ValidatorFilterResult validatorFilterResult = new ValidatorFilterResult();
		ValidatorFilterChain validatorFilterChain = filterChainFactory.createValidatorFilterChain();
		
		validatorFilterChain.addFilter(authenticationValidatorFilter);
		validatorFilterChain.addFilter(nameValidatorFilter);
		validatorFilterChain.addFilter(quoteValidatorFilter);
		validatorFilterChain.addFilter(typeValidatorFilter);
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);

		if (validatorFilterResult.getStatus() != 0) {
			return validatorFilterResult;
		}

		ConfigureFilterResult configureFilterResult = new ConfigureFilterResult();
		ConfigureFilterChain configureFilterChain = filterChainFactory.createConfigureFilterChain();
		
		configureFilterChain.addFilter(mongoConfigureFilter);
		configureFilterChain.addFilter(consumerServerConfigureFilter);
		configureFilterChain.addFilter(quoteConfigureFilter);
		configureFilterChain.doFilter(topicApplyDto, configureFilterResult, configureFilterChain);

		if (configureFilterResult.getStatus() != 0) {
			return configureFilterResult;
		}

		LionFilterResult lionFilterResult = new LionFilterResult();
		LionFilterEntity lionFilterEntity = new LionFilterEntity();
		String topic = topicApplyDto.getTopic().trim();
		boolean isTest = topicApplyDto.isTest();

		lionFilterEntity.setTopic(topic);
		lionFilterEntity.setTest(isTest);
		lionFilterEntity.setLionConfigure(configureFilterResult.getLionConfigure());
		
		LionFilterChain lionFilterChain = filterChainFactory.createLionFilterChain();

		lionFilterChain.addFilter(topicWhiteListLionFilter);
		lionFilterChain.addFilter(consumerServerLionFilter);
		lionFilterChain.addFilter(topicCfgLionFilter);
		lionFilterChain.doFilter(lionFilterEntity, lionFilterResult, lionFilterChain);

		if (lionFilterResult.getStatus() != 0) {
			return lionFilterResult;
		}

		boolean isSuccess = topicResourceService.updateTopicAdministrator(topic, topicApplyDto.getApplicant());
		return isSuccess ? ResponseStatus.SUCCESS : ResponseStatus.MONGOWRITE;
	}

}
