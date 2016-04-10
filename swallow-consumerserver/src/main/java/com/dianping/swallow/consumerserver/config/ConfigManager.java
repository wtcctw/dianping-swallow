package com.dianping.swallow.consumerserver.config;


import com.dianping.swallow.common.internal.config.AbstractConfig;

/**
 * @author zhang.yu
 */
public final class ConfigManager extends AbstractConfig{

   private static ConfigManager ins                             = new ConfigManager();

   // time related
   private int pullFailDelayBase               = 500;
   private int                  pullFailDelayUpperBound         = 3000;
   private long                 checkConnectedChannelInterval   = 10000L;
   private long                 retryIntervalWhenMongoException = 20L;
   private int                  retryTimesWhenMongoException 	= 50;
   private long                 closeChannelMaxWaitingTime      = 10000L;
   private int                  maxClientThreadCount            = 100;
   private int                  masterPort                      = 8081;
   private int                  slavePort                       = 8082;
   private int                  ackIdUpdateIntervalMili       	= 100;

   private int                  messageSendThreadPoolSize       = 1;
   private int 					maxRetriverTaskCountPerConsumer = 3;
   private int                  messageSendNoneInterval       	= 20;
   /**
    * maxAckedMessageSeq最多允许领先"最小的空洞waitAckMessage"的值为seq，seq = max(实时qps *
    * seqRatio,minSeqThreshold)
    */
   private int                  seqRatio                        = 30;
   private long                 minSeqThreshold                 = 100;
   /** 允许"最小的空洞waitAckMessage"存活的时间的阈值,单位秒，默认5分钟 */
   private long                 waitAckExpiredSecond            = 300;

   //Master Ip
   private String               masterIp                        = "127.0.0.1";
   private boolean 				isSlave							= false;

   private int 					minRetrieveInterval 			= 100;
   private int					backupMinRetrieveInterval		= 10000;


   private final int                  heartbeatCheckInterval          = 2000;
   private final int                  heartbeatMaxStopTime            = 10000;
   private final int                  heartbeatUpdateInterval         = 2000;
   /**
    * buffer 参数
    */
   private final int                  capacityOfBuffer                      = 1024;
   private final int                  minThresholdOfBuffer            = 216;
   private final int                  maxThresholdOfBuffer            = 1024;
   private final int                  maxRetriverTaskCountPerDest    = 3;
   private final int                  minRetrieveIntervalOfBuffer     = 100;


   /**
    * queue->buffer切换
    */
   private final int                  minSwitchInterval               = 60000;
   private final int                  maxSwitchInterval               = 1800000;
   private final int                  switchTimeUnit                  = 60000;

   public int getPullFailDelayBase() {
      return pullFailDelayBase;
   }

   public int getMaxClientThreadCount() {
      return maxClientThreadCount;
   }

   public int getMasterPort() {
      return masterPort;
   }

   public int getSlavePort() {
      return slavePort;
   }

   public long getCloseChannelMaxWaitingTime() {
      return closeChannelMaxWaitingTime;
   }

   public int getPullFailDelayUpperBound() {
      return pullFailDelayUpperBound;
   }

   public long getCheckConnectedChannelInterval() {
      return checkConnectedChannelInterval;
   }

   public long getRetryIntervalWhenMongoException() {
      return retryIntervalWhenMongoException;
   }

   public String getMasterIp() {
      return masterIp;
   }

   public int getHeartbeatCheckInterval() {
      return heartbeatCheckInterval;
   }

   /***
    * @return master consumer心跳最长的停止时间
    */
   public int getHeartbeatMaxStopTime() {
      return heartbeatMaxStopTime;
   }

   /***
    * @return master consumer更新心跳的间隔
    */
   public int getHeartbeatUpdateInterval() {
      return heartbeatUpdateInterval;
   }

   public int getSeqRatio() {
      return seqRatio;
   }

   public long getMinSeqThreshold() {
      return minSeqThreshold;
   }

   public long getWaitAckExpiredSecond() {
      return waitAckExpiredSecond;
   }

   public static void main(String[] args) {
      new ConfigManager();
   }

   public static ConfigManager getInstance() {
      return ins;
   }

   private ConfigManager() {
      this("swallow-consumerserver.properties");
   }

   private ConfigManager(String configFileName) {
	   super(configFileName);
	  loadConfig();
	  String masterIp = System.getProperty("masterIp");
      if (masterIp != null && masterIp.length() > 0) {
         this.masterIp = masterIp;
      }
   }

	public int getMinRetrieveInterval() {
		return minRetrieveInterval;
	}

	public int getBackupMinRetrieveInterval() {
		return backupMinRetrieveInterval;
	}

	public int getMessageSendNoneInterval() {
		return messageSendNoneInterval;
	}

	public void setMessageSendNoneInterval(int messageSendNoneInterval) {
		this.messageSendNoneInterval = messageSendNoneInterval;
	}

	public int getMessageSendThreadPoolSize() {
		return messageSendThreadPoolSize;
	}

	public void setMessageSendThreadPoolSize(int messageSendThreadPoolSize) {
		this.messageSendThreadPoolSize = messageSendThreadPoolSize;
	}

	public int getMaxRetriverTaskCountPerConsumer() {
		return maxRetriverTaskCountPerConsumer;
	}

	public void setMaxRetriverTaskCountPerConsumer(int maxRetriverTaskCountPerConsumer) {
		this.maxRetriverTaskCountPerConsumer = maxRetriverTaskCountPerConsumer;
	}

	public int getRetryTimesWhenMongoException() {
		return retryTimesWhenMongoException;
	}

	public void setRetryTimesWhenMongoException(int retryTimesWhenMongoException) {
		this.retryTimesWhenMongoException = retryTimesWhenMongoException;
	}

	public boolean isSlave() {
		return isSlave;
	}

	public void setSlave(boolean isSlave) {
		this.isSlave = isSlave;
	}

	public int getAckIdUpdateIntervalMili() {
		return ackIdUpdateIntervalMili;
	}

    public int getCapacityOfBuffer() {
      return capacityOfBuffer;
   }

    public int getMinThresholdOfBuffer() {
      return minThresholdOfBuffer;
   }

    public int getMaxThresholdOfBuffer() {
      return maxThresholdOfBuffer;
   }

    public int getMaxRetriverTaskCountPerDest() {
      return maxRetriverTaskCountPerDest;
   }

    public int getMinRetrieveIntervalOfBuffer() {
      return minRetrieveIntervalOfBuffer;
   }

    public int getMinSwitchInterval() {
        return minSwitchInterval;
    }

    public int getMaxSwitchInterval() {
      return maxSwitchInterval;
    }

    public int getSwitchTimeUnit() {
        return switchTimeUnit;
    }

}
