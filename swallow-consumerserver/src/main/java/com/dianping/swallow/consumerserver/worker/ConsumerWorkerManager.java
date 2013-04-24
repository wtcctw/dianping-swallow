package com.dianping.swallow.consumerserver.worker;

import java.util.Map;

import org.jboss.netty.channel.Channel;
import org.jboss.netty.util.internal.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.ConsumerType;
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

   private static final Logger             LOG                       = LoggerFactory
                                                                           .getLogger(ConsumerWorkerManager.class);

   private AckDAO                          ackDAO;
   private Heartbeater                     heartbeater;
   private SwallowBuffer                   swallowBuffer;
   private MessageDAO                      messageDAO;
   
   private ConfigManager                   configManager             = ConfigManager.getInstance();

   private MQThreadFactory                 threadFactory             = new MQThreadFactory();

   private Map<ConsumerId, ConsumerWorker> consumerId2ConsumerWorker;
   private Map<ConsumerId, Long> consumerId2MaxSavedAckedMessageId;

   private Thread idleWorkerManagerCheckerThread;
   private Thread maxAckedMessageIdUpdaterThread;

   private volatile boolean closed = false;
   
   public void setAckDAO(AckDAO ackDAO) {
      this.ackDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(ackDAO, configManager.getRetryIntervalWhenMongoException());
      //this.ackDAO = ackDAO;
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
      this.messageDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(messageDAO,configManager.getRetryIntervalWhenMongoException());
      //this.messageDAO = messageDAO;
   }

   public ConfigManager getConfigManager() {
      return configManager;
   }

   public void handleGreet(Channel channel, ConsumerInfo consumerInfo, int clientThreadCount, MessageFilter messageFilter) {
      findOrCreateConsumerWorker(consumerInfo, messageFilter).handleGreet(channel, clientThreadCount);
   }

   public void handleAck(Channel channel, ConsumerInfo consumerInfo, Long ackedMsgId, ACKHandlerType type) {
      ConsumerWorker worker = findConsumerWorker(consumerInfo);
      if (worker != null) {
         worker.handleAck(channel, ackedMsgId, type);
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
      //遍历所有ConsumerWorker，停止发送消息给client端
      LOG.info("stoping ConsumerWorker's Send-Message thread.");
      for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
         entry.getValue().closeMessageFetcherThread();
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
      for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
         entry.getValue().closeAckExecutor();
      }

      //关闭ConsumerWorker的资源（关闭内部的“用于获取消息的队列”）
      for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
         entry.getValue().close();
      }

      closed = true;

      //关闭“检测并关闭空闲ConsumerWorker”的后台线程
      if (idleWorkerManagerCheckerThread != null) {
         idleWorkerManagerCheckerThread.interrupt();
      }

      //等待“更新ack”的线程的关闭
      if (maxAckedMessageIdUpdaterThread != null) {
         try {
            maxAckedMessageIdUpdaterThread.join();
         } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
         }
      }
      
      //清空 “用于保存状态的” 2个map
      consumerId2ConsumerWorker.clear();
      consumerId2MaxSavedAckedMessageId.clear();
   }

   private ConsumerWorker findConsumerWorker(ConsumerInfo consumerInfo) {
      ConsumerId consumerId = consumerInfo.getConsumerId();
      return consumerId2ConsumerWorker.get(consumerId);
   }

   public Map<ConsumerId, ConsumerWorker> getConsumerId2ConsumerWorker() {
      return consumerId2ConsumerWorker;
   }

   private ConsumerWorker findOrCreateConsumerWorker(ConsumerInfo consumerInfo,MessageFilter messageFilter) {
      ConsumerWorker worker = findConsumerWorker(consumerInfo);
      if (worker == null) {
         synchronized (consumerId2ConsumerWorker) {
            if ( (worker = findConsumerWorker(consumerInfo)) == null) {
               worker = new ConsumerWorkerImpl(consumerInfo, this, messageFilter);
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
   public void start(){
       consumerId2ConsumerWorker = new ConcurrentHashMap<ConsumerId, ConsumerWorker>();
       consumerId2MaxSavedAckedMessageId = new ConcurrentHashMap<ConsumerId, Long>();

       startIdleWorkerCheckerThread();
       startMaxAckedMessageIdUpdaterThread();
   }

   private void startMaxAckedMessageIdUpdaterThread() {
      maxAckedMessageIdUpdaterThread = threadFactory.newThread(new Runnable() {

         @Override
         public void run() {
            while (!closed) {
               for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
                  ConsumerWorker worker = entry.getValue();
                  ConsumerId consumerId = entry.getKey();
                  updateMaxAckedMessageId(worker, consumerId);
               }

               // 轮询时有一定的时间间隔
               if(!closed){//如果未close，才进行sleep
                  try {
                     Thread.sleep(configManager.getMaxAckedMessageIdUpdateInterval());
                  } catch (InterruptedException e) {
                     break;
                  }
               }
            }
            LOG.info("MaxAckedMessageIdUpdaterThread closed");
         }

      }, "maxAckedMessageIdUpdaterThread-");
      maxAckedMessageIdUpdaterThread.start();
   }
   
   private void updateMaxAckedMessageId(ConsumerWorker worker, ConsumerId consumerId) {
      if(worker.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
         Long lastSavedAckedMsgId = consumerId2MaxSavedAckedMessageId.get(consumerId);
         lastSavedAckedMsgId = lastSavedAckedMsgId == null ? 0 : lastSavedAckedMsgId;
         Long currentMaxAckedMsgId = worker.getMaxAckedMessageId();
         if(currentMaxAckedMsgId > 0 && currentMaxAckedMsgId > lastSavedAckedMsgId) {
            ackDAO.add(consumerId.getDest().getName(), consumerId.getConsumerId(), currentMaxAckedMsgId, "batch");
            consumerId2MaxSavedAckedMessageId.put(consumerId, currentMaxAckedMsgId);
         }
      }
   }

   private void startIdleWorkerCheckerThread() {
      idleWorkerManagerCheckerThread = threadFactory.newThread(new Runnable() {

         @Override
         public void run() {
            while (!closed) {
               //轮询所有ConsumerWorker，如果其已经没有channel，则关闭ConsumerWorker,并移除
               for (Map.Entry<ConsumerId, ConsumerWorker> entry : consumerId2ConsumerWorker.entrySet()) {
                  ConsumerWorker worker = entry.getValue();
                  ConsumerId consumerId = entry.getKey();
                  if(worker.allChannelDisconnected()) {
                     updateMaxAckedMessageId(worker, consumerId);
                     removeConsumerWorker(consumerId);
                     worker.closeMessageFetcherThread();
                     worker.closeAckExecutor();
                     worker.close();
                     LOG.info("ConsumerWorker for " + consumerId + " has no connected channel, close it");
                  }
               }
               // 轮询时有一定的时间间隔
               if(!closed){//如果未close，才进行sleep
                  try {
                     Thread.sleep(configManager.getCheckConnectedChannelInterval());
                  } catch (InterruptedException e) {
                     break;
                  }
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
      consumerId2MaxSavedAckedMessageId.remove(consumerId);
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
