package com.dianping.swallow.consumerserver.buffer;

import java.util.List;

import com.dianping.swallow.common.message.Destination;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.dao.impl.ReturnMessageWrapper;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年3月3日 上午10:02:15
 */
public abstract class AbstractRetrieveTask implements Runnable {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected RetrieveStrategy retrieveStrategy;
    protected Destination dest;
    protected MessageRetriever messageRetriever;
    protected MessageFilter messageFilter;

    public AbstractRetrieveTask(RetrieveStrategy retrieveStrategy, Destination dest, MessageRetriever messageRetriever,
                                MessageFilter messageFilter) {
        this.retrieveStrategy = retrieveStrategy;
        this.dest = dest;
        this.messageRetriever = messageRetriever;
        this.messageFilter = messageFilter;
    }

    @Override
    public void run() {

        synchronized (retrieveStrategy) {
            try {
                retrieveStrategy.beginRetrieve();
                if (retrieveStrategy.isRetrieve() && messageRetriever != null) {
                    retrieveMessage();
                }
            } catch (Throwable th) {
                logger.error("[run]" + getDetail(), th);
            } finally {
                retrieveStrategy.endRetrieve();
            }
        }
    }

    protected void updateRetrieveStrategy(int rawMessageSize, List<SwallowMessage> messages, Long tailId) {

        if (logger.isDebugEnabled() && rawMessageSize > 0) {

            logger.debug(String.format("[updateRetrieveStrategy][read message]%s,%d,%d", getDetail(), tailId, rawMessageSize));

            int messageSize = messages == null ? 0 : messages.size();
            if (messageSize != rawMessageSize) {
                logger.debug("[updateRetrieveStrategy][real message size]" + messageSize);
            }
        }

        retrieveStrategy.retrieved(rawMessageSize);
    }

    protected void  retrieveMessage() {

        if (logger.isDebugEnabled()) {
            logger.debug("[retrieveMessage][tailBackupMessageId]" + getTailId());
        }

        ReturnMessageWrapper messageWrapper = messageRetriever.retrieveMessage(dest.getName(),
                getConsumerId(), getTailId(), messageFilter);

        List<SwallowMessage> messages = messageWrapper.getMessages();

        updateRetrieveStrategy(messageWrapper.getRawMessageSize(), messageWrapper.getMessages(), getTailId());

        putMessage(messages);

        if (messageWrapper.getRawMessageSize() > 0) {
            setTailId(messageWrapper.getMaxMessageId());
        }

        if (logger.isDebugEnabled()) {
            logger.debug("[retrieveMessage][tailBackupMessageId]" + getTailId());
        }
    }

    protected abstract void putMessage(List<SwallowMessage> messages);

    protected abstract void setTailId(Long tailId);

    protected abstract Long getTailId();

    protected abstract String getConsumerId();

    protected abstract String getDetail();

}