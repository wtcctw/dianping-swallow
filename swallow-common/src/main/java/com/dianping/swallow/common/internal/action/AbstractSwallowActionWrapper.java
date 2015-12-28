package com.dianping.swallow.common.internal.action;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;


/**
 * @author mengwenchao
 *
 * 2015年4月30日 下午2:39:49
 */
public abstract class AbstractSwallowActionWrapper implements SwallowActionWrapper{
	
	protected transient final Logger logger = LogManager.getLogger(getClass());

}
