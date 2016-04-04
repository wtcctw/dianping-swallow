package com.dianping.swallow.consumerserver.buffer.impl;

import java.util.concurrent.ExecutorService;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.consumerserver.buffer.MessageRetriever;
import com.dianping.swallow.consumerserver.buffer.MessageRetrieveTask;
import com.dianping.swallow.consumerserver.buffer.RetrieveStrategy;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年8月17日 下午3:37:55
 */
public final class MessageBlockingQueue extends AbstractClosableBlockingQueue {

    private static final long serialVersionUID = -633276713494338593L;

    public MessageBlockingQueue(ConsumerInfo consumerInfo, int minThreshold, int maxThreshold, int capacity,
                                Long messageIdOfTailMessage, ExecutorService retrieverThreadPool) {
        super(consumerInfo, minThreshold, maxThreshold, capacity, messageIdOfTailMessage,
                retrieverThreadPool);
    }

    @Override
    protected Runnable createMessageRetrieverTask(RetrieveStrategy retrieveStrategy, ConsumerInfo consumerInfo,
                                                  MessageRetriever messageRetriever, AbstractClosableBlockingQueue abstractClosableBlockingQueue,
                                                  MessageFilter messageFilter) {

        return new MessageRetrieveTask(retrieveStrategy, consumerInfo, messageRetriever, this, messageFilter);
    }


}
