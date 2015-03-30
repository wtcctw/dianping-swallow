package com.dianping.swallow.common.internal.processor;

import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午2:31:28
 */
public interface ConsumerProcessor {
	
	void beforeOnMessage(SwallowMessage message) throws SwallowException;
	
	void afterOnMessage(SwallowMessage message) throws SwallowException;
}
