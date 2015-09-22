package com.dianping.swallow.web.controller.chain.validator;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月21日上午11:39:37
 */
@Component
public class TypeValidator extends AbstractValidator implements Validator{

	public TypeValidator(){
		super();
	}

	public TypeValidator(Validator nextSuccessor) {
		super(nextSuccessor);
	}

	@Override
	public ResponseStatus ValidateTopicApplyDto(final TopicApplyDto topicApplyDto) {

		String type = topicApplyDto.getType();
		if(StringUtils.isBlank(type)){
			if(logger.isInfoEnabled()){
				logger.info("Fail TypeValidator");
			}
			return ResponseStatus.INVALIDTYPE;
		}
		
		try{
			MongoType.findByType(type.trim());
			if(logger.isInfoEnabled()){
				logger.info("Pass TypeValidator");
			}
			
			if(nextSuccessor != null){
				return nextSuccessor.ValidateTopicApplyDto(topicApplyDto);
			}else{
				return ResponseStatus.SUCCESS;
			}
			
		}catch(Exception e){
			if(logger.isInfoEnabled()){
				logger.info("Fail TypeValidator");
			}
			return ResponseStatus.INVALIDTYPE;
		}
	}
}
