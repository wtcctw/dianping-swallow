package com.dianping.swallow.producer.impl.internal;


import com.dianping.pigeon.remoting.invoker.route.balance.LoadBalance;
import com.dianping.swallow.common.internal.config.AbstractConfig;

/**
 * ProducerFactory配置，默认构造函数生成的配置为：<br />
 * <br />
 * <table>
 * <tr>
 * <td>serviceName</td>
 * <td>=</td>
 * <td>"http://service.dianping.com/swallowService/producerService_1.0.0"</td>
 * </tr>
 * <tr>
 * <td>serialize</td>
 * <td>=</td>
 * <td>"hessian"</td>
 * </tr>
 * <tr>
 * <td>timeout</td>
 * <td>=</td>
 * <td>5000</td>
 * </tr>
 * <tr>
 * <td>useLion</td>
 * <td>=</td>
 * <td>true</td>
 * </tr>
 * <tr>
 * <td>hosts</td>
 * <td>=</td>
 * <td>"127.0.0.1:4000"</td>
 * </tr>
 * <tr>
 * <td>weights</td>
 * <td>=</td>
 * <td>"1"</td>
 * </tr>
 * <tr>
 * <td>punishTimeout</td>
 * <td>=</td>
 * <td>500</td>
 * </tr>
 * </table>
 * 
 * @author tong.song
 */
public final class SwallowPigeonConfiguration extends AbstractConfig{

   public static final String  DEFAULT_SERVICE_NAME   = "http://service.dianping.com/swallowService/producerService_1.0.0"; //默认远程服务名称
   public static final String  DEFAULT_SERIALIZE      = "hessian";                                                         //默认序列化方式
   public static final int     DEFAULT_TIMEOUT        = 10000;                                                              //默认远程调用延时
   public static final boolean DEFAULT_IS_USE_LION    = true;                                                              //默认是否使用Lion以配置Swallow server地址
   public static final String  DEFAULT_WEIGHTS        = "1";                                                               //默认Swallow server权重
   public static final int     DEFAULT_RETRY_BASE_INTERVAL = 500;                                                             //默认失败重试延时基数
   public static final int     DEFAULT_FAILED_BASE_INTERVAL = 1;                                                              //默认失败后重新获取前的间隔的延时基数
   public static final int     DEFAULT_FILE_QUEUE_FAILED_RETRY_BASE_INTERVAL = 5000;                                                               //默认失败重试延时基数
   public static final String  DEFAULT_LOAD_BALANCE   = "random";

   private String              serviceName            = DEFAULT_SERVICE_NAME;                                              //远程调用服务名称，需与Swallow server端配置的服务名称完全一致
   private String              serialize              = DEFAULT_SERIALIZE;                                                 //序列化方式，共有四种：hessian,java,protobuf以及thrift
   private int                 timeout                = DEFAULT_TIMEOUT;                                                   //远程调用延时
   private int                 retryBaseInterval      = DEFAULT_RETRY_BASE_INTERVAL;                                       //失败重试延时基数
   private int                 failedBaseInterval     = DEFAULT_FAILED_BASE_INTERVAL;                                     //失败后重新获取前的间隔的延时基数
   private int                 punishTimeout          = -1;                                            //失败重试延时基数(旧的，由于名称不合理，废弃使用)
   private int                 fileQueueFailedBaseInterval      = DEFAULT_FILE_QUEUE_FAILED_RETRY_BASE_INTERVAL;                                            //失败重试延时基数
   private String              loadBalance            = DEFAULT_LOAD_BALANCE;
   private LoadBalance         loadBalanceObj         = new SwallowPigeonLoadBalance();

   public SwallowPigeonConfiguration() {
      //默认配置
   }

   @Override
   public String toString() {
      return "serviceName=" + serviceName + "; serialize=" + serialize + "; timeout=" + timeout + "retryBaseInterval="
            + retryBaseInterval + "; punishTimeout=" + punishTimeout + "; failedBaseInterval=" + failedBaseInterval + "; fileQueueFailedBaseInterval=" + fileQueueFailedBaseInterval;
   }

