package com.dianping.swallow.consumer;

import java.util.Date;

import org.bson.types.BSONTimestamp;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.util.MongoUtils;

/**
 * 该类用于设置Consumer的选项，每个Consumer对应一个ConsumerConfig
 * 
 * @author kezhu.wu
 */
public class ConsumerConfig {

    private static final Logger    logger           = LogManager.getLogger(ConsumerConfig.class);

    private int           threadPoolSize                           = 1;
    private MessageFilter messageFilter                            = MessageFilter.AllMatchFilter;
    private ConsumerType  consumerType                             = ConsumerType.DURABLE_AT_LEAST_ONCE;
    /**
    * 当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，2
    * 次重试之间最小的停顿时间
    */
    private int           delayBaseOnBackoutMessageException       = 100;                               //ms
    /**
    * 当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，2
    * 次重试之间最大的停顿时间
    */
    private int           delayUpperboundOnBackoutMessageException = 3000;                              //ms
    /** 当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，最多重试的次数 */
    private int           retryCount      = 5;                                 //重试次数

    /** 当需要在建立连接的时候指定读取消息的位置，可以设置该参数指定 */
    private long          startMessageId                           = -1;
    
    /**
     * 任务运行时间过长，报警，此为运行时间报警阈值；设置为0，则不检查
     */
	private int 		  longTaskAlertTime						   = 5000;

    public int getThreadPoolSize() {
      return threadPoolSize;
    }

    /**
    * 设置consumer处理消息的线程池线程数，默认为1
    * <note>注意，如果设置成多线程，那么会有多线程同时接收消息，这样的话接收的消息就无法保证其先后顺序) </note>
    *
    * @param threadPoolSize
    */
    public void setThreadPoolSize(int threadPoolSize) {
      this.threadPoolSize = threadPoolSize;
    }

    /**
    * 返回消息过滤方式
    */
    public MessageFilter getMessageFilter() {
      return messageFilter;
    }

    /**
    * 设置消息过滤方式
    *
    * @param messageFilter
    */
    public void setMessageFilter(MessageFilter messageFilter) {
      this.messageFilter = messageFilter;
    }

    /**
    * Consumer的类型，包括3种类型：<br>
    * 1.AT_MOST：尽量保证消息最多消费一次，不出现重复消费（注意：只是尽量保证，而非绝对保证。）<br>
    * 2.AT_LEAST：尽量保证消息最少消费一次，不出现消息丢失的情况（注意：只是尽量保证，而非绝对保证。）<br>
    * 3.NON_DURABLE：临时的消费类型，从当前的消息开始消费，不会对消费状态进行持久化，Server重启后将重新开始。
    */
    public ConsumerType getConsumerType() {
      return consumerType;
    }

    /**
    * Consumer的类型，包括3种类型：<br>
    * 1.AT_MOST：尽量保证消息最多消费一次，不出现重复消费（注意：只是尽量保证，而非绝对保证。）<br>
    * 2.AT_LEAST：尽量保证消息最少消费一次，不出现消息丢失的情况（注意：只是尽量保证，而非绝对保证。）<br>
    * 3.NON_DURABLE：临时的消费类型，从当前的消息开始消费，不会对消费状态进行持久化，Server重启后将重新开始。
    */
    public void setConsumerType(ConsumerType consumerType) {
      this.consumerType = consumerType;
    }

    /**
    * 当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，2
    * 次重试之间最小的停顿时间 *
    * <p>
    * 默认值为100
    * </p>
    */
    public int getDelayBaseOnBackoutMessageException() {
      return delayBaseOnBackoutMessageException;
    }

    /**
    * 当MessageListener.onMessage(Message)抛出BackoutMessageException异常时，2
    * 次重试之间最小的停顿时间 *
    * <p>
    * 默认值为100
    * </p>
    */
    public void setDelayBaseOnBackoutMessageException(int delayBaseOnBackoutMessageException) {
      this.delayBaseOnBackoutMessageException = delayBaseOnBackoutMessageException;
    }

    /**
    * delayUpperboundOnBackoutMessageException表示“当MessageListener.onMessage(
    * Message)抛出BackoutMessageException异常时，2次重试之间最大的停顿时间” *
    * <p>
    * 默认值为3000
    * </p>
    */
    public int getDelayUpperboundOnBackoutMessageException() {
      return delayUpperboundOnBackoutMessageException;
    }

    /**
    * delayUpperboundOnBackoutMessageException表示“当MessageListener.onMessage(
    * Message)抛出BackoutMessageException异常时，2次重试之间最大的停顿时间” *
    * <p>
    * 默认值为3000
    * </p>
    */
    public void setDelayUpperboundOnBackoutMessageException(int delayUpperboundOnBackoutMessageException) {
      this.delayUpperboundOnBackoutMessageException = delayUpperboundOnBackoutMessageException;
    }

    /**
     * use getRetryCount instead
     * @return
     */
    @Deprecated
    public int getRetryCountOnBackoutMessageException() {
      return retryCount;
    }

    /**
     * 	请使用方法：setRetryCount()
     * @param retryCountOnBackoutMessageException
     */
    @Deprecated
    public void setRetryCountOnBackoutMessageException(int retryCountOnBackoutMessageException) {
    	setRetryCount(retryCountOnBackoutMessageException);
    }

    public long getStartMessageId()
    {
        return startMessageId;
    }

    public void setStartMessageId(long startMessageId)
    {
        this.startMessageId = startMessageId;
    }

    
    public static long fromDateToMessageId(Date beginDate){
		return MongoUtils.BSONTimestampToLong(new BSONTimestamp((int)(beginDate.getTime()/1000), 0));
    }
    
    @Override
    public String toString() {
        return String
            .format(
                  "ConsumerConfig "
                  + "[threadPoolSize=%s, messageFilter=%s, consumerType=%s, "
                  + "delayBaseOnBackoutMessageException=%s, delayUpperboundOnBackoutMessageException=%s, "
                  + "retryCountOnBackoutMessageException=%s, startMessageId=%s, longTaskAlertTime=%s]",
                  threadPoolSize, messageFilter, consumerType, delayBaseOnBackoutMessageException,
                  delayUpperboundOnBackoutMessageException, retryCount, startMessageId, longTaskAlertTime);
    }

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		
	       if (retryCount == -1) {
	           retryCount = Integer.MAX_VALUE;
	       }
	       if (retryCount < -1) {
	           logger.warn("invalid retryCountOnBackoutMessageException, use default value: " + this.retryCount + ".");
	           return;
	       }
	       this.retryCount = retryCount;
	}

	
	/**
	 * 任务运行时间过长，报警，此为运行时间报警阈值
	 * @return 
	 */
	public int getLongTaskAlertTime() {
		return longTaskAlertTime;
	}

	/**
	 * 
	 * @param longTaskAlertTime 任务运行时间过长，报警，此为运行时间报警阈值
	 */
	public void setLongTaskAlertTime(int longTaskAlertTime) {
		this.longTaskAlertTime = longTaskAlertTime;
	}

}
