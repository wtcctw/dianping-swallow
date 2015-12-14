package com.dianping.swallow.web.controller.filter.validator;

import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.service.TopicResourceService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;


/**
 * @author mingdongli
 *
 * 2015年9月24日上午11:05:52
 */
@Component
public class NameValidatorFilter implements Filter<TopicApplyDto, ValidatorFilterResult> {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String TOPIC_BLANK = "topic名称不能为空！";

	private static final String TOPIC_DUPLICATE = "topic名称已经存在，请更换topic名称";

	private static final String TOPIC_INVALID = "topic名称命名不规范";

	private static final String TOPIC_PASS = "true";

	@Resource(name = "topicResourceService")
	private TopicResourceService topicResourceService;
	
	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result, FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {
		
		String topic = topicApplyDto.getTopic();
		String isPass = validateTopicName(topic);
		
		if(TOPIC_PASS.equals(isPass)){
			if(logger.isInfoEnabled()){
				logger.info("Pass NameValidator");
			}
			validatorChain.doFilter(topicApplyDto, result, validatorChain);
		}else{
			result.setMessage(isPass);
			result.setStatus(-11);
			return;
		}
	}
	
	private String validateTopicName(String topic) {

		if (StringUtils.isBlank(topic)) {
			if(logger.isInfoEnabled()){
				logger.info(TOPIC_BLANK);
			}
			return TOPIC_BLANK;
		}
		if (!NameCheckUtil.isTopicNameValid(topic)) {
			if(logger.isInfoEnabled()){
				logger.info(TOPIC_INVALID);
			}
			return TOPIC_INVALID;
		}
		Set<String> allTopics = topicResourceService.loadCachedTopicToAdministrator().keySet();
		if (allTopics.contains(topic)) {
			if(logger.isInfoEnabled()){
				logger.info(TOPIC_DUPLICATE);
			}
			return TOPIC_DUPLICATE;
		}

		return TOPIC_PASS;
	}
	
	public void setTopicResourceService(TopicResourceService topicResourceService) {
		this.topicResourceService = topicResourceService;
	}

}
