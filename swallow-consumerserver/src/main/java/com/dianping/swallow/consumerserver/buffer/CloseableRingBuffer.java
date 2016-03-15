package com.dianping.swallow.consumerserver.buffer;


import java.util.List;

/**
 * @author qi.yin
 *         2016/03/02  上午10:25.
 */
public interface CloseableRingBuffer<E> {

    void putMessage(E message);

    void putMessages(List<E> messages);

    E getMessage(String consumerId);

    boolean setMessageId(String consumerId, Long messageId);

    void close(String consumerId);

}
