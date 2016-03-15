package com.dianping.swallow.consumerserver.buffer.impl;

import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.consumerserver.buffer.CloseableRingBuffer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qi.yin
 *         2016/03/02  上午11:24.
 */
public class MessageRingBuffer implements CloseableRingBuffer<SwallowMessage> {

    //大小必须是2的n次方。（优化了%操作为&）
    private int bufferSize = 1024 * 8;

    private SwallowMessage[] messageBuffers;

    private AtomicLong head = new AtomicLong(0);

    private ConcurrentHashMap<String, MessageReader> messageReaders = new ConcurrentHashMap<String, MessageReader>();

    public MessageRingBuffer() {
        messageBuffers = new SwallowMessage[bufferSize];
    }

    @Override
    public void putMessage(SwallowMessage message) {
        messageBuffers[(int) (head.getAndIncrement()) & (bufferSize - 1)] = message;
    }

    @Override
    public void putMessages(List<SwallowMessage> messages) {
        for (SwallowMessage message : messages) {
            putMessage(message);
        }
    }

    @Override
    public SwallowMessage getMessage(String consumerId) {
        MessageReader reader = getOrCreateReader(consumerId);

        if (reader.isClosed()) {
            throw new IllegalArgumentException("reader not opened");
        }

        return reader.next();
    }

    @Override
    public boolean setMessageId(String consumerId, Long messageId) {
        MessageReader reader = getOrCreateReader(consumerId);
        return reader.open(messageId);
    }

    @Override
    public void close(String consumerId) {
        MessageReader reader = getOrCreateReader(consumerId);
        reader.close();
    }

    private MessageReader getOrCreateReader(String consumerId) {
        MessageReader reader = messageReaders.get(consumerId);

        if (reader == null) {
            messageReaders.putIfAbsent(consumerId, new MessageReader(head.get() - 1));
        }

        reader = messageReaders.get(consumerId);
        return reader;
    }

    public class MessageReader {

        private volatile AtomicBoolean isClosed;

        private volatile AtomicLong readIndex;

        public MessageReader() {
            this(0, false);
        }

        public MessageReader(long readIndex) {
            this(readIndex, true);
        }

        public MessageReader(long readIndex, boolean isClosed) {
            this.readIndex = new AtomicLong(readIndex);
            this.isClosed = new AtomicBoolean(isClosed);
        }

        public boolean open(Long messageId) {
            for (long k = head.get() - bufferSize; k > head.get(); k++) {

                SwallowMessage swallowMessage = messageBuffers[(int) k & (bufferSize - 1)];

                if (swallowMessage.getMessageId().longValue() == messageId.longValue()) {

                    readIndex.compareAndSet(readIndex.get(), k);
                    isClosed.compareAndSet(false, true);
                    return true;
                }
            }

            isClosed.compareAndSet(true, false);
            return false;
        }

        public SwallowMessage next() {

            if (readIndex.get() >= head.get() || readIndex.get() < head.get() - bufferSize) {
                throw new IndexOutOfBoundsException("invalid index");
            }

            SwallowMessage swallowMessage = messageBuffers[readIndex.intValue() & (bufferSize - 1)];

            if (readIndex.get() >= head.get() || readIndex.get() < head.get() - bufferSize) {
                throw new IndexOutOfBoundsException("invalid index");
            } else {
                readIndex.incrementAndGet();
                return swallowMessage;
            }
        }

        public void close() {
            isClosed.compareAndSet(true, false);
        }

        public boolean isClosed() {
            return isClosed.get();
        }

    }

}