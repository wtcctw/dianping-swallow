package com.dianping.swallow.consumerserver.buffer;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.consumerserver.buffer.impl.BackupMessageBlockingQueue;
import com.dianping.swallow.consumerserver.buffer.impl.MessageBlockingQueue;
import com.dianping.swallow.consumerserver.buffer.impl.MessageRingBuffer;
import com.dianping.swallow.consumerserver.pool.ConsumerThreadPoolManager;

public class SwallowBuffer {
    /**
     * 锁是为了防止相同topicName的TopicBuffer被并发创建，所以相同的topicName对应相同的锁。
     * 锁的个数也决定了最多能有多少创建TopicBuffer的并发操作
     */
    private final int LOCK_NUM_FOR_CREATE_TOPIC_BUFFER = 10;
    private ReentrantLock[] locksForCreateTopicBuffer = new ReentrantLock[LOCK_NUM_FOR_CREATE_TOPIC_BUFFER];

    {
        for (int i = 0; i < locksForCreateTopicBuffer.length; i++) {
            locksForCreateTopicBuffer[i] = new ReentrantLock();
        }
    }

    private final ConcurrentMap<String, TopicBuffer> topicBuffers = new ConcurrentHashMap<String, TopicBuffer>();
    private MessageRetriever messageRetriever;
    private int capacityOfQueue = Integer.MAX_VALUE;
    private int minThresholdOfQueue = 60;
    private int maxThresholdOfQueue = 10000;
    private int capacityOfBuffer = 1024;
    private int minThresholdOfBuffer = 256;
    private int maxThresholdOfBuffer = 1024;

    private ConsumerThreadPoolManager consumerThreadPoolManager;

    /**
     * 根据topicName，获取topicName对应的TopicBuffer。<br>
     * 如果topicBuffer已经存在，则返回。否则创建一个新的TopicBuffer，该方法保证，
     * 一个topicName只有一个TopicBuffer。<br>
     * 该方法是线程安全的。
     */
    protected TopicBuffer getTopicBuffer(String topicName) {
        TopicBuffer topicBuffer = topicBuffers.get(topicName);
        if (topicBuffer != null) {
            return topicBuffer;
        }
        // topicBuffer不存在，须创建
        ReentrantLock reentrantLock = locksForCreateTopicBuffer[index(topicName)];// 对于String对象的加锁，也可以使用synchronized(topicName.inner())
        try {
            reentrantLock.lock();// 加锁，防止同时创建相同topicName的TopicCache
            topicBuffer = topicBuffers.get(topicName);// double check
            if (topicBuffer == null) {
                topicBuffer = new TopicBuffer(topicName);
                topicBuffers.put(topicName, topicBuffer);
            }
        } finally {// 释放锁
            reentrantLock.unlock();
        }
        return topicBuffer;
    }

    public CloseableBlockingQueue<SwallowMessage> createMessageQueue(ConsumerInfo consumerInfo, long tailMessageId) {

        return createMessageQueue(consumerInfo, tailMessageId, null);
    }


    public CloseableBlockingQueue<SwallowMessage> createMessageQueue(ConsumerInfo consumerInfo, long tailMessageId, MessageFilter messageFilter) {

        return getTopicBuffer(consumerInfo.getDest().getName()).createMessageQueue(consumerInfo, tailMessageId, messageFilter);
    }

    public CloseableBlockingQueue<SwallowMessage> createBackupMessageQueue(ConsumerInfo consumerInfo, long tailMessageId) {

        return createBackupMessageQueue(consumerInfo, tailMessageId, null);
    }

    public CloseableBlockingQueue<SwallowMessage> createBackupMessageQueue(ConsumerInfo consumerInfo, long tailMessageId, MessageFilter messageFilter) {

        return getTopicBuffer(consumerInfo.getDest().getName()).createBackupMessageQueue(consumerInfo, tailMessageId, messageFilter);
    }

    public CloseableRingBuffer<SwallowMessage> getOrCreateMessageBuffer(ConsumerInfo consumerInfo) {
        return getTopicBuffer(consumerInfo.getDest().getName()).getOrCreateMessageBuffer();
    }


    private int index(String topicName) {
        int hashcode = topicName.hashCode();
        hashcode = hashcode == Integer.MIN_VALUE ? 0 : Math.abs(hashcode);// 保证非负
        return hashcode % LOCK_NUM_FOR_CREATE_TOPIC_BUFFER;
    }

    public void setCapacity(int capacity) {
        this.capacityOfQueue = capacity;
    }

    public void setMessageRetriever(MessageRetriever messageRetriever) {
        this.messageRetriever = messageRetriever;
    }

    public ConsumerThreadPoolManager getConsumerThreadPoolManager() {
        return consumerThreadPoolManager;
    }

    public void setConsumerThreadPoolManager(ConsumerThreadPoolManager consumerThreadPoolManager) {
        this.consumerThreadPoolManager = consumerThreadPoolManager;
    }

