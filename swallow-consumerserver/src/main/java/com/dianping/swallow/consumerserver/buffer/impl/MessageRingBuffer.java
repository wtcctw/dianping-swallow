package com.dianping.swallow.consumerserver.buffer.impl;

import com.dianping.swallow.common.internal.exception.SwallowIOException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumerserver.buffer.*;
import com.dianping.swallow.consumerserver.config.ConfigManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author qi.yin
 *         2016/03/02  上午11:24.
 */
public class MessageRingBuffer implements CloseableRingBuffer<SwallowMessage> {

    //大小必须是2的n次方。（优化了%操作为&）
    private int bufferSize = 1024;

    private final int indexMask;

    private SwallowMessage[] messageBuffers;

    private Destination dest;

    protected volatile Long tailMessageId;

    private MessageRetriever messageRetriever;

    private ExecutorService retrieverThreadPool;

    private int minThreshold;
    private int maxThreshold;

    private RetrieveStrategy retrieveStrategy;

    private AtomicLong head = new AtomicLong(0);

    private ConcurrentHashMap<String, BufferReader> bufferReaders = new ConcurrentHashMap<String, BufferReader>();

    public MessageRingBuffer(Destination dest, int minThreshold, int maxThreshold, int capacity,
                             ExecutorService retrieverThreadPool) {

        this.dest = dest;
        bufferSize = capacity;
        if (Integer.bitCount(bufferSize) != 1) {
            throw new IllegalArgumentException("bufferSize must be a power of 2");
        }

        if (minThreshold < 0 || maxThreshold < 0 || minThreshold > maxThreshold) {
            throw new IllegalArgumentException("wrong threshold: "
                    + minThreshold + "," + maxThreshold);
        }
        this.minThreshold = minThreshold;
        this.maxThreshold = maxThreshold;

        this.retrieverThreadPool = retrieverThreadPool;

        indexMask = bufferSize - 1;

        messageBuffers = new SwallowMessage[bufferSize];

        this.retrieveStrategy = new RingBufferRetrieveStrategy(dest, ConfigManager.getInstance().getMinRetrieveInterval(), this.maxThreshold,
                ConfigManager.getInstance().getMaxRetriverTaskCountPerDest());
    }

    @Override
    public void putMessage(SwallowMessage message) {
        if (message != null) {
            messageBuffers[(int) (head.get()) & (indexMask)] = message;
            head.incrementAndGet();
        }
    }

    @Override
    public void putMessages(List<SwallowMessage> messages) {
        for (SwallowMessage message : messages) {
            putMessage(message);
            if (EnvUtil.isQa()) {
                putMessage(message);
            }
        }
    }

    private SwallowMessage getMessage(long position) {
        return messageBuffers[(int) position & (indexMask)];
    }


    public Long getTailMessageId() {
        return tailMessageId;
    }

    @Override
    public void setMessageRetriever(MessageRetriever messageRetriever) {
        this.messageRetriever = messageRetriever;
    }

    public void setTailMessageId(Long tailMessageId) {
        this.tailMessageId = tailMessageId;
    }

    @Override
    public BufferReader getOrCreateReader(String consumerId) {
        BufferReader reader = bufferReaders.get(consumerId);

        if (reader == null) {
            bufferReaders.putIfAbsent(consumerId, new BufferReader());
        }

        reader = bufferReaders.get(consumerId);
        return reader;
    }

    @Override
    public void fetchMessage(BufferReader bufferReader, long lastMessageId) {
        boolean isRetrieve = false;

        if (!bufferReader.isClosed()) {
            if ((head.get() - bufferReader.getReadIndex()) < minThreshold) {
                isRetrieve = true;
            }
        } else {
            if (isEmpty()) {
                isRetrieve = true;
            } else {
                SwallowMessage message = getMessage(head.get() - 1);
                if (message != null && lastMessageId >= message.getMessageId()) {
                    isRetrieve = true;
                }
            }
        }

        if (isRetrieve && retrieveStrategy.canPutNewTask()) {
            retrieverThreadPool.execute(new RingBufferRetrieveTask(retrieveStrategy, this.dest, messageRetriever, this));
            retrieveStrategy.offerNewTask();
        }
    }

    public boolean isEmpty() {
        return !(head.get() > 0L);
    }

    @Override
    public String toString() {
        return "MessageRingBuffer[head=" + head + ", bufferSize=" + bufferSize + ", tailMessageId=" + getTailMessageId() + ']';
    }

    public enum ReaderStatus {

        OPEN,
        CLOSED_OVER,
        CLOSED_BACK;

        public boolean isOpen() {
            return this == OPEN;
        }

        public boolean isClosedOver() {
            return this == CLOSED_OVER;
        }

        public boolean isClosedBack() {
            return this == CLOSED_BACK;
        }
    }

    public class BufferReader {

        private final AtomicLong readIndex = new AtomicLong(-1);

        private final AtomicBoolean closed = new AtomicBoolean(false);

        public BufferReader() {
        }

        public ReaderStatus tryOpen(Long messageId) {
            if (!isEmpty()) {
                long firstPosition = head.get() - bufferSize;
                long position = firstPosition < 0L ? 0L : firstPosition;

                if (messageId.longValue() < getMessage(position).getMessageId().longValue()) {
                    closed.compareAndSet(false, true);
                    return ReaderStatus.CLOSED_BACK;
                } else if (messageId.longValue() > getMessage((head.get() - 1)).getMessageId().longValue()) {
                    closed.compareAndSet(false, true);
                    return ReaderStatus.CLOSED_OVER;
                } else {

                    for (; position < head.get(); position++) {

                        SwallowMessage swallowMessage = getMessage(position);

                        if (messageId.longValue() == swallowMessage.getMessageId().longValue()) {
                            readIndex.set(position + 1);
                            closed.compareAndSet(true, false);
                            return ReaderStatus.OPEN;
                        } else if (messageId.longValue() < swallowMessage.getMessageId().longValue()) {
                            readIndex.set(position);
                            closed.compareAndSet(true, false);
                            return ReaderStatus.OPEN;
                        }

                    }
                }
            }

            closed.compareAndSet(false, true);
            return ReaderStatus.CLOSED_BACK;
        }

        public SwallowMessage next() throws SwallowIOException {
            if (readIndex.get() >= head.get()) {
                return null;
            }
            if (readIndex.get() <= head.get() - bufferSize) {
                closed.compareAndSet(false, true);
                throw new SwallowIOException("reader out of buffer, readIndex " + readIndex.get() + ", head " + head.get() + ".");
            }

            SwallowMessage swallowMessage = getMessage(readIndex.get());

            if (readIndex.get() <= head.get() - bufferSize) {
                closed.compareAndSet(false, true);
                throw new SwallowIOException("reader out of buffer, readIndex " + readIndex.get() + ", head " + head.get() + ".");
            } else {
                readIndex.incrementAndGet();
                return swallowMessage;
            }
        }

        public Long getCurrentMessageId() {
            if (!isClosed()) {
                SwallowMessage swallowMessage = null;
                if (readIndex.get() <= head.get() && readIndex.get() > 0) {
                    swallowMessage = getMessage(readIndex.get() - 1);
                }
                if (swallowMessage != null && readIndex.get() > head.get() - bufferSize) {
                    return swallowMessage.getMessageId();
                }
            }
            return null;
        }

        public long getReadIndex() {
            return readIndex.longValue();
        }

        public boolean isClosed() {
            return closed.get();
        }

        @Override
        public String toString() {
            return "BufferReader[readIndex=" + readIndex + ']';
        }

    }

}