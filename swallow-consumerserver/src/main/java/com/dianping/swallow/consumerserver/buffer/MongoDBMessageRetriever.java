package com.dianping.swallow.consumerserver.buffer;

import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.consumer.MessageFilter.FilterType;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.dao.impl.ReturnMessageWrapper;
import com.dianping.swallow.common.internal.message.SwallowMessage;

public class MongoDBMessageRetriever implements MessageRetriever {


    private static final Logger logger = LogManager.getLogger(MongoDBMessageRetriever.class);

    private int fetchSize = 500;

    private MessageDAO<?> messageDAO;

    public void setMessageDAO(MessageDAO<?> messageDAO) {
        this.messageDAO = messageDAO;
    }


    @Override
    public ReturnMessageWrapper retrieveMessage(String topicName, Long messageId, int fetchSize) {
        return retrieveMessage(topicName, null, messageId, null, fetchSize);
    }

    @Override
    public ReturnMessageWrapper retrieveMessage(String topicName, String consumerId, Long messageId, MessageFilter messageFilter) {

        return retrieveMessage(topicName, consumerId, messageId, messageFilter, fetchSize);
    }


    private ReturnMessageWrapper retrieveMessage(String topicName, String consumerId, Long messageId, MessageFilter messageFilter, int fetchSize) {

        ReturnMessageWrapper returnMessageWrapper = messageDAO.getMessagesGreaterThan(topicName, consumerId, messageId, fetchSize);

        final int rawMessageSize = returnMessageWrapper.getRawMessageSize();
        final Long maxMessageId = returnMessageWrapper.getMaxMessageId();

        List<SwallowMessage> messages = returnMessageWrapper.getMessages();
        if (messages != null && messages.size() > 0) {
            //过滤type
            if (MessageFilter.isFilterable(messageFilter)) {
                Iterator<SwallowMessage> iterator = messages.iterator();
                while (iterator.hasNext()) {

                    SwallowMessage msg = iterator.next();
                    if (!messageFilter.getParam().contains(msg.getType())) {
                        iterator.remove();
                    }
                }
            }
        }

        if (logger.isDebugEnabled()) {
            logger.debug("fetched messages from mongodb, size:" + messages.size());
            logger.debug("messages:" + messages);
        }

        return new ReturnMessageWrapper(messages, rawMessageSize, maxMessageId);

    }


    @Override
    public void setFetchSize(int fetchSize) {
        this.fetchSize = fetchSize;
    }


    @Override
    public ReturnMessageWrapper retrieveMessage(String topicName, Long messageId) {
        return retrieveMessage(topicName, messageId, fetchSize);
    }

    @Override
    public Long getBeginFetchIdOfBuffer(String topicName) {
        Long messageId = messageDAO.getMaxMessageId(topicName);
        if (messageId == null) {
            messageId = messageDAO.getMessageEmptyAckId(topicName);
        }
        return messageId;
    }

}
