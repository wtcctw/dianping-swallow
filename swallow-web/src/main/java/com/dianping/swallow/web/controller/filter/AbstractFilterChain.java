package com.dianping.swallow.web.controller.filter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午1:31:04
 */
public abstract class AbstractFilterChain<T, R> {
	
	protected List<Filter<T, R>> validators = new ArrayList<Filter<T, R>>();
	
	protected int index = 0;
	
	protected final Logger logger = LogManager.getLogger(getClass());
	
	public void resetFilterChain(){
		index = 0;
	}

}
