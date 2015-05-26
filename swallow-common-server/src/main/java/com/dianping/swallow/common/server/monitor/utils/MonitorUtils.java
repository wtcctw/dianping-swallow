package com.dianping.swallow.common.server.monitor.utils;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.QPX;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午6:44:09
 */
public class MonitorUtils {

	public static int getRealIntervalCount(int intervalTimeSeconds) {
		
		return getRealIntervalCount(intervalTimeSeconds, QPX.SECOND);
	}

	public static int getRealIntervalTimeSeconds(int intervalTimeSeconds) {
		
		return getRealIntervalTimeSeconds(intervalTimeSeconds, QPX.SECOND);
	}

	public static int getRealIntervalCount(int intervalTimeSeconds, QPX qpx) {
		
		if(qpx == QPX.MINUTE){
			intervalTimeSeconds = 60;
		}

		return (int) Math.ceil((double)intervalTimeSeconds/AbstractCollector.SEND_INTERVAL);
	}

	public static int getRealIntervalTimeSeconds(int intervalTimeSeconds, QPX qpx) {
		
		return getRealIntervalCount(intervalTimeSeconds, qpx) * AbstractCollector.SEND_INTERVAL;
	}

}
