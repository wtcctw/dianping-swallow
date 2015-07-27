package com.dianping.swallow.web.alarm;

import com.dianping.swallow.web.model.event.Event;

public interface EventReporter {

	public void report(Event event);
	
}
