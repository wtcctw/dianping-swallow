package com.dianping.swallow.web.controller.filter.validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;
import com.dianping.swallow.web.controller.utils.UserUtils;


/**
 * @author mingdongli
 *
 * 2015年9月24日上午11:05:52
 */
@Component
public class AuthenticationValidatorFilter implements  Filter<TopicApplyDto, ValidatorFilterResult> {
	
	protected final Logger logger = LogManager.getLogger(getClass());
	
	@Autowired
	private UserUtils userUtils;

	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result, FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {
		
		String approver = topicApplyDto.getApprover();
		boolean isPass = userUtils.isAdministrator(approver, true);
		
		if(isPass){
			if(logger.isInfoEnabled()){
				logger.info("Pass AuthenticationFilter");
			}
			validatorChain.doFilter(topicApplyDto, result, validatorChain);
			
		}else{
			if(logger.isInfoEnabled()){
				logger.info("Fail AuthenticationFilter");
			}
			result.setMessage("unauthenrized");
			result.setStatus(-2);
			return;
		}
	}
	
	public void setUserUtils(UserUtils userUtils) {
		this.userUtils = userUtils;
	}

}
