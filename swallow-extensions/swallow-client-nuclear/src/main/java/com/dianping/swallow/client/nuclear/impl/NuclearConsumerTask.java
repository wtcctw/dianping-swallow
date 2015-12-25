package com.dianping.swallow.client.nuclear.impl;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.message.BytesSwallowMessage;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.processor.ConsumerProcessor;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.internal.task.AbstractConsumerTask;
import com.dianping.swallow.consumer.internal.task.TaskChecker;

/**
 * @author qi.yin
 *         2015/12/22  上午10:55.
 */
public class NuclearConsumerTask extends AbstractConsumerTask {

    public NuclearConsumerTask(Consumer consumer, SwallowCatActionWrapper actionWrapper, TaskChecker taskChecker, BytesSwallowMessage swallowMessage, ConsumerProcessor consumerProcessor) {

        super(consumer,actionWrapper,taskChecker,swallowMessage,consumerProcessor);

        catNameStr = this.consumer.getDest().getName() + ":" + this.consumer.getConsumerId() + ":" + IPUtil.getFirstNoLoopbackIP4Address();
    }

    @Override
    protected Transaction createConsumerClientTransaction(SwallowMessage swallowMessage) {
        Transaction transaction = Cat.getProducer().newTransaction("NuclearMQ.MsgConsumed", catNameStr);

        transaction.addData("mid", swallowMessage.getMessageId());

        return transaction;
    }

    @Override
    protected void sendAck(Long messageId) {
        //不需要ack，nuclearmq本身有ack。
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
