package com.dianping.swallow.web.alarmer;

import com.dianping.swallow.web.model.event.Event;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午6:05:29
 */
public interface EventChannel {

	public void put(Event event) throws InterruptedException;

	public Event next() throws InterruptedException;

}
