package com.dianping.swallow.web.controller.filter.validator;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.service.TopicResourceService;


/**
 * @author mingdongli
 *
 * 2015年9月24日上午11:05:52
 */
@Component
public class NameValidatorFilter implements Filter<TopicApplyDto, ValidatorFilterResult> {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;
	
	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result, FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {
		
		String topic = topicApplyDto.getTopic();
		boolean isPass = validateTopicName(topic);
		
		if(isPass){
			if(logger.isInfoEnabled()){
				logger.info("Pass NameValidator");
			}
			validatorChain.doFilter(topicApplyDto, result, validatorChain);
		}else{
			if(logger.isInfoEnabled()){
				logger.info("Fail NameValidator");
			}
			result.setMessage("invalid topic name");
			result.setStatus(-11);
			return;
		}
	}
	
	private boolean validateTopicName(String topic) {

		if (StringUtils.isBlank(topic)) {
			if(logger.isInfoEnabled()){
				logger.info("Fail NameValidator, Blank Topic");
			}
			return false;
		}
		if (!NameCheckUtil.isTopicNameValid(topic)) {
			if(logger.isInfoEnabled()){
				logger.info("Fail NameValidator, Invalid Topic");
			}
			return false;
		}
		Set<String> allTopics = topicResourceService.loadCachedTopicToAdministrator().keySet();
		if (allTopics.contains(topic)) {
			if(logger.isInfoEnabled()){
				logger.info("Fail NameValidator, Same Topic");
			}
			return false;
		}

		return true;
	}
	
	public void setTopicResourceService(TopicResourceService topicResourceService) {
		this.topicResourceService = topicResourceService;
	}

}
