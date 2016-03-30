package com.dianping.swallow.consumerserver.buffer;


import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.consumerserver.buffer.impl.MessageRingBuffer;

import java.util.List;

/**
 * @author qi.yin
 *         2016/03/02  上午10:25.
 */
public interface CloseableRingBuffer<E> {

    void putMessage(E message);

    void putMessages(List<E> messages);

    MessageRingBuffer.BufferReader getOrCreateReader(String consumerId);

    /**
     * @param tailId
     */
    void setTailMessageId(Long tailId);

    /**
     * @return
     */
    Long getTailMessageId();

    /**
     * @param messageRetriever
     */
    void setMessageRetriever(MessageRetriever messageRetriever);


    void fetchMessage(MessageRingBuffer.BufferReader bufferReader,long lastMessageId);

}
