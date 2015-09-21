package com.dianping.swallow.web.controller.validator;

import org.apache.commons.lang.StringUtils;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.model.resource.MongoType;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月21日上午11:39:37
 */
public class TypeValidator extends AbstractValidator implements Validator{

	private Validator nextSuccessor;
	
	public TypeValidator(){
		
	}

	public TypeValidator(Validator nextSuccessor) {
		this.nextSuccessor = nextSuccessor;
	}

	@Override
	public ResponseStatus ValidateTopicApplyDto(TopicApplyDto topicApplyDto) {

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