    public int getMinThresholdOfQueue() {
        return minThresholdOfQueue;
    }

    public void setMinThresholdOfQueue(int minThresholdOfQueue) {
        this.minThresholdOfQueue = minThresholdOfQueue;
    }

    public int getMaxThresholdOfQueue() {
        return maxThresholdOfQueue;
    }

    public void setMaxThresholdOfQueue(int maxThresholdOfQueue) {
        this.maxThresholdOfQueue = maxThresholdOfQueue;
    }

    public int getCapacityOfBuffer() {
        return capacityOfBuffer;
    }

    public void setCapacityOfBuffer(int capacityOfBuffer) {
        this.capacityOfBuffer = capacityOfBuffer;
    }

    public int getMinThresholdOfBuffer() {
        return minThresholdOfBuffer;
    }

    public void setMinThresholdOfBuffer(int minThresholdOfBuffer) {
        this.minThresholdOfBuffer = minThresholdOfBuffer;
    }

    public int getMaxThresholdOfBuffer() {
        return maxThresholdOfBuffer;
    }

    public void setMaxThresholdOfBuffer(int maxThresholdOfBuffer) {
        this.maxThresholdOfBuffer = maxThresholdOfBuffer;
    }

    private class TopicBuffer {

        private String topicName;

        public TopicBuffer(String topicName) {
            this.topicName = topicName;
        }

        private volatile CloseableRingBuffer<SwallowMessage> messageBuffer;

        private ConcurrentHashMap<String, MessageQueuePair> messageQueues = new ConcurrentHashMap<String, MessageQueuePair>();

        public CloseableBlockingQueue<SwallowMessage> createBackupMessageQueue(ConsumerInfo consumerInfo,
                                                                               long tailMessageId, MessageFilter messageFilter) {

            CloseableBlockingQueue<SwallowMessage> messageBlockingQueue = new BackupMessageBlockingQueue(consumerInfo,
                    minThresholdOfQueue, maxThresholdOfQueue, capacityOfQueue, tailMessageId,
                    consumerThreadPoolManager.getRetrieverThreadPool());
            messageBlockingQueue.setMessageRetriever(messageRetriever);

            MessageQueuePair messageQueuePair = getMessageQueuePair(consumerInfo);
            messageQueuePair.setBackupMessageQueue(messageBlockingQueue);

            return messageBlockingQueue;
        }

        private MessageQueuePair getMessageQueuePair(ConsumerInfo consumerInfo) {

            return MapUtil.getOrCreate(messageQueues, consumerInfo.getConsumerId(), MessageQueuePair.class);
        }

        public CloseableBlockingQueue<SwallowMessage> createMessageQueue(ConsumerInfo consumerInfo, long tailMessageId, MessageFilter messageFilter) {

            if (consumerInfo == null) {
                throw new IllegalArgumentException("consumerInfo is null.");
            }

            CloseableBlockingQueue<SwallowMessage> messageBlockingQueue = new MessageBlockingQueue(consumerInfo,
                    minThresholdOfQueue, maxThresholdOfQueue, capacityOfQueue, tailMessageId,
                    consumerThreadPoolManager.getRetrieverThreadPool());
            messageBlockingQueue.setMessageRetriever(messageRetriever);

            MessageQueuePair messageQueuePair = getMessageQueuePair(consumerInfo);

            messageQueuePair.setMessageQueue(messageBlockingQueue);

            return messageBlockingQueue;
        }

        public CloseableRingBuffer<SwallowMessage> getOrCreateMessageBuffer() {
            if (messageBuffer == null) {
                synchronized (this) {
                    if (messageBuffer == null) {
                        messageBuffer = new MessageRingBuffer(Destination.topic(topicName),
                                minThresholdOfBuffer, maxThresholdOfBuffer, capacityOfBuffer, consumerThreadPoolManager.getRetrieverThreadPool());
                        messageBuffer.setMessageRetriever(messageRetriever);
                        messageBuffer.setTailMessageId(messageRetriever.getBeginFetchIdOfBuffer(topicName));
                    }
                }
            }
            return messageBuffer;
        }
    }

    public static class MessageQueuePair {

        private WeakReference<CloseableBlockingQueue<SwallowMessage>> messageQueue, backupMessageQueue;


        public CloseableBlockingQueue<SwallowMessage> getMessageQueue() {
            return messageQueue.get();
        }

        public CloseableBlockingQueue<SwallowMessage> getBackupMessageQueue() {
            return backupMessageQueue.get();
        }

        public void setMessageQueue(CloseableBlockingQueue<SwallowMessage> messageQueue) {
            this.messageQueue = new WeakReference<CloseableBlockingQueue<SwallowMessage>>(messageQueue);
        }

        public void setBackupMessageQueue(CloseableBlockingQueue<SwallowMessage> backupMessageQueue) {
            this.backupMessageQueue = new WeakReference<CloseableBlockingQueue<SwallowMessage>>(backupMessageQueue);
        }

    }

}
