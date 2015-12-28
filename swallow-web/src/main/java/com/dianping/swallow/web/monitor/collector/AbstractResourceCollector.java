package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
/**
 * 
 * @author qiyin
 *
 *         2015年9月30日 上午11:45:47
 */
public abstract class AbstractResourceCollector extends AbstractLifecycle implements CollectorLifecycle {

	protected Logger logger = LogManager.getLogger(getClass());

	protected final static String CAT_TYPE = "ResourceCollector";

	protected static final String FACTORY_NAME = "ResourceCollector";

	protected String collectorName;

}
