package com.dianping.swallow.consumerserver.worker;

import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.jboss.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;
import com.dianping.cat.CatConstants;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.dao.AckDAO;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.threadfactory.PullStrategy;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.internal.util.MongoUtils;
import com.dianping.swallow.consumerserver.buffer.CloseableBlockingQueue;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;

/***
 * 一个ConsumerWorkerImpl负责处理一个(topic,consumerId)的消费者集群，使用单线程获取消息，并顺序推送给Consumer
 * 
 * @author kezhu.wu
 */
public final class ConsumerWorkerImpl implements ConsumerWorker {
   private static final Logger                          LOG                     = LoggerFactory
                                                                                      .getLogger(ConsumerWorkerImpl.class);

   private static final long                            SECOND                  = 1000;

   private final AtomicLong                             SEQ                     = new AtomicLong(1);
   private final AtomicLong                             BACKUP_SEQ              = new AtomicLong(1);

   private ConsumerInfo                                 consumerInfo;
   private MQThreadFactory                              threadFactory;
   private MessageFilter                                messageFilter;

   private SwallowBuffer                                swallowBuffer;
   private MessageDAO                                   messageDao;
   private AckDAO                                       ackDao;

   private CloseableBlockingQueue<SwallowMessage>       messageQueue;
   private ExecutorService                              ackExecutor;
   private PullStrategy                                 pullStgy;

   private volatile boolean                             getMessageisAlive       = true;
   private volatile boolean                             started                 = false;

   /** 可用来发送消息的channel */
   private BlockingQueue<Channel>                       freeChannels            = new LinkedBlockingQueue<Channel>();
   /** 存放已连接的channel，key是channel，value是ip */
   private ConcurrentHashMap<Channel, String>           connectedChannels       = new ConcurrentHashMap<Channel, String>();

   /** 记录当前最大的已经返回ack的消息id */
   //   private volatile long                                maxAckedMessageId                = 0L;
   //   private volatile long                                maxAckedBackupMessageId          = 0L;
   /** 记录当前最大的已经返回ack的消息seq */
   //   private volatile long                                maxAckedMessageSeq               = 0L;
   //   private volatile long                                maxAckedBackupMessageSeq         = 0L;
   private volatile ConsumerMessage                     maxAckedMessage;
   private volatile ConsumerMessage                     maxAckedBackupMessage;

   /** 记录当前最大的已经持久化的ack的消息id */
   private volatile long                                lastRecordedAckId       = 0L;
   private volatile long                                lastRecordedBackupAckId = 0L;
   /**
    * 发送后等待ack的消息。以channel为key，每个channel可以发N条消息(N为其threadSize)，该变量会被多线程访问，
    * 故需要保证线程安全。<br>
    * log(n)的操作，如果性能有问题，则尝试使用googlecode上的ConcurrentLinkedHashMap代替。
    */
   private ConcurrentSkipListMap<Long, ConsumerMessage> waitAckMessages         = new ConcurrentSkipListMap<Long, ConsumerMessage>();
   private ConcurrentSkipListMap<Long, ConsumerMessage> waitAckBackupMessages   = new ConcurrentSkipListMap<Long, ConsumerMessage>();

   /** maxAckedMessageSeq最多允许领先"最小的空洞waitAckMessage"的阈值 */
   private long                                         seqThreshold            = 100;
   /** 允许"最小的空洞waitAckMessage"存活的时间的阈值 */
   private long                                         waitAckExpiredSecond;

   private long                                         lastSeqForQPS           = 0;

   @SuppressWarnings("deprecation")
   public ConsumerWorkerImpl(ConsumerInfo consumerInfo, ConsumerWorkerManager workerManager, MessageFilter messageFilter) {
      this.consumerInfo = consumerInfo;
      this.ackDao = workerManager.getAckDAO();
      this.messageDao = workerManager.getMessageDAO();
      this.swallowBuffer = workerManager.getSwallowBuffer();
      this.threadFactory = workerManager.getThreadFactory();
      this.messageFilter = messageFilter;
      this.pullStgy = new DefaultPullStrategy(ConfigManager.getInstance().getPullFailDelayBase(), ConfigManager
            .getInstance().getPullFailDelayUpperBound());
      this.waitAckExpiredSecond = ConfigManager.getInstance().getWaitAckExpiredSecond() * SECOND;

      // consumerInfo的type不允许AT_MOST模式，遇到则修改成AT_LEAST模式（因为AT_MOST会导致ack插入比较频繁，所以不用它）
      if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_MOST_ONCE) {
         this.consumerInfo.setConsumerType(ConsumerType.DURABLE_AT_LEAST_ONCE);
         LOG.warn("ConsumerClient[consumerInfo=" + consumerInfo
               + "] used ConsumerType.DURABLE_AT_MOST_ONCE. Now change it to ConsumerType.DURABLE_AT_LEAST_ONCE.");
      }

