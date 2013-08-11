package com.dianping.swallow.consumerserver.buffer;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;

public final class MessageBlockingQueue extends LinkedBlockingQueue<SwallowMessage> implements
      CloseableBlockingQueue<SwallowMessage> {

   private static final long            serialVersionUID                  = -633276713494338593L;
   private static final Logger          LOG                               = LoggerFactory
                                                                                .getLogger(MessageBlockingQueue.class);

   private final String                 cid;
   private final String                 topicName;

   protected transient MessageRetriever messageRetriever;

   private AtomicBoolean                isClosed                          = new AtomicBoolean(false);

   private transient Thread             messageRetrieverThread;
   protected volatile Long              tailMessageId;
   protected MessageFilter              messageFilter;
   private int                          delayBase                         = 10;
   private int                          delayUpperbound                   = 50;
   /** 最小剩余数量,当queue的消息数量小于threshold时，会触发从数据库加载数据的操作 */
   private final int                    threshold;
   private ReentrantLock                reentrantLock                     = new ReentrantLock(true);
   private transient Condition          condition                         = reentrantLock.newCondition();

   //起一个线程定时从msg#<topic>#<cid>获取消息(该db存放backup消息)，放到queue里
   private transient Thread             backupMessageRetrieverThread;
   protected volatile Long              tailBackupMessageId;
   private int                          retrieverFromBackupIntervalSecond = 10;

   public MessageBlockingQueue(String cid, String topicName, int threshold, int capacity, Long messageIdOfTailMessage,
                               Long tailBackupMessageId) {
      super(capacity);
      //能运行到这里，说明capacity>0
      this.cid = cid;
      this.topicName = topicName;
      if (threshold < 0) {
         throw new IllegalArgumentException("threshold: " + threshold);
      }
      this.threshold = threshold;
      if (messageIdOfTailMessage == null) {
         throw new IllegalArgumentException("messageIdOfTailMessage is null.");
      }
      this.tailMessageId = messageIdOfTailMessage;
      this.tailBackupMessageId = tailBackupMessageId;
   }

   public MessageBlockingQueue(String cid, String topicName, int threshold, int capacity, Long messageIdOfTailMessage,
                               Long tailBackupMessageId, MessageFilter messageFilter) {
      super(capacity);
      //能运行到这里，说明capacity>0
      this.cid = cid;
      this.topicName = topicName;
      if (threshold < 0) {
         throw new IllegalArgumentException("threshold: " + threshold);
      }
      this.threshold = threshold;
      if (messageIdOfTailMessage == null) {
         throw new IllegalArgumentException("messageIdOfTailMessage is null.");
      }
      this.tailMessageId = messageIdOfTailMessage;
      this.tailBackupMessageId = tailBackupMessageId;
      this.messageFilter = messageFilter;
   }

   public void init() {
      messageRetrieverThread = new MessageRetrieverThread();
      messageRetrieverThread.start();

      backupMessageRetrieverThread = new BackupMessageRetrieverThread();
      backupMessageRetrieverThread.start();

   }

   @Override
   public SwallowMessage take() throws InterruptedException {
      //阻塞take()方法不让使用的原因是：防止当ensureLeftMessage()失败时，没法获取消息，而调用take()的代码将一直阻塞。
      //不过，可以在后台线程中获取消息失败时不断重试，直到保证ensureLeftMessage。
      //但是极端情况下，可能出现：
      //  后台线程某时刻判断size()是足够的，所以wait；但在wait前，queue的元素又被取光了，外部调用者继续在take()上阻塞；而此时后台线程也wait，就‘死锁’了。
      //  即，size()的判断和fetch消息的操作，比较作为同步块原子化。才能从根本上保证线程安全。
      throw new UnsupportedOperationException(
            "Don't call this operation, call 'poll(long timeout, TimeUnit unit)' instead.");
   }

   @Override
   public SwallowMessage poll() {
      //如果剩余元素数量小于最低限制值threshold，就启动一个“获取DB数据的后台线程”去DB获取数据，并添加到Queue的尾部
      if (super.size() < threshold) {
         ensureLeftMessage();
      }
      return super.poll();
   }

   @Override
   public SwallowMessage poll(long timeout, TimeUnit unit) throws InterruptedException {
      //如果剩余元素数量小于最低限制值threshold，就启动一个“获取DB数据的后台线程”去DB获取数据，并添加到Queue的尾部
      if (super.size() < threshold) {
         ensureLeftMessage();
      }
      return super.poll(timeout, unit);
   }

   /**
    * 唤醒“获取DB数据的后台线程”去DB获取数据，并添加到Queue的尾部
    */
   private void ensureLeftMessage() {
      if (messageRetriever == null) {
         return;
      }
      //只有一个线程能唤醒“获取DB数据的后台线程”
      if (reentrantLock.tryLock()) {
         condition.signal();
         reentrantLock.unlock();
      }
   }

   public void setMessageRetriever(MessageRetriever messageRetriever) {
      this.messageRetriever = messageRetriever;
   }

   private class MessageRetrieverThread extends Thread {

      private DefaultPullStrategy pullStrategy = new DefaultPullStrategy(delayBase, delayUpperbound);

      public MessageRetrieverThread() {
         this.setName("swallow-MessageRetriever-(topic=" + topicName + ",cid=" + cid + ")");
         this.setDaemon(true);
      }

      @Override
      public void run() {
         LOG.info("thread start:" + this.getName());
         while (!this.isInterrupted()) {
            reentrantLock.lock();
            try {
               condition.await();
               retrieveMessage();
            } catch (InterruptedException e) {
               this.interrupt();
            } finally {
               reentrantLock.unlock();
            }
         }
         LOG.info("thread done:" + this.getName());
      }

      @SuppressWarnings("rawtypes")
      private void retrieveMessage() {
         if (LOG.isDebugEnabled()) {
            LOG.debug("retriveMessage() start:" + this.getName());
         }
         try {
            List messages = messageRetriever.retriveMessage(topicName, null, tailMessageId, messageFilter);
            if (messages != null && messages.size() > 0) {
               tailMessageId = (Long) messages.get(0);
               putMessage(messages);
               //如果本次获取完，queue的消息条数仍然比最低阀值小，那么消费者与生产者很有可能速度差不多，此时为了
               //避免retrieve线程不断被唤醒，适当地睡眠一段时间
               if (MessageBlockingQueue.this.size() < MessageBlockingQueue.this.threshold) {
                  pullStrategy.fail(true);
               } else {
                  pullStrategy.succeess();
               }
            }
         } catch (RuntimeException e1) {
            LOG.error(e1.getMessage(), e1);
         }
         if (LOG.isDebugEnabled()) {
            LOG.debug("retriveMessage() done:" + this.getName());
         }
      }
   }

   private class BackupMessageRetrieverThread extends Thread {

      public BackupMessageRetrieverThread() {
         this.setName("swallow-BackupMessageRetriever-(topic=" + topicName + ",cid=" + cid + ")");
         this.setDaemon(true);
      }

      @Override
      public void run() {
         LOG.info("thread start:" + this.getName());
         while (!this.isInterrupted()) {
            try {
               retrieveMessage();
               TimeUnit.SECONDS.sleep(retrieverFromBackupIntervalSecond);
            } catch (RuntimeException e) {
               this.interrupt();
            } catch (InterruptedException e) {
               this.interrupt();
            }
         }
         LOG.info("thread done:" + this.getName());
      }

      @SuppressWarnings("rawtypes")
      private void retrieveMessage() {
         if (LOG.isDebugEnabled()) {
            LOG.debug("retriveMessage() start:" + this.getName());
         }
         try {
            List messages = messageRetriever.retriveMessage(topicName, cid, tailBackupMessageId, messageFilter);
            if (messages != null && messages.size() > 0) {
               tailBackupMessageId = (Long) messages.get(0);
               putMessage(messages);
            }
         } catch (RuntimeException e1) {
            LOG.error(e1.getMessage(), e1);
         }
         if (LOG.isDebugEnabled()) {
            LOG.debug("retriveMessage() done:" + this.getName());
         }
      }
   }

   /**
    * 关闭BlockingQueue占用的资源。<br>
    * <note> 注意:close()只是中断了内部的后台线程，并不会导致poll()和poll(long timeout, TimeUnit
    * unit)方法不可用,您依然可以使用poll()和poll(long timeout, TimeUnit
    * unit)方法获取MessageBlockingQueue中剩余的消息。不过，不会再有后台线程去从DB获取更多的消息。 </note>
    */
   @Override
   public void close() {
      if (isClosed.compareAndSet(false, true)) {
         this.messageRetrieverThread.interrupt();
      }
   }

   @Override
   public void isClosed() {
      if (isClosed.get()) {
         throw new RuntimeException("MessageBlockingQueue- already closed! ");
      }
   }

   public void setDelayBase(int delayBase) {
      this.delayBase = delayBase;
   }

   public void setDelayUpperbound(int delayUpperbound) {
      this.delayUpperbound = delayUpperbound;
   }

   public void setRetrieverFromBackupIntervalSecond(int retrieverFromBackupIntervalSecond) {
      this.retrieverFromBackupIntervalSecond = retrieverFromBackupIntervalSecond;
   }

   @SuppressWarnings("rawtypes")
   private void putMessage(List messages) {
      for (int i = 1; i < messages.size(); i++) {
         SwallowMessage message = (SwallowMessage) messages.get(i);
         try {
            MessageBlockingQueue.this.put(message);
            if (LOG.isDebugEnabled()) {
               LOG.debug("add message to (topic=" + topicName + ",cid=" + cid + ") queue:" + message.toString());
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
         }
      }
   }

}
