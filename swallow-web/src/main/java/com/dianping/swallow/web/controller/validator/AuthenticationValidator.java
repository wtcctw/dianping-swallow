package com.dianping.swallow.web.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.controller.dto.TopicApplyDto;
import com.dianping.swallow.web.controller.utils.UserUtils;
import com.dianping.swallow.web.util.ResponseStatus;


/**
 * @author mingdongli
 *
 * 2015年9月18日下午4:23:25
 */
@Component
public class AuthenticationValidator extends AbstractValidator implements Validator{

	@Autowired
	protected UserUtils userUtils;
	
	private Validator nextSuccessor;
	
	public AuthenticationValidator(){
		
	}

	public AuthenticationValidator(Validator nextSuccessor) {
		this.nextSuccessor = nextSuccessor;
	}
	
	@Override
	public ResponseStatus ValidateTopicApplyDto(TopicApplyDto topicApplyDto) {

		String approver = topicApplyDto.getApprover();
		boolean pass = userUtils.isTrueAdministrator(approver);
		
		if(pass){
			if(logger.isInfoEnabled()){
				logger.info("Pass AuthenticationValidator");
			}
			if(nextSuccessor != null){
				return nextSuccessor.ValidateTopicApplyDto(topicApplyDto);
			}else{
				return ResponseStatus.SUCCESS;
			}
			
		}else{
			if(logger.isInfoEnabled()){
				logger.info("Fail AuthenticationValidator");
			}
			return ResponseStatus.UNAUTHENTICATION;
		}
	}

	public void setUserUtils(UserUtils userUtils) {
		this.userUtils = userUtils;
	}
	
}
