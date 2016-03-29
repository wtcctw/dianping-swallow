package com.dianping.swallow.consumerserver.buffer.impl;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.exception.SwallowIOException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.observer.Observable;
import com.dianping.swallow.common.internal.observer.Observer;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumerserver.buffer.*;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.worker.impl.ConsumerConfigChanged;
import com.dianping.swallow.consumerserver.worker.impl.ConsumerWorkerImpl;

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

    protected transient MessageRetriever messageRetriever;

    private SwallowMessage[] messageBuffers;

    private Destination dest;

    protected volatile Long tailMessageId;

    private ExecutorService retrieverThreadPool;

    private int minThreshold;
    private int maxThreshold;

    private RetrieveStrategy retrieveStrategy;

    private AtomicLong head = new AtomicLong(0);

    private Object getTailMessageIdLock = new Object();

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
        messageBuffers[(int) (head.getAndIncrement()) & (indexMask)] = message;
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


    public Long getTailMessageId() {
        return tailMessageId;
    }

    @Override
    public void setMessageRetriever(MessageRetriever messageRetriever) {
        this.messageRetriever = messageRetriever;
    }

    public void setTailMessageId(Long tailMessageId) {

        synchronized (getTailMessageIdLock) {

            this.tailMessageId = tailMessageId;
        }
    }

    @Override
    public BufferReader getOrCreateReader(String consumerId) {
        BufferReader reader = bufferReaders.get(consumerId);

        if (reader == null) {
            bufferReaders.putIfAbsent(consumerId, new BufferReader(head.get() - 1));
        }

        reader = bufferReaders.get(consumerId);
        return reader;
    }

    @Override
    public void fetchMessage(BufferReader bufferReader) {
        boolean isRetrieve = false;

        if (!bufferReader.isClosed()) {
            if ((head.get() - bufferReader.getReadIndex()) < minThreshold) {
                isRetrieve = true;
            }
        } else {
            isRetrieve = true;
        }

        if (isRetrieve && retrieveStrategy.canPutNewTask()) {
            retrieverThreadPool.execute(new RingBufferRetrieveTask(retrieveStrategy, this.dest, messageRetriever, this));
            retrieveStrategy.offerNewTask();
        }
    }
//
//    private long currentMinThreshold() {
//        long minThreshold = Long.MAX_VALUE;
//        long currentPosition = head.get();
//        for (ConcurrentHashMap.Entry<String, BufferReader> entry : bufferReaders.entrySet()) {
//
//            BufferReader bufferReader = entry.getValue();
//            if (!bufferReader.isClosed()) {
//
//                long temp = currentPosition - bufferReader.getReadIndex();
//                if (temp < minThreshold) {
//                    minThreshold = temp;
//                }
//            }
//        }
//        if (minThreshold == Long.MAX_VALUE) {
//            return 0L;
//        }
//        return minThreshold;
//    }

    public boolean isEmpty() {
        return head.get() == 0L ? true : false;
    }

    @Override
    public String toString() {
        return "MessageRingBuffer[head=" + head + ", bufferSize=" + bufferSize + ", tailMessageId=" + getTailMessageId() + ']';
    }

    public class BufferReader implements Observer {

        private final AtomicLong readIndex = new AtomicLong(-1);

        private final AtomicBoolean closed = new AtomicBoolean(true);

        protected volatile MessageFilter messageFilter;

        public BufferReader(long readIndex) {
            this.readIndex.set(readIndex);
        }

        public int tryOpen(Long messageId) {
            if (!isEmpty()) {
                long firstPosition = head.get() - bufferSize;
                long position = firstPosition < 0L ? 0L : firstPosition;

                if (messageId.longValue() < getMessage(position).getMessageId().longValue()) {
                    closed.compareAndSet(false, true);
                    return -1;
                } else if (messageId.longValue() > getMessage((head.get() - 1)).getMessageId().longValue()) {
                    closed.compareAndSet(false, true);
                    return 1;
                } else {

                    for (; position < head.get(); position++) {

                        SwallowMessage swallowMessage = getMessage(position);

                        if (messageId.longValue() == swallowMessage.getMessageId().longValue()) {
                            readIndex.set(position + 1);
                            closed.compareAndSet(true, false);
                            return 0;
                        } else if (messageId.longValue() < swallowMessage.getMessageId().longValue()) {
                            readIndex.set(position);
                            closed.compareAndSet(true, false);
                            return 0;
                        }
                    }
                }
            }

            closed.compareAndSet(false, true);
            return -1;
        }

        public SwallowMessage next() throws SwallowIOException {
            if (readIndex.get() >= head.get()) {
                return null;
            }

            if (readIndex.get() <= head.get() - bufferSize) {
                closed.compareAndSet(false, true);
                throw new SwallowIOException("reader out of buffer.");
            }

            SwallowMessage swallowMessage = getMessage(readIndex.intValue());

            if (readIndex.get() <= head.get() - bufferSize) {
                closed.compareAndSet(false, true);
                throw new SwallowIOException("reader out of buffer.");
            } else {
                readIndex.incrementAndGet();
                if (!MessageFilter.isFilted(messageFilter, swallowMessage.getType())) {
                    return swallowMessage;
                } else {
                    return next();
                }
            }
        }

        private SwallowMessage getMessage(long position) {
            return messageBuffers[(int) position & (indexMask)];
        }

        @Override
        public void update(Observable observable, Object args) {

            if (!(observable instanceof ConsumerWorkerImpl)) {
                throw new IllegalArgumentException("observable not supported!" + observable.getClass());
            }

            ConsumerConfigChanged changed = (ConsumerConfigChanged) args;

            switch (changed.getConsumerConfigChangeType()) {

                case MESSAGE_FILTER:
                    this.messageFilter = changed.getNewMessageFilter();
                    break;
                default:
                    throw new IllegalArgumentException("type not supported!" + changed.getConsumerConfigChangeType());
            }

        }

        public Long getEmptyTailMessageId() {

            synchronized (getTailMessageIdLock) {

                if (head.get() == readIndex.get()) {

                    return getTailMessageId();
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
            return "BufferReader[readIndex=" + readIndex + ", messageFilter=" + messageFilter + ']';
        }
    }

}