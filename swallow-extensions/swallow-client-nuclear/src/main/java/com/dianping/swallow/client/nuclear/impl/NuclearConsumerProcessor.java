package com.dianping.swallow.client.nuclear.impl;

import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.processor.ConsumerProcessor;

/**
 * @author qi.yin
 *         2015/12/22  上午11:51.
 */
public class NuclearConsumerProcessor implements ConsumerProcessor {

    @Override
    public void beforeOnMessage(SwallowMessage message) throws SwallowException {
        //nuclearmq目前不需要处理
    }

    @Override
    public void afterOnMessage(SwallowMessage message) throws SwallowException {
        //nuclearmq目前不需要处理
    }
}
