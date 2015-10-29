package com.dianping.swallow.web.controller.filter.validator;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;


/**
 * @author mingdongli
 *
 * 2015年10月12日下午12:26:46
 */
@Component
public class ApplicantValidatorFilter implements  Filter<TopicApplyDto, ValidatorFilterResult>{

	protected final Logger logger = LogManager.getLogger(getClass());
	
	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result,
			FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {
		
		String applicant = topicApplyDto.getApplicant();
		
		if(StringUtils.isNotBlank(applicant)){
			if(logger.isInfoEnabled()){
				logger.info("Pass ApplicantValidatorFilter");
			}
			validatorChain.doFilter(topicApplyDto, result, validatorChain);
			
		}else{
			if(logger.isInfoEnabled()){
				logger.info("Fail ApplicantValidatorFilter");
			}
			result.setMessage("Applicant must not be blank");
			result.setStatus(-4);
			return;
		}
		
	}

}
