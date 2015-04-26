package com.dianping.swallow.common.server.monitor.visitor;


import java.util.List;


/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:07:15
 */
public interface ConsumerMonitorVisitor extends MonitorTopicVisitor{
	

	List<Long> buildSendDelay(int intervalTimeSeconds);

	List<Long> buildAckDelay(int intervalTimeSeconds);

	List<Long> buildSendQpx(int intervalTimeSeconds, QPX qpx);

	List<Long> buildAckQpx(int intervalTimeSeconds, QPX qpx);

}
