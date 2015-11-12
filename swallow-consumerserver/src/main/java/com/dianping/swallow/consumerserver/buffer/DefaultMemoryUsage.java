package com.dianping.swallow.consumerserver.buffer;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 *
 * 2015年3月4日 上午10:13:51
 */
public class DefaultMemoryUsage implements MemoryUsage{
	
	private  final Logger          logger  = LoggerFactory.getLogger(getClass());

	public static final long _1MB = 1024*1024;
	private AtomicLong usage = new AtomicLong();
	private long limit = 50 * _1MB;

	@Override
	public void increase(long bytes) {
		usage.addAndGet(bytes);
	}

	@Override
	public boolean isFull() {
		return usage.get() >= limit;
	}

	@Override
	public void setLimit(long bytes) {
		if(logger.isInfoEnabled()){
			logger.info("[setLimit][bytes]" + bytes);
		}
		limit = bytes;
	}

	@Override
	public long getLimit() {
		return limit;
	}

	@Override
	public void decrease(long bytes) {
		usage.addAndGet(-bytes);
	}

}