      this.ackExecutor = new ThreadPoolExecutor(1, 1, Long.MAX_VALUE, TimeUnit.DAYS,
            new LinkedBlockingQueue<Runnable>(), new MQThreadFactory("swallow-ack-"));

      //创建消息缓冲QUEUE
      long messageIdOfTailMessage = getMaxMessageId(false);
      long messageIdOfTailBackupMessage = -1;
      if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
         messageIdOfTailBackupMessage = getMaxMessageId(true);
      }
      messageQueue = swallowBuffer.createMessageQueue(this.consumerInfo, messageIdOfTailMessage,
            messageIdOfTailBackupMessage, this.messageFilter);

      start();

   }

   @Override
   public void handleAck(final Channel channel, final long ackId, final ACKHandlerType type) {
      ackExecutor.execute(new Runnable() {
         @Override
         public void run() {
            try {

               //               LOG.info("Receive ACK(" + consumerInfo.getDest().getName() + "," + consumerInfo.getConsumerId() + ","
               //                     + ackedMsgId + ") from " + connectedChannels.get(channel));

               removeWaitAckMessages(channel, ackId);

               if (ACKHandlerType.CLOSE_CHANNEL.equals(type)) {
                  LOG.info("receive ack(type=" + type + ") from " + IPUtil.getIpFromChannel(channel));
                  channel.close();//channel.close()会触发netty调用handleChannelDisconnect(channel);
               } else if (ACKHandlerType.SEND_MESSAGE.equals(type)) {
                  freeChannels.add(channel);
               }
            } catch (Exception e) {
               LOG.error("handleAck wrong!", e);
            }
         }
      });

   }

   /**
    * 收到ack，则从WaitAckMessages中移除相应的消息，同时更新最大的ack message id <br>
    * 按照实现逻辑，该方法只会被单线程调用
    */
   private void removeWaitAckMessages(Channel channel, long ackId) {
      //waitAckMessages和waitAckBackupMessages可以有一样的msgId!，
      ConsumerMessage waitAckMessage = waitAckMessages.remove(ackId);
      //      System.out.println("waitAckMessage:" + waitAckMessage);
      //更新最大ack message id，更新最大seq
      if (waitAckMessage != null) {//ack属于正常消息的

         //TODO 临时代码
         //         ConsumerMessage waitAckMessage0 = waitAckBackupMessages.remove(ackId);
         //         if (waitAckMessage0 != null) {//ack属于备份消息的
         //            System.out.println("2个地方一个id：" + ackId);
         //         }

         if (maxAckedMessage == null || waitAckMessage.seq > maxAckedMessage.seq) {
            maxAckedMessage = waitAckMessage;
         }
         //         maxAckedMessageSeq = Math.max(maxAckedMessageSeq, waitAckMessage.seq);
         //         maxAckedMessageId = Math.max(maxAckedMessageId, ackedMsgId);
      } else {
         waitAckMessage = waitAckBackupMessages.remove(ackId);
         //         System.out.println("waitAckMessage-backup:" + waitAckMessage);
         if (waitAckMessage != null) {//ack属于备份消息的
            if (maxAckedBackupMessage == null || waitAckMessage.seq > maxAckedBackupMessage.seq) {
               maxAckedBackupMessage = waitAckMessage;
            }
            //            maxAckedBackupMessageSeq = Math.max(maxAckedBackupMessageSeq, waitAckMessage.seq);
            //            maxAckedBackupMessageId = Math.max(maxAckedBackupMessageId, waitAckMessage.message.getBackupMessageId());
            //cat打点，记录一次备份消息的ack
            catTraceForBackupAck(channel, ackId);
         }
      }
   }

   /**
    * 持久化ack的逻辑（该方法会被定时调用） 2个，waitAckMessages和waitAckBackupMessages，处理方式一样
    */
   @Override
   public void recordAck() {
      //根据SEQ计算QPS，调整seqThreshold；如qps=200，那么seqThreshold=2000(当然seqThreshold最少为100)
      long seqForQps = SEQ.get();
      long qps = seqForQps - lastSeqForQPS;
      seqThreshold = Math.max(qps * 30, 100);
      lastSeqForQPS = seqForQps;
      //TODO 将qps和seqThreshold打cat
      System.out.println("seqThreshold:" + seqThreshold);

      lastRecordedAckId = recordAck0(waitAckMessages, maxAckedMessage, lastRecordedAckId, false);
      lastRecordedBackupAckId = recordAck0(waitAckBackupMessages, maxAckedBackupMessage, lastRecordedBackupAckId, true);

   }

   private long recordAck0(ConcurrentSkipListMap<Long, ConsumerMessage> waitAckMessages0,
                           ConsumerMessage maxAckedMessage0, long lastRecordedAckId0, boolean isBackup) {
      //做超过阈值的判断，如果超过阈值，则移除并备份
      Entry<Long, ConsumerMessage> entry;
      while ((entry = waitAckMessages0.firstEntry()) != null) {//使用while，尽最大可能消除空洞ack id
         boolean overdue = false;//是否超过阈值

         ConsumerMessage minWaitAckMessage = entry.getValue();
         long minAckId = minWaitAckMessage.getAckId();
         if (maxAckedMessage0 != null && minAckId < maxAckedMessage0.getAckId()) {//大于最大ack id（maxAckedMessageId）的消息，不算是空洞
            //如果最小等待ack的消息，与最大已记录ack的消息，相差超过seqThreshold，则移除该消息到备份队列里。
            if (maxAckedMessage0.seq - minWaitAckMessage.seq > seqThreshold) {
               System.out.println("maxAckedMessage0.seq:" + maxAckedMessage0.seq + ",time:"
                     + new Date(maxAckedMessage0.gmt) + ",minWaitAckMessage.seq " + minWaitAckMessage.seq + ",time:"
                     + new Date(minWaitAckMessage.gmt) + ",isBackup:" + isBackup);
               overdue = true;
            }
            //如果最小等待ack的消息，与当前时间，相差超过waitAckTimeThreshold，则移除该消息到备份队列里。
            if (!overdue && System.currentTimeMillis() - minWaitAckMessage.gmt > waitAckExpiredSecond) {
               overdue = true;
            }
         }

         if (overdue && (minWaitAckMessage = waitAckMessages0.remove(minWaitAckMessage.message.getMessageId())) != null) {//超过阈值，则移除空洞
            if (minWaitAckMessage != null && this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
               messageDao.saveMessage(this.consumerInfo.getDest().getName(), this.consumerInfo.getConsumerId(),
                     minWaitAckMessage.message);
               //cat打点，记录一次备份消息的record
               System.out.println("overdue:" + new Date());//TODO 临时代码
               catTraceForBackupRecord(minWaitAckMessage.message);
            }
         } else {//没有移除任何空洞，则不再迭代；否则需要继续迭代以尽量多地移除空洞。
            break;//TODO
         }
      }

      //找到此时waitAckMessages0中最小的ack message id，减1后，即为应该持久化的消息id。
      //如果waitAckMessages0为空（没有任何空洞），那就是maxAckedMessageId(maxAckedMessageId是long型，故判断waitAckMessages0为空前后，maxAckedMessageId不会变)
      Long ackMessageId = null;
      entry = waitAckMessages0.firstEntry();
      if (entry != null) {
         ackMessageId = entry.getValue().getAckId() - 1;
      } else if (maxAckedMessage0 != null) {
         ackMessageId = maxAckedMessage0.getAckId();
      }

      //如果新的可记录ack的消息id，大于上次记录的最大ack，则可以记录ack
      if (ackMessageId != null && ackMessageId > lastRecordedAckId0) {
         if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
            ackDao.add(this.consumerInfo.getDest().getName(), this.consumerInfo.getConsumerId(), ackMessageId, "batch",
                  isBackup);
         }
         lastRecordedAckId0 = ackMessageId;
      }

      return lastRecordedAckId0;
   }

   @Override
   public synchronized void handleChannelDisconnect(Channel channel) {
      connectedChannels.remove(channel);

      //等待一下，如果对方已经返回ack回来，让其处理完
      try {
         Thread.sleep(100);
      } catch (InterruptedException e) {
         //netty自身的线程，不会有谁Interrupt。
      }

      removeByChannel(channel, waitAckMessages);
      removeByChannel(channel, waitAckBackupMessages);
   }

   private void removeByChannel(Channel channel, Map<Long, ConsumerMessage> waitAckMessages0) {
      Iterator<Entry<Long, ConsumerMessage>> it = waitAckMessages0.entrySet().iterator();
      while (it.hasNext()) {
         Entry<Long, ConsumerMessage> entry = (Entry<Long, ConsumerMessage>) it.next();
         ConsumerMessage consumerMessage = entry.getValue();
         if (consumerMessage.channel.equals(channel)) {
            if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
               messageDao.saveMessage(this.consumerInfo.getDest().getName(), this.consumerInfo.getConsumerId(),
                     consumerMessage.message);
               //cat打点，记录一次备份消息的record
               System.out.println("2");//TODO 临时代码
               catTraceForBackupRecord(consumerMessage.message);
            }
            it.remove();
         }
      }
   }

   private void start() {
      LOG.info("Worker(topic=" + consumerInfo.getDest().getName() + ",cid=" + consumerInfo.getConsumerId() + ") Start");
      threadFactory.newThread(new Runnable() {
         @Override
         public void run() {
            while (getMessageisAlive) {
               try {
                  Channel channel = freeChannels.take();
                  //如果未连接，则不做处理
                  if (channel.isConnected()) {

                     // 确保有消息可发
                     ConsumerMessage consumerMessage = pollMessage(channel);

                     // 拿出消息并发送
                     if (consumerMessage != null) {
                        sendMessage(channel, consumerMessage);
                     } else {// 没有消息，channel继续放回去
                        freeChannels.add(channel);
                     }

                  }
               } catch (InterruptedException e) {
                  LOG.info("Get message from messageQueue thread InterruptedException", e);
               } catch (RuntimeException e) {
                  LOG.info("Get message from messageQueue thread Exception", e);
               }

            }
            LOG.info("Message fetcher thread closed");
         }
      }, this.consumerInfo.getDest().getName() + "#" + this.consumerInfo.getConsumerId() + "-messageFetcher-").start();

   }

   private ConsumerMessage pollMessage(Channel channel) throws InterruptedException {
      ConsumerMessage consumerMessage = null;

      while (getMessageisAlive) {
         //从blockQueue中获取消息
         SwallowMessage message = (SwallowMessage) messageQueue.poll(pullStgy.fail(false), TimeUnit.MILLISECONDS);
         if (message != null) {
            if (!message.isBackup()) {
               consumerMessage = new ConsumerMessage(message, channel, SEQ.getAndIncrement());
            } else {
               consumerMessage = new ConsumerMessage(message, channel, BACKUP_SEQ.getAndIncrement());
            }
            pullStgy.succeess();
            break;
         }
      }

      //如果因为getMessageisAlive为false而退出（如收到close命令）,则消息可能依然是null
      return consumerMessage;
   }

   private void sendMessage(Channel channel, ConsumerMessage consumerMessage) throws InterruptedException {
      PktMessage pktMessage = new PktMessage(consumerInfo.getDest(), consumerMessage.message);

      //Cat begin
      Transaction consumerServerTransaction = Cat.getProducer().newTransaction(
            "Out:" + this.consumerInfo.getDest().getName(),
            consumerInfo.getConsumerId() + ":" + IPUtil.getIpFromChannel(channel));
      String childEventId;
      try {
         childEventId = Cat.getProducer().createMessageId();
         pktMessage.setCatEventID(childEventId);
         Cat.getProducer().logEvent(CatConstants.TYPE_REMOTE_CALL, "ConsumedByWhom",
               com.dianping.cat.message.Message.SUCCESS, childEventId);
      } catch (Exception e) {
         childEventId = "UnknownMessageId";
      }
      //Cat end

      try {
         //发送消息
         channel.write(pktMessage);

         //发送后，记录已发送但未收到ACK的消息记录
         if (!consumerMessage.message.isBackup()) {
            waitAckMessages.put(consumerMessage.message.getMessageId(), consumerMessage);
         } else {
            waitAckBackupMessages.put(consumerMessage.message.getMessageId(), consumerMessage);
         }

         //Cat begin
         consumerServerTransaction.addData("mid", pktMessage.getContent().getMessageId());
         consumerServerTransaction.setStatus(com.dianping.cat.message.Message.SUCCESS);
         //Cat end
      } catch (RuntimeException e) {
         LOG.error(consumerInfo.toString() + "：channel write error.", e);

         if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
            //发送失败，则放到backup队列里
            messageDao.saveMessage(consumerInfo.getDest().getName(), consumerInfo.getConsumerId(),
                  consumerMessage.message);
            //cat打点，记录一次备份消息的record
            System.out.println("3");//TODO 临时代码
            catTraceForBackupRecord(consumerMessage.message);
         }

         //Cat begin
         consumerServerTransaction.addData(pktMessage.getContent().toKeyValuePairs());
         consumerServerTransaction.setStatus(e);
         Cat.getProducer().logError(e);
      } finally {
         consumerServerTransaction.complete();
      }
      //Cat end
   }

   private void catTraceForBackupRecord(SwallowMessage message) {
      if (!message.isBackup()) {//一条消息可能会多次放进backup队列，第一次放进backup队列的，才打点。
         Transaction transaction = Cat.getProducer().newTransaction("Backup:" + consumerInfo.getDest().getName(),
               "In:" + consumerInfo.getConsumerId());
         if (message != null) {
            transaction.addData("message", message.toString());
         }
         transaction.setStatus(Message.SUCCESS);
         transaction.complete();
      } else {
         Transaction transaction = Cat.getProducer().newTransaction("Backup:" + consumerInfo.getDest().getName(),
               "In-Again:" + consumerInfo.getConsumerId());
         if (message != null) {
            transaction.addData("message", message.toString());
         }
         transaction.setStatus(Message.SUCCESS);
         transaction.complete();
      }
   }

   private void catTraceForBackupAck(Channel channel, Long messageId) {
      Transaction transaction = Cat.getProducer().newTransaction("Backup:" + consumerInfo.getDest().getName(),
            "Out:" + consumerInfo.getConsumerId());
      if (messageId != null) {
         transaction.addData("mid", messageId);
      }
      transaction.setStatus(Message.SUCCESS);
      transaction.complete();
   }

   @Override
   public void handleGreet(final Channel channel, final int clientThreadCount) {
      ackExecutor.execute(new Runnable() {
         @Override
         public void run() {

            connectedChannels.putIfAbsent(channel, IPUtil.getIpFromChannel(channel));
            started = true;
            for (int i = 0; i < clientThreadCount; i++) {
               freeChannels.add(channel);
            }
         }
      });
   }

   @Override
   public void closeMessageFetcherThread() {
      getMessageisAlive = false;
   }

   @Override
   public void closeAckExecutor() {
      ackExecutor.shutdownNow();
   }

   @Override
   public void close() {
      getMessageisAlive = false;
      messageQueue.close();
   }

   /**
    * @param topicName
    * @param consumerId consumerId为null时使用非backup队列
    * @param isBakcup
    * @return
    */
   private long getMaxMessageId(boolean isBakcup) {
      Long maxMessageId = null;
      String topicName = consumerInfo.getDest().getName();

      //持久类型，先尝试从数据库的ack表获取最大的id
      if (consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
         maxMessageId = ackDao.getMaxMessageId(topicName, consumerInfo.getConsumerId(), isBakcup);
      }

      //消息id不存在，则从消息队列里获取最大消息id
      if (maxMessageId == null) {
         maxMessageId = messageDao.getMaxMessageId(topicName, isBakcup ? consumerInfo.getConsumerId() : null);
         if (maxMessageId == null) {//不存在任何消息，则使用当前时间作为消息id即可
            maxMessageId = MongoUtils.getLongByCurTime();
         }

         if (consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
            //持久型且ack尚未有记录，则插入ack，表示以此ack为基准。
            ackDao.add(topicName, consumerInfo.getConsumerId(), maxMessageId, "inited", isBakcup);
         }
      }
      return maxMessageId;
   }

   @Override
   public boolean allChannelDisconnected() {
      return started && connectedChannels.isEmpty();
   }

   @Override
   public ConsumerType getConsumerType() {
      return consumerInfo.getConsumerType();
   }

   /**
    * 对消息的封装，含有其他辅助属性
    */
   private static class ConsumerMessage {
      private SwallowMessage message;
      /** 创建时间 */
      private long           gmt;
      /** 序号 */
      private long           seq;
      /** 连接 */
      private Channel        channel;

      public ConsumerMessage(SwallowMessage message, Channel channel, long seq) {
         super();
         this.message = message;
         this.channel = channel;
         this.gmt = System.currentTimeMillis();
         this.seq = seq;
      }

      @Override
      public String toString() {
         return "ConsumerMessage [message=" + message + ", gmt=" + gmt + ", seq=" + seq + ", channel=" + channel + "]";
      }

      public Long getAckId() {
         if (message.isBackup()) {
            return message.getBackupMessageId();
         } else {
            return message.getMessageId();
         }
      }

   }

}
