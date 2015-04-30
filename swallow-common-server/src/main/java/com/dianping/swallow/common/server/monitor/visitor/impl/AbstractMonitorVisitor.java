package com.dianping.swallow.common.server.monitor.visitor.impl;

import java.util.LinkedList;
import java.util.List;

import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.MonitorData.MessageInfo;
import com.dianping.swallow.common.server.monitor.visitor.MonitorTopicVisitor;
import com.dianping.swallow.common.server.monitor.visitor.QPX;

/**
 * @author mengwenchao
 *
 * 2015年4月22日 下午5:17:58
 */
public abstract class AbstractMonitorVisitor implements MonitorTopicVisitor{

	protected String topic;
	
	protected QPX  qpx = QPX.MINUTE;
	
	
	public AbstractMonitorVisitor(String topic) {
		
		this.topic = topic;
	}

	
	@Override
	public String getVisitTopic() {
		
		return topic;
	}
	
	
	protected List<Long> buildQpx(List<MessageInfo> rawData, int intervalTimeSeconds, QPX qpx) {

		
		int intervalCount = getRealIntervalCount(intervalTimeSeconds, qpx);
		int realintervalTimeSeconds = getRealIntervalTimeSeconds(intervalTimeSeconds, qpx);
		double realIntervalTimeMinutes = (double)realintervalTimeSeconds/60;
		
		List<Long> result = new LinkedList<Long>();
		
		int step = 0;
		long count = 0, lastCount = 0;
		
		for(MessageInfo info : rawData){
			
			if(step != 0){
				count += info.getTotal() - lastCount;
			}
			lastCount = info.getTotal();
			
			if(step >= intervalCount){
				if(count >= 0){
					switch(qpx){
						case SECOND:
							result.add(count/realintervalTimeSeconds);
						break;
						case MINUTE:
							result.add((long)(count/realIntervalTimeMinutes));
						break;
					}
					
				}else{
					result.add(0L);
				}
				step  = 0;
				count = 0;
				continue;
			}
			step++;

		}
		
		
		return result;
	}

	public List<Long> buildDelay(int intervalTimeSeconds, List<MessageInfo> rawData){
		
		int  intervalCount = getRealIntervalCount(intervalTimeSeconds);
		
		List<Long> result = new LinkedList<Long>();
		
		int step = 0;
		long delay = 0, lastDelay = 0;
		long count = 0, lastCount = 0;
		for(MessageInfo info : rawData){
			
			if(step != 0){
				
				count += info.getTotal() - lastCount;
				delay += info.getTotalDelay() - lastDelay;
				
			}
			
			lastCount = info.getTotal();
			lastDelay = info.getTotalDelay();
			
			
			if(step >= intervalCount){
				if(delay < 0){
					delay = 0;
				}
				if(count != 0){
					result.add(delay/count);
				}else{
					result.add(0L);
				}
				step  = 0;
				count = 0;
				delay = 0;
				continue;
			}
			step++;

		}
		
		return result;
	}

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
