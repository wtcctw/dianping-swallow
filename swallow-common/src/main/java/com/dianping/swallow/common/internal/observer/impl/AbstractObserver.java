package com.dianping.swallow.common.internal.observer.impl;


import com.dianping.swallow.common.internal.observer.Observer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author mengwenchao
 *
 * 2015年6月11日 下午6:06:44
 */
public abstract class AbstractObserver implements Observer{

	protected Logger logger = LogManager.getLogger(getClass());
	
}
