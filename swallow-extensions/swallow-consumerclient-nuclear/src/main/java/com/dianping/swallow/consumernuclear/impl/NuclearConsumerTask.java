package com.dianping.swallow.consumernuclear.impl;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.BytesSwallowMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.internal.task.ConsumerTask;
import com.dianping.swallow.consumer.internal.task.TaskChecker;
import com.meituan.nuclearmq.client.ConsumerCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qi.yin
 *         2015/12/14  下午5:28.
 */
public class NuclearConsumerTask implements ConsumerCallback, ConsumerTask {

    private static final Logger logger = LoggerFactory.getLogger(NuclearConsumerTask.class);

    private Consumer consumer;

    private SwallowCatActionWrapper actionWrapper;

    private String catNameStr;

    private TaskChecker taskChecker;

    public NuclearConsumerTask(Consumer consumer, SwallowCatActionWrapper actionWrapper, TaskChecker taskChecker) {
        this.consumer = consumer;
        this.actionWrapper = actionWrapper;
        this.taskChecker = taskChecker;

        catNameStr = this.consumer.getDest().getName() + ":" + this.consumer.getConsumerId() + ":" + IPUtil.getFirstNoLoopbackIP4Address();
    }

    @Override
    public void onHandle(long messageId, byte[] bytes, Object o) {

        final BytesSwallowMessage message = new BytesSwallowMessage();
        message.setBytesContent(bytes);
        message.setMessageId(messageId);

        Transaction consumerClientTransaction = createConsumerClientTransaction(messageId);

        try {

            if (logger.isInfoEnabled()) {
                logger.info("[onHandle][begin]" + messageId);
            }
            taskChecker.addTask(this);

            actionWrapper.doAction(consumerClientTransaction, new SwallowAction() {
                @Override
                public void doAction() throws SwallowException {
                    consumer.getListener().onMessage(message);
                }
            });

        } finally {

            taskChecker.removeTask(this);

            if (logger.isInfoEnabled()) {
                logger.info("[onHandle][end]" + messageId);
            }

            consumerClientTransaction.complete();


        }

    }

    private Transaction createConsumerClientTransaction(long messageId) {

        Transaction transaction = Cat.getProducer().newTransaction("NuclearMQ.MsgConsumed", catNameStr);

        transaction.addData("mid", messageId);

        return transaction;
    }
}

