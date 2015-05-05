package com.dianping.swallow.common.server.monitor.data;

import com.dianping.swallow.common.internal.util.DateUtils;
import com.dianping.swallow.common.server.monitor.MonitorException;

/**
 * 时间异常
 * @author mengwenchao
 *
 * 2015年4月30日 下午2:29:03
 */
public class TimeException extends MonitorException{

	private static final long serialVersionUID = 1L;

	private long startTime;
	private long endTime;
	public TimeException(String message, long startTime, long endTime) {
		super(message);
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	@Override
	public String getMessage() {
		
		return "["+ super.getMessage()+"]" + "startTime:" + DateUtils.toPrettyFormat(startTime) +  
				",endTime:"  + DateUtils.toPrettyFormat(endTime);
	}

}
