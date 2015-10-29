package com.dianping.swallow.web.monitor.wapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

public abstract class AbstractStatsDataWapper {

	protected Logger logger = LogManager.getLogger(getClass());

	protected static final long DEFAULT_VALUE = -1L;

	protected static final String TOTAL_KEY = MonitorData.TOTAL_KEY;
}
