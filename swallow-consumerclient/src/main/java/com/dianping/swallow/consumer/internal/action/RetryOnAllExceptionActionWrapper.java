package com.dianping.swallow.consumer.internal.action;

import com.dianping.swallow.common.internal.threadfactory.PullStrategy;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午4:27:57
 */
public class RetryOnAllExceptionActionWrapper extends AbstractRetryWrapper{

	public RetryOnAllExceptionActionWrapper(PullStrategy pullStrategy, int totalRetryCount) {
		super(pullStrategy, totalRetryCount);
	}

	@Override
	protected Class<?> getExceptionRetryClass() {
		return Throwable.class;
	}

}
