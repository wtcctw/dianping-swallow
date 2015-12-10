package com.dianping.swallow.web.controller.filter.validator;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	public static final String MAIL_POSTFIX = "@dianping.com";

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result,
			FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {
		
		String applicant = topicApplyDto.getApplicant();
		
		if(StringUtils.isNotBlank(applicant)){
			String trimedApplicant = applicant.trim();
			int index = trimedApplicant.indexOf(MAIL_POSTFIX);
			if(index != -1){
				String fixedApplicant = trimedApplicant.substring(0, index);
				topicApplyDto.setApplicant(fixedApplicant);
				if (logger.isWarnEnabled()) {
					logger.warn(String.format("Modify approver name %s to %s", applicant, fixedApplicant));
				}
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
