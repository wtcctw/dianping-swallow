package com.dianping.swallow.consumerserver.buffer.impl;

import java.util.concurrent.ExecutorService;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.consumerserver.buffer.BackupMessageRetrieveTask;
import com.dianping.swallow.consumerserver.buffer.MessageRetriever;
import com.dianping.swallow.consumerserver.buffer.RetrieveStrategy;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年11月12日 下午7:38:09
 */
public class BackupMessageBlockingQueue extends AbstractClosableBlockingQueue {

    private static final long serialVersionUID = 1L;

    public BackupMessageBlockingQueue(ConsumerInfo consumerInfo, int minThreshold, int maxThreshold, int capacity,
                                      Long messageIdOfTailMessage, ExecutorService retrieverThreadPool) {
        super(consumerInfo, minThreshold, maxThreshold, capacity, messageIdOfTailMessage, retrieverThreadPool);
    }

    @Override
    protected Runnable createMessageRetrieverTask(RetrieveStrategy retrieveStrategy, ConsumerInfo consumerInfo,
                                                  MessageRetriever messageRetriever, AbstractClosableBlockingQueue abstractClosableBlockingQueue,
                                                  MessageFilter messageFilter) {


        return new BackupMessageRetrieveTask(retrieveStrategy, consumerInfo, messageRetriever, this, messageFilter);
    }

}
