package com.dianping.swallow.consumerserver.bootstrap;


import com.dianping.swallow.consumerserver.config.ConfigManager;

/**
 *  slave启动
 * @author mengwenchao
 *
 * 2015年6月15日 下午5:04:52
 */
public class SlaveBootStrap extends AbstractBootStrap{

   private static boolean          isSlave   = true;

   private SlaveBootStrap() {
   }

   public static void main(String[] args) {
	   
	   new SlaveBootStrap().run();
   }

	private void run() {
		
	    ConfigManager configManager = ConfigManager.getInstance();
		
	    createContext();
	    createShutdownHook();
	
	    if(logger.isInfoEnabled()){
	    	logger.info("slave starting, master ip: " + configManager.getMasterIp());
	    }
	
	    while (!closed) {
	
	       try {
	          heartbeater.waitUntilMasterDown(configManager.getMasterIp(), configManager.getHeartbeatCheckInterval(),
	                configManager.getHeartbeatMaxStopTime());
	       } catch (InterruptedException e) {
	          logger.warn("slave interruptted, will stop", e);
	          break;
	       }
	       
	       startConsumerServer();
	       bootstrap = startNetty(ConfigManager.getInstance().getSlavePort());
	
	       try {
	          heartbeater.waitUntilMasterUp(configManager.getMasterIp(), configManager.getHeartbeatCheckInterval(),
	                configManager.getHeartbeatMaxStopTime());
	       } catch (InterruptedException e) {
	          logger.warn("slave interruptted, will stop", e);
	          break;
	       }
	       closeNettyRelatedResource();
	       stopConsumerServer();
	    }
	    
	    if(logger.isInfoEnabled()){
	    	logger.info("slave stopped");
	    }
	}

	@Override
	protected boolean isSlave() {
		return isSlave;
	}
}
