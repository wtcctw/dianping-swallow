package com.dianping.swallow.consumerserver.worker;

import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.dao.AckDAO;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.internal.util.ProxyUtil;
import com.dianping.swallow.consumerserver.Heartbeater;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;

public class ConsumerWorkerManager {

   private static final Logger               LOG                   = LoggerFactory
                                                                         .getLogger(ConsumerWorkerManager.class);

   private final long                        ACKID_UPDATE_INTERVAL = ConfigManager.getInstance()
                                                                         .getAckIdUpdateIntervalSecond() * 1000;

   private AckDAO                            ackDAO;
   private Heartbeater                       heartbeater;
   private SwallowBuffer                     swallowBuffer;
   private MessageDAO                        messageDAO;

   private ConsumerAuthController            consumerAuthController;

   private MQThreadFactory                   threadFactory         = new MQThreadFactory();

   private Map<ConsumerInfo, ConsumerWorker> consumerInfo2ConsumerWorker;

   private Thread                            idleWorkerManagerCheckerThread;
   private Thread                            ackIdUpdaterThread;

   private volatile boolean                  readyForAcceptConn    = true;

   public void setAckDAO(AckDAO ackDAO) {
      this.ackDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(ackDAO, ConfigManager.getInstance()
            .getRetryIntervalWhenMongoException());
   }

   public MQThreadFactory getThreadFactory() {
      return threadFactory;
   }

   public void setHeartbeater(Heartbeater heartbeater) {
      this.heartbeater = heartbeater;
   }

   public void setSwallowBuffer(SwallowBuffer swallowBuffer) {
      this.swallowBuffer = swallowBuffer;
   }

   public void setMessageDAO(MessageDAO messageDAO) {
      this.messageDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(messageDAO, ConfigManager.getInstance()
            .getRetryIntervalWhenMongoException());
   }

   public void handleGreet(Channel channel, ConsumerInfo consumerInfo, int clientThreadCount,
                           MessageFilter messageFilter) {
      if (!readyForAcceptConn) {
         //接收到连接，直接就关闭它
         channel.close();
      } else {
         findOrCreateConsumerWorker(consumerInfo, messageFilter).handleGreet(channel, clientThreadCount);
      }
   }

   public void handleAck(Channel channel, ConsumerInfo consumerInfo, Long ackedMsgId, ACKHandlerType type) {
      ConsumerWorker worker = findConsumerWorker(consumerInfo);
      if (worker != null) {
         if (ackedMsgId != null) {
            worker.handleAck(channel, ackedMsgId, type);
         }
      } else {
         LOG.warn(consumerInfo + "ConsumerWorker is not exist!");
         channel.close();
      }
   }

   public void handleChannelDisconnect(Channel channel, ConsumerInfo consumerInfo) {
      ConsumerWorker worker = findConsumerWorker(consumerInfo);
      if (worker != null) {
         worker.handleChannelDisconnect(channel);
      }
   }

   public void close() {
      // 停止接收client的新连接
      readyForAcceptConn = false;

      //遍历所有ConsumerWorker，停止发送消息给client端
      LOG.info("Stoping ConsumerWorker's Send-Message thread.");
      if (consumerInfo2ConsumerWorker != null) {
         for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
            entry.getValue().closeMessageFetcherThread();
         }
      }
      LOG.info("Stoped.");

      //等待一段时间，以便让所有ConsumerWorker，可以从client端接收 “已发送但未收到ack的消息” 的ack
      try {
         long waitAckTimeWhenCloseSwc = ConfigManager.getInstance().getWaitAckTimeWhenCloseSwc();
         LOG.info("Sleeping " + waitAckTimeWhenCloseSwc + "ms to wait receiving client's Acks.");
         Thread.sleep(waitAckTimeWhenCloseSwc);
         LOG.info("Sleep done.");
      } catch (InterruptedException e) {
         LOG.error("Close Swc thread InterruptedException", e);
      }

