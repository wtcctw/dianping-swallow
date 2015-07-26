package com.dianping.swallow.web.alarm;

import com.dianping.swallow.web.model.event.Event;

public interface EventChannel {

	public void put(Event event) throws InterruptedException;

	public Event next() throws InterruptedException;

}
