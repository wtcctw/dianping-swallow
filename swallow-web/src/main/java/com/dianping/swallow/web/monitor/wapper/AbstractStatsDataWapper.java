package com.dianping.swallow.web.monitor.wapper;

import com.dianping.swallow.common.server.monitor.data.QPX;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;

public abstract class AbstractStatsDataWapper {

	protected Logger logger = LogManager.getLogger(getClass());

	protected static final long DEFAULT_VALUE = -1L;

	protected static final String TOTAL_KEY = MonitorData.TOTAL_KEY;

	protected static final QPX DEFAULT_QPX_TYPE  = QPX.SECOND;
}
