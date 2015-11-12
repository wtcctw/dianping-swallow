package com.dianping.swallow.web.controller.filter;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午1:31:04
 */
public abstract class AbstractFilterChain<T, R> {
	
	protected List<Filter<T, R>> validators = new ArrayList<Filter<T, R>>();
	
	protected int index = 0;
	
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	
	public void resetFilterChain(){
		index = 0;
	}

}
