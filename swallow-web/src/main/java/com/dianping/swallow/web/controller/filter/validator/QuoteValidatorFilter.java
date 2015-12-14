package com.dianping.swallow.web.controller.filter.validator;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.filter.Filter;
import com.dianping.swallow.web.controller.filter.FilterChain;
import com.dianping.swallow.web.controller.filter.result.ValidatorFilterResult;


/**
 * @author mingdongli
 *
 * 2015年9月24日上午11:05:52
 */
@Component
public class QuoteValidatorFilter implements Filter<TopicApplyDto, ValidatorFilterResult> {
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String EXCEED_QUOTA = "申请量太大，请减小每天消息数量或者消息大小，如不行请发送邮件到mingdong.li@dianping.com进行申请。";

	@Override
	public void doFilter(TopicApplyDto topicApplyDto, ValidatorFilterResult result, FilterChain<TopicApplyDto, ValidatorFilterResult> validatorChain) {
		
		int size = topicApplyDto.getSize();
		float amount = topicApplyDto.getAmount();
		
		boolean isPass;
		if (size > 500 || size <= 0 || amount <= 0) {
			isPass = false;
		} else {
			isPass = size * amount <= 700.0f;
		}
		
		if(isPass){
			if(logger.isInfoEnabled()){
				logger.info("Pass QuoteValidator");
			}
			validatorChain.doFilter(topicApplyDto, result, validatorChain);
			
		}else{
			if(logger.isInfoEnabled()){
				logger.info("Fail QuoteValidator");
			}
			result.setMessage(EXCEED_QUOTA);
			result.setStatus(-12);
			return;
		}
	}
	
}
