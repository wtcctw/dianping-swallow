package com.dianping.swallow.consumerserver.buffer;

import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.consumer.MessageFilter.FilterType;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;

public class MongoDBMessageRetriever implements MessageRetriever {
   private static final Logger LOG       = LoggerFactory.getLogger(MongoDBMessageRetriever.class);

   private int                 fetchSize = 500;

   private MessageDAO          messageDAO;

   public void setMessageDAO(MessageDAO messageDAO) {
      this.messageDAO = messageDAO;
   }

   /**
    * @param topicName
    * @param consumerId consumerId为null时使用非backup队列
    * @param messageId
    * @param messageFilter
    * @return
    */
   @SuppressWarnings({ "rawtypes", "unchecked" })
   @Override
   public List retriveMessage(String topicName, String consumerId, Long messageId, MessageFilter messageFilter) {
      List messages = messageDAO.getMessagesGreaterThan(topicName, consumerId, messageId, fetchSize);

      Long maxMessageId = null;
      if (messages != null && messages.size() > 0) {
         //记录本次返回的最大那条消息的messageId
         SwallowMessage message = (SwallowMessage) messages.get(messages.size() - 1);

         if (message.getBackupMessageId() == null) {//正常消息队列
            maxMessageId = message.getMessageId();
         } else {//备份消息队列
            maxMessageId = message.getBackupMessageId();
         }
         //过滤type
         if (messageFilter != null && messageFilter.getType() == FilterType.InSet && messageFilter.getParam() != null
               && !messageFilter.getParam().isEmpty()) {
            Iterator<SwallowMessage> iterator = messages.iterator();
            while (iterator.hasNext()) {
               SwallowMessage msg = iterator.next();
               if (!messageFilter.getParam().contains(msg.getType())) {
                  iterator.remove();
               }
            }
         }
         //无论最终过滤后messages.size是否大于0，都添加maxMessageId到返回集合
         messages.add(0, maxMessageId);
      }

      if (LOG.isDebugEnabled()) {
         LOG.debug("fetched messages from mongodb, size:" + messages.size());
         LOG.debug("messages:" + messages);
      }

      //如果返回值messages.size大于0，则第一个元素一定是更新后的maxMessageId
      return messages;
   }

   @Override
   public void setFetchSize(int fetchSize) {
      this.fetchSize = fetchSize;
   }

}
