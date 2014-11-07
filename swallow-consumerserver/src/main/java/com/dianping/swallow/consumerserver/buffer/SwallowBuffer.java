package com.dianping.swallow.consumerserver.buffer;

import java.lang.ref.WeakReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReentrantLock;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.consumerserver.pool.ConsumerThreadPoolManager;
import com.dianping.swallow.consumerserver.worker.ConsumerInfo;

public class SwallowBuffer {
   /**
    * 锁是为了防止相同topicName的TopicBuffer被并发创建，所以相同的topicName对应相同的锁。
    * 锁的个数也决定了最多能有多少创建TopicBuffer的并发操作
    */
   private final int                                LOCK_NUM_FOR_CREATE_TOPIC_BUFFER  = 10;
   private ReentrantLock[]                          locksForCreateTopicBuffer         = new ReentrantLock[LOCK_NUM_FOR_CREATE_TOPIC_BUFFER];
   {
      for (int i = 0; i < locksForCreateTopicBuffer.length; i++) {
         locksForCreateTopicBuffer[i] = new ReentrantLock();
      }
   }

   private final ConcurrentMap<String, TopicBuffer> topicBuffers                      = new ConcurrentHashMap<String, TopicBuffer>();
   private MessageRetriever                         messageRetriever;
   private int                                      capacityOfQueue                   = Integer.MAX_VALUE;
   private int                                      thresholdOfQueue                  = 60;
   private ConsumerThreadPoolManager  		 		consumerThreadPoolManager; 

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
      ReentrantLock reentrantLock = locksForCreateTopicBuffer[index(topicName)];//对于String对象的加锁，也可以使用synchronized(topicName.inner())
      try {
         reentrantLock.lock();// 加锁，防止同时创建相同topicName的TopicCache
         topicBuffer = topicBuffers.get(topicName);// double check
         if (topicBuffer == null) {
            topicBuffer = new TopicBuffer();
            topicBuffers.put(topicName, topicBuffer);
         }
      } finally {// 释放锁
         reentrantLock.unlock();
      }
      return topicBuffer;
   }

   /**
    * 创建一个BlockingQueue
    * 
    * @param tailMessageId 从messageId大于messageIdOfTailMessage的消息开始消费
    * @return
    */
   public CloseableBlockingQueue<SwallowMessage> createMessageQueue(ConsumerInfo consumerInfo, long tailMessageId,
                                                                    long tailBackupMessageId,
                                                                    MessageFilter messageFilter) {
      return this.getTopicBuffer(consumerInfo.getDest().getName()).createMessageQueue(consumerInfo, tailMessageId,
            tailBackupMessageId, messageFilter);
   }

   private int index(String topicName) {
      int hashcode = topicName.hashCode();
      hashcode = hashcode == Integer.MIN_VALUE ? 0 : Math.abs(hashcode);// 保证非负
      return hashcode % LOCK_NUM_FOR_CREATE_TOPIC_BUFFER;
   }

   public void setCapacity(int capacity) {
      this.capacityOfQueue = capacity;
   }

   public void setThreshold(int threshold) {
      this.thresholdOfQueue = threshold;
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

private class TopicBuffer {

      private ConcurrentHashMap<String, WeakReference<MessageBlockingQueue>> messageQueues = new ConcurrentHashMap<String, WeakReference<MessageBlockingQueue>>();

      /**
       * 创建一个BlockingQueue
       * 
       * @param tailMessageId 从messageId大于messageIdOfTailMessage的消息开始消费
       * @return
       */
      public CloseableBlockingQueue<SwallowMessage> createMessageQueue(ConsumerInfo consumerInfo, long tailMessageId,
                                                                       long tailBackupMessageId,
                                                                       MessageFilter messageFilter) {
         if (consumerInfo == null) {
            throw new IllegalArgumentException("consumerInfo is null.");
         }
         MessageBlockingQueue messageBlockingQueue;
         messageBlockingQueue = new MessageBlockingQueue(consumerInfo, thresholdOfQueue, capacityOfQueue,
                  tailMessageId, tailBackupMessageId, messageFilter, consumerThreadPoolManager.getRetrieverThreadPool());
         messageBlockingQueue.setMessageRetriever(messageRetriever);
         messageBlockingQueue.init();

         messageQueues.put(consumerInfo.getConsumerId(), new WeakReference<MessageBlockingQueue>(messageBlockingQueue));
         return messageBlockingQueue;
      }

   }

}
