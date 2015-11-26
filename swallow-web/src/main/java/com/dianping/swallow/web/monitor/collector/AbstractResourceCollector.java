package com.dianping.swallow.web.monitor.collector;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
/**
 * 
 * @author qiyin
 *
 *         2015年9月30日 上午11:45:47
 */
public abstract class AbstractResourceCollector extends AbstractLifecycle implements CollectorLifecycle {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected final static String CAT_TYPE = "ResourceCollector";

	protected static final String FACTORY_NAME = "ResourceCollector";

	protected String collectorName;

}
