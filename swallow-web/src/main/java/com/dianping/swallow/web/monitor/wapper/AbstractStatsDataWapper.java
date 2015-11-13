package com.dianping.swallow.web.monitor.wapper;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

public abstract class AbstractStatsDataWapper {

	protected Logger logger = LoggerFactory.getLogger(getClass());

	protected static final long DEFAULT_VALUE = -1L;

	protected static final String TOTAL_KEY = MonitorData.TOTAL_KEY;
}