      //所有ConsumerWorker，不再处理接收到的ack(接收到，则丢弃)
      if (consumerInfo2ConsumerWorker != null) {
         for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
            entry.getValue().closeAckExecutor();
         }
      }

      //关闭ConsumerWorker的资源（关闭内部的“用于获取消息的队列”）
      if (consumerInfo2ConsumerWorker != null) {
         for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
            entry.getValue().close();
         }
      }

      //关闭“检测并关闭空闲ConsumerWorker”的后台线程
      if (idleWorkerManagerCheckerThread != null) {
         idleWorkerManagerCheckerThread.interrupt();
      }

      //等待“更新ack”的线程的关闭
      if (ackIdUpdaterThread != null) {
         try {
            ackIdUpdaterThread.interrupt();
            ackIdUpdaterThread.join();
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }

      //清空 “用于保存状态的” 2个map
      if (consumerInfo2ConsumerWorker != null) {
         consumerInfo2ConsumerWorker.clear();
      }
   }

   private ConsumerWorker findConsumerWorker(ConsumerInfo consumerInfo) {
      return consumerInfo2ConsumerWorker.get(consumerInfo);
   }

   public Map<ConsumerInfo, ConsumerWorker> getConsumerId2ConsumerWorker() {
      return consumerInfo2ConsumerWorker;
   }

   private ConsumerWorker findOrCreateConsumerWorker(ConsumerInfo consumerInfo, MessageFilter messageFilter) {
      ConsumerWorker worker = findConsumerWorker(consumerInfo);
      if (worker == null) {
         // 以ConsumerId(String)为同步对象，如果是同一个ConsumerId，则串行化
         synchronized (consumerInfo.getConsumerId().intern()) {
            if ((worker = findConsumerWorker(consumerInfo)) == null) {
               worker = new ConsumerWorkerImpl(consumerInfo, this, messageFilter, consumerAuthController);
               consumerInfo2ConsumerWorker.put(consumerInfo, worker);
            }
         }
      }
      return worker;
   }

   public void init(boolean isSlave) {
      if (!isSlave) {
         startHeartbeater(ConfigManager.getInstance().getMasterIp());
      }
   }

   /**
    * 启动。在close()之后，可以再次调用此start()方法进行启动
    */
   public void start() {
      readyForAcceptConn = true;

      consumerInfo2ConsumerWorker = new ConcurrentHashMap<ConsumerInfo, ConsumerWorker>();

      startIdleWorkerCheckerThread();
      startAckIdUpdaterThread();
   }

   private void startAckIdUpdaterThread() {
      ackIdUpdaterThread = threadFactory.newThread(new Runnable() {

         @Override
         public void run() {
            while (!Thread.currentThread().isInterrupted()) {
               for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
                  ConsumerWorker worker = entry.getValue();
                  worker.recordAck();
               }

               // 轮询时有一定的时间间隔
               try {
                  Thread.sleep(ACKID_UPDATE_INTERVAL);
               } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
               }
            }
            LOG.info("AckIdUpdaterThread closed");
         }

      }, "AckIdUpdaterThread-");
      ackIdUpdaterThread.start();
   }

   private void startIdleWorkerCheckerThread() {
      idleWorkerManagerCheckerThread = threadFactory.newThread(new Runnable() {

         @Override
         public void run() {
            while (!Thread.currentThread().isInterrupted()) {
               //轮询所有ConsumerWorker，如果其已经没有channel，则关闭ConsumerWorker,并移除
               for (Map.Entry<ConsumerInfo, ConsumerWorker> entry : consumerInfo2ConsumerWorker.entrySet()) {
                  ConsumerWorker worker = entry.getValue();
                  ConsumerInfo consumerInfo = entry.getKey();
                  if (worker.allChannelDisconnected()) {
                     worker.recordAck();
                     removeConsumerWorker(consumerInfo);
                     worker.closeMessageFetcherThread();
                     worker.closeAckExecutor();
                     worker.close();
                     LOG.info("ConsumerWorker for " + consumerInfo + " has no connected channel, close it");
                  }
               }
               // 轮询时有一定的时间间隔
               try {
                  Thread.sleep(ConfigManager.getInstance().getCheckConnectedChannelInterval());
               } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
               }
            }
            LOG.info("idle ConsumerWorker checker thread closed");
         }

      }, "idleConsumerWorkerChecker-");
      idleWorkerManagerCheckerThread.setDaemon(true);
      idleWorkerManagerCheckerThread.start();
   }

   private void startHeartbeater(final String ip) {

      Runnable runnable = new Runnable() {

         @Override
         public void run() {
            while (true) {

               try {
                  heartbeater.beat(ip);
                  Thread.sleep(ConfigManager.getInstance().getHeartbeatUpdateInterval());
               } catch (Exception e) {
                  LOG.error("Error update heart beat", e);
               }
            }
         }

      };

      Thread heartbeatThread = threadFactory.newThread(runnable, "heartbeat-");
      heartbeatThread.setDaemon(true);
      heartbeatThread.start();
   }

   /**
    * consumerId对应的ConsumerWorker已经没有任何连接，所以移除ConsumerWorker
    */
   private void removeConsumerWorker(ConsumerInfo consumerInfo) {
      consumerInfo2ConsumerWorker.remove(consumerInfo);
   }

   public AckDAO getAckDAO() {
      return ackDAO;
   }

   public SwallowBuffer getSwallowBuffer() {
      return swallowBuffer;
   }

   public MessageDAO getMessageDAO() {
      return messageDAO;
   }

   public void setConsumerAuthController(ConsumerAuthController consumerAuthController) {
      this.consumerAuthController = consumerAuthController;
   }

}
