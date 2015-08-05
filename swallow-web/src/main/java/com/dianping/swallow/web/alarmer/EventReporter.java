package com.dianping.swallow.web.alarmer;

import com.dianping.swallow.web.model.event.Event;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午6:05:38
 */
public interface EventReporter {

	public void report(Event event);
	
}
