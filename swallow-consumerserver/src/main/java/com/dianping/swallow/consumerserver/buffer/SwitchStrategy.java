package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.consumerserver.buffer.impl.MessageRingBuffer;

/**
 * @author qi.yin
 *         2016/03/03  上午11:11.
 */
public interface SwitchStrategy {

    boolean isSwitch();

    void switched(MessageRingBuffer.ReaderStatus result);
}
