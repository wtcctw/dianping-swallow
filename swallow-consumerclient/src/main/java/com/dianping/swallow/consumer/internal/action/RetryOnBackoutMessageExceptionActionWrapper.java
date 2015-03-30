package com.dianping.swallow.consumer.internal.action;


import com.dianping.swallow.common.internal.threadfactory.PullStrategy;
import com.dianping.swallow.consumer.BackoutMessageException;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午4:23:25
 */
public class RetryOnBackoutMessageExceptionActionWrapper extends AbstractRetryWrapper{

	public RetryOnBackoutMessageExceptionActionWrapper(PullStrategy pullStrategy, int totalRetryCount) {
		super(pullStrategy, totalRetryCount);
	}

	@Override
	protected Class<?> getExceptionRetryClass() {
		return BackoutMessageException.class;
	}


	
}
