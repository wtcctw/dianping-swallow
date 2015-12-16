package com.dianping.swallow.consumernuclear.impl;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.BytesSwallowMessage;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.ByteObjectUtil;
import com.dianping.swallow.common.internal.util.CatUtil;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.message.BytesMessage;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.message.Message;
import com.dianping.swallow.consumer.BackoutMessageException;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.MessageListener;
import com.meituan.nuclearmq.client.ConsumerCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qi.yin
 *         2015/12/14  下午5:28.
 */
public class NuclearMessageListener implements ConsumerCallback {

    private static final Logger logger = LoggerFactory.getLogger(NuclearMessageListener.class);

    private Consumer consumer;

    private SwallowCatActionWrapper actionWrapper;

    private String catNameStr;

    public NuclearMessageListener(Consumer consumer, SwallowCatActionWrapper actionWrapper) {
        this.consumer = consumer;
        this.actionWrapper = actionWrapper;

        catNameStr = this.consumer.getDest().getName() + ":" + this.consumer.getConsumerId() + ":" + IPUtil.getFirstNoLoopbackIP4Address();
    }

    @Override
    public void onHandle(long l, byte[] bytes, Object o) {

        final BytesSwallowMessage message = new BytesSwallowMessage();
        message.setBytesContent(bytes);

        if (logger.isInfoEnabled()) {
            logger.info("[onHandle][begin]" + l);
        }

        Transaction consumerClientTransaction = createConsumerClientTransaction(l);

        try {

            actionWrapper.doAction(consumerClientTransaction, new SwallowAction() {
                @Override
                public void doAction() throws SwallowException {
                    consumer.getListener().onMessage(message);
                }
            });

        }finally {

            if (logger.isInfoEnabled()) {
                logger.info("[onHandle][end]" + l);
            }

            consumerClientTransaction.complete();
        }

    }

    private Transaction createConsumerClientTransaction(long messageId) {

        Transaction transaction = Cat.getProducer().newTransaction("Nuclear_MsgConsumed", catNameStr);

        transaction.addData("mid", messageId);

        return transaction;
    }
}

