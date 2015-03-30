package com.dianping.swallow.common.internal.processor;

import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *
 * 2015年3月27日 下午2:31:28
 */
public interface ProducerProcessor{
	
	void beforeSend(SwallowMessage message) throws SwallowException;
}
