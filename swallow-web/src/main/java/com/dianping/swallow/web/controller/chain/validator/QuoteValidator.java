package com.dianping.swallow.web.controller.chain.validator;

import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月18日下午4:24:56
 */
@Component
public class QuoteValidator extends AbstractValidator implements Validator{

	public QuoteValidator(){
		super();
	}

	public QuoteValidator(Validator nextSuccessor) {
		super(nextSuccessor);
	}

	@Override
	public ResponseStatus ValidateTopicApplyDto(final TopicApplyDto topicApplyDto) {

		int size = topicApplyDto.getSize();
		float amount = topicApplyDto.getAmount();
		
		boolean pass;
		if (size > 500 || size <= 0 || amount <= 0) {
			pass = false;
		} else {
			pass = size * amount <= 700.0f;
		}
		
		if(pass){
			if(logger.isInfoEnabled()){
				logger.info("Pass QuoteValidator");
			}
			if(nextSuccessor != null){
				return nextSuccessor.ValidateTopicApplyDto(topicApplyDto);
			}else{
				return ResponseStatus.SUCCESS;
			}
			
		}else{
			if(logger.isInfoEnabled()){
				logger.info("Fail QuoteValidator");
			}
			return ResponseStatus.TOOLARGEQUOTA;
		}
	}
	
}
