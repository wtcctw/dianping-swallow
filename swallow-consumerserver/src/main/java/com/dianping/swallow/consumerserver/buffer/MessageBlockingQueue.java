package com.dianping.swallow.consumerserver.buffer;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.consumerserver.config.ConfigManager;
import com.dianping.swallow.consumerserver.worker.ConsumerInfo;

public final class MessageBlockingQueue extends LinkedBlockingQueue<SwallowMessage> implements
      CloseableBlockingQueue<SwallowMessage> {

   private static final long            serialVersionUID                  = -633276713494338593L;
   private static final Logger          LOG                               = LoggerFactory
                                                                                .getLogger(MessageBlockingQueue.class);

   private final ConsumerInfo           consumerInfo;

   protected transient MessageRetriever messageRetriever;

   private AtomicBoolean                isClosed                          = new AtomicBoolean(false);

   protected volatile Long              tailMessageId;
   protected MessageFilter              messageFilter;
   /** 最小剩余数量,当queue的消息数量小于threshold时，会触发从数据库加载数据的操作 */
   private final int                    threshold;

   //起一个线程定时从msg#<topic>#<cid>获取消息(该db存放backup消息)，放到queue里
   protected volatile Long              tailBackupMessageId;
   private ExecutorService 				retrieverThreadPool;
   
   private  Object  lock = new Object(), backupLock = new Object();
   private RetriveStrategy retriveStrategy, backupRetriveStrategy;
   
   public MessageBlockingQueue(ConsumerInfo consumerInfo, int threshold, int capacity, Long messageIdOfTailMessage,
                               Long tailBackupMessageId, MessageFilter messageFilter, ExecutorService retrieverThreadPool) {
      super(capacity);
      //能运行到这里，说明capacity>0
      this.consumerInfo = consumerInfo;
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
      this.retrieverThreadPool = retrieverThreadPool;
      
      this.retriveStrategy = new DefaultRetriveStrategy(ConfigManager.getInstance().getZeroRetrieveInterval());
      this.backupRetriveStrategy = new DefaultRetriveStrategy(ConfigManager.getInstance().getBackupZeroRetrieveInterval());
   }

   public void init() {
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
	   
	  ensureLeftMessage();
      return super.poll();
   }

   @Override
   public SwallowMessage poll(long timeout, TimeUnit unit) throws InterruptedException {
	   
	   ensureLeftMessage();
	   LOG.info("[poll]" + timeout);
	   return super.poll(timeout, unit);
   }

   /**
    * 唤醒“获取DB数据的后台线程”去DB获取数据，并添加到Queue的尾部
    */
   private void ensureLeftMessage() {
	   
	   if (super.size() < threshold) {
		   retrieverThreadPool.execute(new MessageRetrieverTask(retriveStrategy));
		   
		   if (consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
			   retrieverThreadPool.execute(new BackupMessageRetrieverTask(backupRetriveStrategy));
		   }
	   }
   }

   public void setMessageRetriever(MessageRetriever messageRetriever) {
      this.messageRetriever = messageRetriever;
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
      }
   }

   @Override
   public void isClosed() {
      if (isClosed.get()) {
         throw new RuntimeException("MessageBlockingQueue- already closed! ");
      }
   }

   @SuppressWarnings("rawtypes")
   private void putMessage(List messages) {
      for (int i = 1; i < messages.size(); i++) {
         SwallowMessage message = (SwallowMessage) messages.get(i);
         try {
            MessageBlockingQueue.this.put(message);
            if (LOG.isDebugEnabled()) {
               LOG.debug("Add message to (topic=" + consumerInfo.getDest().getName() + ",cid="
                     + consumerInfo.getConsumerId() + ") queue:" + message.toString());
            }
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
         }
      }
   }


   private abstract class AbstractRetrieveTask implements Runnable {

	   protected final Logger  LOG  = LoggerFactory.getLogger(getClass());
	   private RetriveStrategy retriveStrategy;
	   
	   public AbstractRetrieveTask(RetriveStrategy retriveStrategy){
		   this.retriveStrategy = retriveStrategy;
	   }
	   
		@Override
		public void run() {
			try{
				if(retriveStrategy.isRetrieve()){
					retrieveMessage();
				}
			}catch(Throwable th){
				LOG.error("[run]", th);
			}
		}
		protected abstract void retrieveMessage();
		
		@SuppressWarnings("rawtypes") 
		protected void updateRetrieveStrategy(List messages) {
			int messageSize = messages ==null ? 0 :messages.size();
			if(LOG.isInfoEnabled()){
				LOG.info("[updateRetrieveStrategy]" + messageSize);
			}
			retriveStrategy.retrieved(messageSize);
		}

   }

   private class MessageRetrieverTask extends AbstractRetrieveTask implements Runnable {

      public MessageRetrieverTask(RetriveStrategy retriveStrategy) {
    	  super(retriveStrategy);
      }

      @SuppressWarnings("rawtypes")
      @Override
      protected void retrieveMessage() {
    	 synchronized (lock) {
             LOG.info("[run]thread start(tailMessageId=" + tailMessageId + ")");
             List messages = messageRetriever.retriveMessage(consumerInfo.getDest().getName(), null, tailMessageId,
                     messageFilter);
             updateRetrieveStrategy(messages);
             if (messages != null && messages.size() > 0) {
            	 tailMessageId = (Long) messages.get(0);
            	 putMessage(messages);
             }
		}
         LOG.info("[run]thread done.(tailMessageId=" + tailMessageId + ")");
      }
   }

   private class BackupMessageRetrieverTask extends AbstractRetrieveTask implements  Runnable {

      public BackupMessageRetrieverTask(RetriveStrategy retriveStrategy) {
		super(retriveStrategy);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected void retrieveMessage() {
    	 synchronized (backupLock) {
             LOG.info("[run]thread start(tailBackupMessageId=" + tailBackupMessageId + ")");
             List messages = messageRetriever.retriveMessage(consumerInfo.getDest().getName(),
                     consumerInfo.getConsumerId(), tailBackupMessageId, messageFilter);
             updateRetrieveStrategy(messages);
             if (messages != null && messages.size() > 0) {
                  tailBackupMessageId = (Long) messages.get(0);
                  putMessage(messages);
             }
             LOG.info("[run]thread done(tailBackupMessageId=" + tailBackupMessageId + ")");
    	 }
      }
   }
}
