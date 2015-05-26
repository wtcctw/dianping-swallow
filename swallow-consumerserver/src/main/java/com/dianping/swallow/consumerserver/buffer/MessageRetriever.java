package com.dianping.swallow.consumerserver.buffer;

import java.util.List;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.message.SwallowMessage;

public interface MessageRetriever {

   /**
    * 从数据库获取messageId大于tailMessageId的消息，fetchSize可配置<br>
    * <p>
    * 注意：如果返回值messages.size大于0，则第一个元素一定是maxMessageId，
    * maxMessageId是本次访问数据库所获得的批量消息的最大messageId
    * ，用该值来更新tailMessageId，这样下次调用retriveMessage就不会访问数据库里已经访问过的消息。
    * </p>
    * 
    * @param topicName
    * @param consumerId consumerId为null时使用非backup队列
    * @param tailMessageId
    * @param messageFilter 可以为null，如果为null则忽略
    * @return
    * @throws Exception
    */
	ReturnMessageWrapper retrieveMessage(String topicName, String consumerId, Long messageId, MessageFilter messageFilter);
   
	ReturnMessageWrapper retrieveMessage(String topicName, Long messageId, int fetchSize);

	ReturnMessageWrapper retrieveMessage(String topicName, Long messageId);
   
   void setFetchSize(int fetchSize);

   
	public static class ReturnMessageWrapper{
		
		private List<SwallowMessage> messages;
		
		/**
		 * 消息大小，如果是包含有filter，则返回原生的获取消息大小
		 */
		private int rawMessageSize;
		
		private Long maxMessageId;

		public ReturnMessageWrapper(List<SwallowMessage> messages, int rawMessageSize, Long maxMessageId){
			this.messages = messages;
			this.rawMessageSize = rawMessageSize;
			this.maxMessageId = maxMessageId;
		}

		public List<SwallowMessage> getMessages() {
			return messages;
		}

		public int getRawMessageSize() {
			return rawMessageSize;
		}

		public Long getMaxMessageId() {
			return maxMessageId;
		}

	}

}