   private String getConfigInfo() {
      return "serviceName=" + serviceName + "; serialize=" + serialize + "; timeout=" + timeout + "; retryBaseInterval="
            + retryBaseInterval + "; punishTimeout=" + punishTimeout + "; failedBaseInterval=" + failedBaseInterval + "; fileQueueFailedBaseInterval=" + fileQueueFailedBaseInterval;
   }

   public SwallowPigeonConfiguration(String configFile) {
	   
	  super(configFile);
	  loadConfig();
      checkSerialize();
      checkTimeout();
      checkRetryBaseInterval();
      checkFileQueueFailedBaseInterval();
      
      if(logger.isInfoEnabled()){
    	  logger.info("ProducerFactory configuration: [" + getConfigInfo() + "]");
      }
      
      if (punishTimeout > 0) {//兼容老的punishTimeout参数，如果配置了punishTimeout，就提示警告
          logger.warn("Property 'punishTimeout' is deprecated(but still work) after version 0.6.7, please use retryBaseInterval instead.");
      }
   }

   /**
    * 检查序列化方式
    */
   private void checkSerialize() {
      if (!"hessian".equals(serialize) && !"java".equals(serialize) && !"protobuf".equals(serialize)
            && !"thrift".equals(serialize)) {
         logger.warn("[Unrecognized serialize, use default value: " + DEFAULT_SERIALIZE + ".]");
         serialize = DEFAULT_SERIALIZE;
      }
   }

   /**
    * 检查Timeout是否合法，Timeout>0
    */
   private void checkTimeout() {
      if (timeout <= 0) {
         timeout = DEFAULT_TIMEOUT;
         logger.warn("Timeout should be more than 0, use default value.");
      }
   }

   private void checkRetryBaseInterval() {
      if (retryBaseInterval <= 0) {
          retryBaseInterval = DEFAULT_RETRY_BASE_INTERVAL;
         logger.warn("retryBaseInterval should be more than 0, use default value.");
      }
   }
   
   private void checkFileQueueFailedBaseInterval() {
       if (fileQueueFailedBaseInterval <= 0) {
           fileQueueFailedBaseInterval = DEFAULT_FILE_QUEUE_FAILED_RETRY_BASE_INTERVAL;
          logger.warn("fileQueueFailedBaseInterval should be more than 0, use default value.");
       }
    }

   public String getServiceName() {
      return serviceName;
   }

   public void setServiceName(String serviceName) {
      this.serviceName = serviceName;
   }

   public String getSerialize() {
      return serialize;
   }

   public void setSerialize(String serialize) {
      this.serialize = serialize;
      checkSerialize();
   }

   public int getTimeout() {
      return timeout;
   }

   public void setTimeout(int timeout) {
      this.timeout = timeout;
      checkTimeout();
   }

   public int getRetryBaseInterval() {
       if (punishTimeout > 0) {//兼容老的punishTimeout参数，如果配置了punishTimeout，就使用punishTimeout
           return punishTimeout;
       }
      return retryBaseInterval;
   }

   public void setRetryBaseInterval(int retryBaseInterval) {
      this.retryBaseInterval = retryBaseInterval;
      checkRetryBaseInterval();
   }

   public int getFailedBaseInterval() {
      return failedBaseInterval;
   }

   public void setFailedBaseInterval(int failedBaseInterval) {
      this.failedBaseInterval = failedBaseInterval;
   }

   public int getFileQueueFailedBaseInterval() {
      return fileQueueFailedBaseInterval;
   }

   public void setFileQueueFailedBaseInterval(int fileQueueFailedBaseInterval) {
      this.fileQueueFailedBaseInterval = fileQueueFailedBaseInterval;
      checkFileQueueFailedBaseInterval();
   }

   public String getLoadBalance() {
      return loadBalance;
   }

   public void setLoadBalance(String loadBalance) {
      this.loadBalance = loadBalance;
   }


   public LoadBalance getLoadBalanceObj() {
      return loadBalanceObj;
   }

   public void setLoadBalanceObj(LoadBalance loadBalanceObj) {
      this.loadBalanceObj = loadBalanceObj;
   }
}
