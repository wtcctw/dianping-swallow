package com.dianping.swallow.consumer.internal.task;

import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.processor.ConsumerProcessor;
import com.dianping.swallow.common.internal.util.CatUtil;
import com.dianping.swallow.consumer.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qi.yin
 *         2015/12/22  上午10:02.
 */
public abstract class AbstractConsumerTask implements ConsumerTask {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private SwallowCatActionWrapper actionWrapper;
    protected Consumer consumer;
    private TaskChecker taskChecker;
    protected SwallowMessage swallowMessage;
    private ConsumerProcessor consumerProcessor;
    protected String catNameStr;

    public AbstractConsumerTask(Consumer consumer, SwallowCatActionWrapper actionWrapper, TaskChecker taskChecker, SwallowMessage message, ConsumerProcessor consumerProcessor) {
        this.consumer = consumer;
        this.actionWrapper = actionWrapper;
        this.taskChecker = taskChecker;
        this.swallowMessage = message;
        this.consumerProcessor = consumerProcessor;
    }

    @Override
    public void doConsumerTask() {
        Long messageId = swallowMessage.getMessageId();
        Transaction consumerClientTransaction = createConsumerClientTransaction(swallowMessage);

        try {

            beginTask();
            consumerProcessor.beforeOnMessage(swallowMessage);
            if (logger.isInfoEnabled()) {
                logger.info("[run][begin]" + messageId);
            }
            actionWrapper.doAction(consumerClientTransaction, new SwallowAction() {
                @Override
                public void doAction() throws SwallowException {
                    consumer.getListener().onMessage(swallowMessage);
                }
            });

        } catch (SwallowException e) {
            logger.error("[run][can not process message]" + swallowMessage, e);
            CatUtil.logException(e);
            consumerClientTransaction.setStatus(e);
        } finally {

            endTask();
            try {
                consumerProcessor.afterOnMessage(swallowMessage);
            } catch (SwallowException e1) {
                logger.error("[message process exception]" + swallowMessage, e1);
            }
            sendAck(swallowMessage.getMessageId());
            consumerClientTransaction.complete();

            if (logger.isInfoEnabled()) {
                logger.info("[run][end]" + messageId);
            }
        }
    }

    private void beginTask() {
        try {
            taskChecker.addTask(this);
        } catch (Throwable th) {
            logger.error("[beginTask]", th);
        }
    }

    private void endTask() {
        try {
            taskChecker.removeTask(this);
        } catch (Throwable th) {
            logger.error("[endTask]", th);
        }
    }

    @Override
    public String toString() {
        return catNameStr + "," + swallowMessage.getMessageId();
    }

    protected abstract Transaction createConsumerClientTransaction(SwallowMessage swallowMessage);

    protected abstract void sendAck(Long messageId);

}
