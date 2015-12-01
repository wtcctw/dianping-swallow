package com.dianping.swallow.consumerserver.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.dao.HeartbeatDAO;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.DateUtils;
import com.dianping.swallow.common.internal.util.ProxyUtil;
import com.dianping.swallow.consumerserver.Heartbeater;
import com.dianping.swallow.consumerserver.config.ConfigManager;

public class MongoHeartbeater extends AbstractLifecycle implements Heartbeater {

   private static final Logger logger           = LoggerFactory.getLogger(MongoHeartbeater.class);
   
   private HeartbeatDAO<?>        heartbeatDAO;
   private ConfigManager       configManager = ConfigManager.getInstance();
   
   private Thread 			   heartbeatThread;

   public void setHeartbeatDAO(HeartbeatDAO<?> heartbeatDAO) {
      this.heartbeatDAO = ProxyUtil.createMongoDaoProxyWithRetryMechanism(heartbeatDAO,
    		  configManager.getRetryIntervalWhenMongoException(), configManager.getRetryTimesWhenMongoException());
   }
   
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		
		if(!ConfigManager.getInstance().isSlave()){
			startHeartbeater();
		}
		
	}
	
    private void startHeartbeater() {

    	if(logger.isInfoEnabled()){
    		logger.info("[startHeartbeater]");
    	}
    	
        Runnable runnable = new Runnable() {
        	
            @Override
            public void run() {
            	
                while (!Thread.interrupted()) {
                	
                    try {
                        beat(ConfigManager.getInstance().getMasterIp());
                        Thread.sleep(ConfigManager.getInstance().getHeartbeatUpdateInterval());
                    } catch(InterruptedException e){
                    	logger.error("Error update heart beat", e);
                    	Thread.currentThread().interrupt();
                    }catch (Throwable e) {
                        logger.error("Error update heart beat", e);
                    }
                }
            }
        };

        heartbeatThread = new Thread(runnable, "heartbeater");
        heartbeatThread.setDaemon(true);
        heartbeatThread.start();
    }

	
	@Override
	protected void doDispose() throws Exception {
		super.doDispose();
		
		heartbeatThread.interrupt();
	}

   @Override
   public void beat(String ip) {
      heartbeatDAO.updateLastHeartbeat(ip);
   }

   @Override
   public void waitUntilMasterDown(String ip, long checkInterval, long maxStopTime) throws InterruptedException {
	   
      long startTime = System.currentTimeMillis();
      logger.info("start to wait " + ip + " master stop beating");
      System.out.println(DateUtils.current() + "[start to wait " + ip + " master stop beating]");//检查是否启动成功
      while (true) {
         Date beat = null;
         beat = heartbeatDAO.findLastHeartbeat(ip);
         if (beat == null) {
            logger.warn(ip + " no beat");
            if (System.currentTimeMillis() - startTime > maxStopTime) {
               break;
            }
         } else {
            if (logger.isDebugEnabled()) {
               logger.debug(ip + " beat at " + beat.getTime());
            }
            long now = System.currentTimeMillis();
            long lastBeatTime = beat.getTime();
            if (now - lastBeatTime > maxStopTime) {
               break;
            }
         }
         Thread.sleep(checkInterval);
      }
      logger.info(ip + " master stop beating, slave waked up");
   }

   @Override
   public void waitUntilMasterUp(String ip, long checkInterval, long maxStopTime) throws InterruptedException {
      Date beat = null;
      logger.info("start to wait " + ip + " master up");
      while (true) {
         try {
            beat = heartbeatDAO.findLastHeartbeat(ip);
         } catch (Exception e) {
            logger.error("error find last heartbeat", e);
            Thread.sleep(1000);
            continue;
         }
         if (beat != null) {
            long lastBeatTime = beat.getTime();
            long now = System.currentTimeMillis();
            if (now - lastBeatTime < maxStopTime) {
               if (logger.isDebugEnabled()) {
                  logger.debug(ip + " beat at " + beat.getTime());
               }
               break;
            }
         }
         Thread.sleep(checkInterval);
      }
      logger.info(ip + " master up, slave shutdown");
   }

}
