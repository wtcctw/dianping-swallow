package com.dianping.swallow.web.monitor.wapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
*
* @author qiyin
*
*/
public abstract class AbstractDataWapper {
	
	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected static final long DEFAULT_VALUE = -1L;
	
	protected static final String TOTAL_KEY = "total";
	
}
