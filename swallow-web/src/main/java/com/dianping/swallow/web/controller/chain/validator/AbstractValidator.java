package com.dianping.swallow.web.controller.chain.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mingdongli
 *
 * 2015年9月18日下午5:19:05
 */
public abstract class AbstractValidator implements Validator{
	
	protected Validator nextSuccessor;

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public AbstractValidator(){
		
	}
	
	public AbstractValidator(Validator nextSuccessor){
		this.nextSuccessor = nextSuccessor;
	}

	public Validator setNextSuccessor(Validator nextSuccessor) {
		this.nextSuccessor = nextSuccessor;
		return this;
	}
	
}
