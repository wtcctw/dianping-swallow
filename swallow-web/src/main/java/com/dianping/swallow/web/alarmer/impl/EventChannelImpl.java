package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.alarmer.EventChannel;
import com.dianping.swallow.web.model.event.Event;

/**
 * 
 * @author qiyin
 *
 * 2015年8月3日 下午6:06:41
 */
@Component
public class EventChannelImpl implements EventChannel {

	private BlockingQueue<Event> eventBuffer;

	private int bufferSize = 5000;

	public EventChannelImpl() {
		eventBuffer = new ArrayBlockingQueue<Event>(bufferSize);
	}

	public EventChannelImpl(int bufferSize) {
		this.bufferSize = bufferSize;
		eventBuffer = new ArrayBlockingQueue<Event>(bufferSize);
	}

	@Override
	public void put(Event event) throws InterruptedException {
		eventBuffer.put(event);
	}

	@Override
	public Event next() throws InterruptedException {
		return eventBuffer.take();
	}

}
