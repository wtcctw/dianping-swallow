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
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.config.ConfigManager;

public class ConsumerWorkerManager {

   private static final Logger             LOG                = LoggerFactory.getLogger(ConsumerWorkerManager.class);

   private AckDAO                          ackDAO;
   private Heartbeater                     heartbeater;
   private SwallowBuffer                   swallowBuffer;
   private MessageDAO                      messageDAO;

   private ConfigManager                   configManager      = ConfigManager.getInstance();

   private MQThreadFactory                 threadFactory      = new MQThreadFactory();

   private Map<ConsumerId, ConsumerWorker> consumerId2ConsumerWorker;

   private Thread                          idleWorkerManagerCheckerThread;
   private Thread                          maxAckedMessageIdUpdaterThread;

   private volatile boolean                readyForAcceptConn = true;

   public void setAckDAO(AckDAO ackDAO) {
      this.ackDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(ackDAO,
            configManager.getRetryIntervalWhenMongoException());
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
      this.messageDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(messageDAO,
            configManager.getRetryIntervalWhenMongoException());
   }

   public ConfigManager getConfigManager() {
      return configManager;
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
      LOG.info("stoping ConsumerWorker's Send-Message thread.");
      if (consumerId2ConsumerWorker != null) {
         for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
            entry.getValue().closeMessageFetcherThread();
         }
      }
      LOG.info("stoped.");

      //等待一段时间，以便让所有ConsumerWorker，可以从client端接收 “已发送但未收到ack的消息” 的ack
      try {
         long waitAckTimeWhenCloseSwc = configManager.getWaitAckTimeWhenCloseSwc();
         LOG.info("sleeping " + waitAckTimeWhenCloseSwc + "ms to wait receiving client's Acks.");
         Thread.sleep(configManager.getWaitAckTimeWhenCloseSwc());
         LOG.info("sleep done.");
      } catch (InterruptedException e) {
         LOG.error("close Swc thread InterruptedException", e);
      }

      //所有ConsumerWorker，不再处理接收到的ack(接收到，则丢弃)
      if (consumerId2ConsumerWorker != null) {
         for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
            entry.getValue().closeAckExecutor();
         }
      }

      //关闭ConsumerWorker的资源（关闭内部的“用于获取消息的队列”）
      if (consumerId2ConsumerWorker != null) {
         for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
            entry.getValue().close();
         }
      }

      //关闭“检测并关闭空闲ConsumerWorker”的后台线程
      if (idleWorkerManagerCheckerThread != null) {
         idleWorkerManagerCheckerThread.interrupt();
      }

      //等待“更新ack”的线程的关闭
      if (maxAckedMessageIdUpdaterThread != null) {
         try {
            maxAckedMessageIdUpdaterThread.interrupt();
            maxAckedMessageIdUpdaterThread.join();
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }

      //清空 “用于保存状态的” 2个map
      if (consumerId2ConsumerWorker != null) {
         consumerId2ConsumerWorker.clear();
      }
   }

   private ConsumerWorker findConsumerWorker(ConsumerInfo consumerInfo) {
      ConsumerId consumerId = consumerInfo.getConsumerId();
      return consumerId2ConsumerWorker.get(consumerId);
   }

   public Map<ConsumerId, ConsumerWorker> getConsumerId2ConsumerWorker() {
      return consumerId2ConsumerWorker;
   }

   private ConsumerWorker findOrCreateConsumerWorker(ConsumerInfo consumerInfo, MessageFilter messageFilter) {
      ConsumerWorker worker = findConsumerWorker(consumerInfo);
      if (worker == null) {
         // 以ConsumerId(String)为同步对象，如果是同一个ConsumerId，则串行化
         synchronized (consumerInfo.getConsumerId().getConsumerId().intern()) {
            if ((worker = findConsumerWorker(consumerInfo)) == null) {
               worker = new ConsumerWorkerImpl(consumerInfo, this, messageFilter, configManager.getSeqThreshold(),
                     configManager.getWaitAckTimeThreshold());
               ConsumerId consumerId = consumerInfo.getConsumerId();
               consumerId2ConsumerWorker.put(consumerId, worker);
            }
         }
      }
      return worker;
   }

   public void init(boolean isSlave) {
      if (!isSlave) {
         startHeartbeater(configManager.getMasterIp());
      }
   }

   /**
    * 启动。在close()之后，可以再次调用此start()方法进行启动
    */
   public void start() {
      readyForAcceptConn = true;

      consumerId2ConsumerWorker = new ConcurrentHashMap<ConsumerId, ConsumerWorker>();

      startIdleWorkerCheckerThread();
      startMaxAckedMessageIdUpdaterThread();
   }

   private void startMaxAckedMessageIdUpdaterThread() {
      maxAckedMessageIdUpdaterThread = threadFactory.newThread(new Runnable() {

         @Override
         public void run() {
            while (!Thread.currentThread().isInterrupted()) {
               for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
                  ConsumerWorker worker = entry.getValue();
                  //                  ConsumerId consumerId = entry.getKey();
                  //                  updateMaxAckedMessageId(worker, consumerId);
                  worker.recordAck();

               }

               // 轮询时有一定的时间间隔
               try {
                  Thread.sleep(configManager.getAckedMessageIdUpdateInterval());
               } catch (InterruptedException e) {
                  Thread.currentThread().interrupt();
               }
            }
            LOG.info("MaxAckedMessageIdUpdaterThread closed");
         }

      }, "maxAckedMessageIdUpdaterThread-");
      maxAckedMessageIdUpdaterThread.start();
   }

   //   private void updateMaxAckedMessageId(ConsumerWorker worker, ConsumerId consumerId) {
   //      Long lastSavedAckedMsgId = consumerId2MaxSavedAckedMessageId.get(consumerId);
   //      lastSavedAckedMsgId = lastSavedAckedMsgId == null ? 0 : lastSavedAckedMsgId;
   //      Long currentMaxAckedMsgId = worker.getMaxAckedMessageId();
   //      if (currentMaxAckedMsgId > 0 && currentMaxAckedMsgId > lastSavedAckedMsgId) {
   //         ackDAO.add(consumerId.getDest().getName(), consumerId.getConsumerId(), currentMaxAckedMsgId, "batch");
   //         consumerId2MaxSavedAckedMessageId.put(consumerId, currentMaxAckedMsgId);
   //      }
   //   }

   private void startIdleWorkerCheckerThread() {
      idleWorkerManagerCheckerThread = threadFactory.newThread(new Runnable() {

         @Override
         public void run() {
            while (!Thread.currentThread().isInterrupted()) {
               //轮询所有ConsumerWorker，如果其已经没有channel，则关闭ConsumerWorker,并移除
               for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
                  ConsumerWorker worker = entry.getValue();
                  ConsumerId consumerId = entry.getKey();
                  if (worker.allChannelDisconnected()) {
                     //                     updateMaxAckedMessageId(worker, consumerId);
                     worker.recordAck();
                     removeConsumerWorker(consumerId);
                     worker.closeMessageFetcherThread();
                     worker.closeAckExecutor();
                     worker.close();
                     LOG.info("ConsumerWorker for " + consumerId + " has no connected channel, close it");
                  }
               }
               // 轮询时有一定的时间间隔
               try {
                  Thread.sleep(configManager.getCheckConnectedChannelInterval());
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
                  Thread.sleep(configManager.getHeartbeatUpdateInterval());
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
   private void removeConsumerWorker(ConsumerId consumerId) {
      consumerId2ConsumerWorker.remove(consumerId);
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

}
