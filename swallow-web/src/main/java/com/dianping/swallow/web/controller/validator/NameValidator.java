package com.dianping.swallow.web.controller.validator;

import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ResponseStatus;

/**
 * @author mingdongli
 *
 *         2015年9月18日下午4:06:08
 */
@Component
public class NameValidator extends AbstractValidator implements Validator{
	
	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;

	private Validator nextSuccessor;
	
	public NameValidator(){
		
	}

	public NameValidator(Validator nextSuccessor) {
		this.nextSuccessor = nextSuccessor;
	}

	@Override
	public ResponseStatus ValidateTopicApplyDto(TopicApplyDto topicApplyDto) {

		String topic = topicApplyDto.getTopic();
		boolean pass = validateTopicName(topic);
		
		if(pass){
			
			if(nextSuccessor != null){
				return nextSuccessor.ValidateTopicApplyDto(topicApplyDto);
			}else{
				return ResponseStatus.SUCCESS;
			}
			
		}else{
			return ResponseStatus.INVALIDTOPICNAME;
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
		Set<String> allTopics = topicResourceService.loadCachedTopicToWhiteList().keySet();
		if (allTopics.contains(topic)) {
			if(logger.isInfoEnabled()){
				logger.info("Fail NameValidator, Same Topic");
			}
			return false;
		}

		if(logger.isInfoEnabled()){
			logger.info("Pass NameValidator");
		}
		return true;
	}

	public void setTopicResourceService(TopicResourceService topicResourceService) {
		this.topicResourceService = topicResourceService;
	}
	
}
