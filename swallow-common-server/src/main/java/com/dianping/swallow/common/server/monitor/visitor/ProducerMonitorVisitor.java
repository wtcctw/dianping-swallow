package com.dianping.swallow.common.server.monitor.visitor;


import java.util.List;

import com.dianping.swallow.common.server.monitor.data.QPX;

/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:07:15
 */
public interface ProducerMonitorVisitor extends MonitorTopicVisitor{
	

	List<Long> buildSaveDelay(int intervalTimeSeconds);
	
	List<Long> buildSaveQpx(int intervalTimeSeconds, QPX qpx);

}
