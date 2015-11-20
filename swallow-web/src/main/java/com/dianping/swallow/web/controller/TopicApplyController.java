package com.dianping.swallow.web.controller;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.FilterChainFactory;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.controller.filter.validator.*;
import com.dianping.swallow.web.controller.handler.HandlerChainFactory;
import com.dianping.swallow.web.controller.handler.config.ConfigureHandlerChain;
import com.dianping.swallow.web.controller.handler.config.ConsumerServerHandler;
import com.dianping.swallow.web.controller.handler.config.MongoServerHandler;
import com.dianping.swallow.web.controller.handler.config.QuoteHandler;
import com.dianping.swallow.web.controller.handler.data.EmptyObject;
import com.dianping.swallow.web.controller.handler.data.LionEditorEntity;
import com.dianping.swallow.web.controller.handler.lion.ConsumerServerLionHandler;
import com.dianping.swallow.web.controller.handler.lion.LionHandlerChain;
import com.dianping.swallow.web.controller.handler.lion.TopicCfgLionHandler;
import com.dianping.swallow.web.controller.handler.lion.TopicWhiteListLionHandler;
import com.dianping.swallow.web.controller.handler.result.LionConfigureResult;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.Set;

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
	private FilterChainFactory filterChainFactory;

	@Autowired
	private HandlerChainFactory handlerChainFactory;

	@Autowired
	private AuthenticationValidatorFilter authenticationValidatorFilter;

	@Autowired
	private SwitchValidatorFilter switchValidatorFilter;

	@Autowired
	private NameValidatorFilter nameValidatorFilter;

	@Autowired
	private QuoteValidatorFilter quoteValidatorFilter;

	@Autowired
	private TypeValidatorFilter typeValidatorFilter;

	@Autowired
	private ApplicantValidatorFilter applicantValidatorFilter;

	@Autowired
	private MongoServerHandler mongoServerHandler;

	@Autowired
	private ConsumerServerHandler consumerServerHandler;

	@Autowired
	private QuoteHandler quoteHandler;

	@Autowired
	private TopicWhiteListLionHandler topicWhiteListLionHandler;

	@Autowired
	private ConsumerServerLionHandler consumerServerLionHandler;

	@Autowired
	private TopicCfgLionHandler topicCfgLionHandler;

	private Object APPLY_TOPIC = new Object();

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@RequestMapping(value = "/api/topic/apply", method = RequestMethod.POST)
	@ResponseBody
	public Object applyTopic(@RequestBody TopicApplyDto topicApplyDto) {

		ValidatorFilterResult validatorFilterResult = new ValidatorFilterResult();
		ValidatorFilterChain validatorFilterChain = filterChainFactory.createValidatorFilterChain();

		validatorFilterChain.addFilter(switchValidatorFilter);
		validatorFilterChain.addFilter(authenticationValidatorFilter);
		validatorFilterChain.addFilter(nameValidatorFilter);
		validatorFilterChain.addFilter(quoteValidatorFilter);
		validatorFilterChain.addFilter(typeValidatorFilter);
		validatorFilterChain.addFilter(applicantValidatorFilter);
		validatorFilterChain.doFilter(topicApplyDto, validatorFilterResult, validatorFilterChain);

		if (validatorFilterResult.getStatus() != 0) {
			return validatorFilterResult;
		}

		LionConfigureResult lionConfigureResult = new LionConfigureResult();
		ConfigureHandlerChain configureHandlerChain = handlerChainFactory.createConfigureHandlerChain();

		configureHandlerChain.addHandler(mongoServerHandler);
		configureHandlerChain.addHandler(consumerServerHandler);
		configureHandlerChain.addHandler(quoteHandler);
		ResponseStatus status = configureHandlerChain.handle(topicApplyDto, lionConfigureResult);

		if (status != ResponseStatus.SUCCESS) {
			return status;
		}

		EmptyObject emptyObject = new EmptyObject();
		LionEditorEntity lionEditorEntity = new LionEditorEntity();
		String topic = topicApplyDto.getTopic().trim();
		boolean isTest = topicApplyDto.isTest();

		lionEditorEntity.setTopic(topic);
		lionEditorEntity.setTest(isTest);
		lionEditorEntity.setMongoServer(lionConfigureResult.getMongoServer());
		lionEditorEntity.setConsumerServer(lionConfigureResult.getConsumerServer());
		lionEditorEntity.setSize4SevenDay(lionConfigureResult.getSize4SevenDay());

		LionHandlerChain lionHandlerChain = handlerChainFactory.createLionHandlerChain();
		lionHandlerChain.addHandler(topicWhiteListLionHandler);
		lionHandlerChain.addHandler(consumerServerLionHandler);
		lionHandlerChain.addHandler(topicCfgLionHandler);
		status = lionHandlerChain.handle(lionEditorEntity, emptyObject);

		if (status != ResponseStatus.SUCCESS) {
			return status;
		}

		String applicant = topicApplyDto.getApplicant();
		Set<String> administrator = new HashSet<String>();
		administrator.add(applicant.trim());

		boolean isSuccess;
		synchronized (APPLY_TOPIC) {
			isSuccess = topicResourceService.updateTopicAdministrator(topic, administrator);
		}
		return isSuccess ? ResponseStatus.SUCCESS : ResponseStatus.MONGOWRITE;
	}

}
