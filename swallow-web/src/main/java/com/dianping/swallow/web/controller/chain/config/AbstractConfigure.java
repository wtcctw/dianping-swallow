package com.dianping.swallow.web.controller.chain.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author mingdongli
 *
 * 2015年9月21日下午7:51:36
 */
public abstract class AbstractConfigure implements Configure{
	
	protected Configure nextSuccessor;

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public AbstractConfigure(){
		
	}
	
	public AbstractConfigure(Configure nextSuccessor){
		this.nextSuccessor = nextSuccessor;
	}

	public Configure setNextSuccessor(Configure nextSuccessor) {
		this.nextSuccessor = nextSuccessor;
		return this;
	}
}
