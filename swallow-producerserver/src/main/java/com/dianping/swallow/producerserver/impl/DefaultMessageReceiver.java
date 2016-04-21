package com.dianping.swallow.producerserver.impl;

import java.util.Date;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.NameCheckUtil;
import com.dianping.swallow.common.internal.util.SHAUtil;
import com.dianping.swallow.common.internal.whitelist.TopicWhiteList;
import com.dianping.swallow.common.server.monitor.collector.ProducerCollector;
import com.dianping.swallow.producerserver.MessageReceiver;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年10月30日 下午1:56:30
 */
public class DefaultMessageReceiver implements MessageReceiver {


    protected final Logger logger = LogManager.getLogger(getClass());

    private TopicWhiteList topicWhiteList;

    private MessageDAO<?> messageDao;

    private ProducerCollector producerCollector;

    @Override
    public void receiveMessage(String topicName, String sourceDomain, SwallowMessage swallowMessage) {


        if (sourceDomain == null) {
            sourceDomain = "UnknownDomain";
        }

        Transaction producerTransaction = Cat.newTransaction("In:" + topicName, sourceDomain + ":" + swallowMessage.getSourceIp());

        try {
            Date generateTime = swallowMessage.getGeneratedTime() != null ? swallowMessage.getGeneratedTime() : new Date();

            String sha1 = SHAUtil.generateSHA(swallowMessage.getContent());
            swallowMessage.setSha1(sha1);

            messageDao.saveMessage(topicName, swallowMessage);

            producerCollector.addMessage(topicName, swallowMessage.getSourceIp(), 0, swallowMessage.size(), generateTime.getTime(), System.currentTimeMillis());

            producerTransaction.setStatus(Message.SUCCESS);
        } catch (RuntimeException e) {
            producerTransaction.setStatus(e);
            throw e;
        } finally {
            producerTransaction.complete();
        }

    }

    @Override
    public VALID_STATUS isTopicNameValid(String topicName) {

        if (!NameCheckUtil.isTopicNameValid(topicName)) {
            return VALID_STATUS.TOPIC_NAME_INVALID;
        }

        if (!topicWhiteList.isValid(topicName)) {
            return VALID_STATUS.TOPIC_NAME_NOT_IN_WHITELIST;
        }

        return VALID_STATUS.SUCCESS;
    }


    public TopicWhiteList getTopicWhiteList() {
        return topicWhiteList;
    }

    public void setTopicWhiteList(TopicWhiteList topicWhiteList) {
        this.topicWhiteList = topicWhiteList;
    }

    public MessageDAO<?> getMessageDao() {
        return messageDao;
    }

    public void setMessageDao(MessageDAO<?> messageDao) {
        this.messageDao = messageDao;
    }

    public ProducerCollector getProducerCollector() {
        return producerCollector;
    }

    public void setProducerCollector(ProducerCollector producerCollector) {
        this.producerCollector = producerCollector;
    }

}
