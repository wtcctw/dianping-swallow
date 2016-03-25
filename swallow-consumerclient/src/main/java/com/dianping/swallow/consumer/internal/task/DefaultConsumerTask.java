package com.dianping.swallow.consumer.internal.task;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.processor.ConsumerProcessor;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.consumer.Consumer;
import com.dianping.swallow.consumer.internal.ConsumerImpl;
import io.netty.channel.ChannelHandlerContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年3月30日 下午4:38:46
 */
public class DefaultConsumerTask extends AbstractConsumerTask implements Runnable {

    private final Logger logger = LogManager.getLogger(getClass());

    private final ChannelHandlerContext ctx;
    private final String connectionDesc;

    public DefaultConsumerTask(ChannelHandlerContext ctx, PktMessage message, Consumer consumer, ConsumerProcessor consumerProcessor,
                               SwallowCatActionWrapper actionWrapper, TaskChecker taskChecker) {
        super(consumer, actionWrapper, taskChecker, message.getContent(), consumerProcessor);
        this.ctx = ctx;
        this.catNameStr = consumer.getDest().getName() + ":" + consumer.getConsumerId() + ":" + IPUtil.getStrAddress(ctx.channel().localAddress());
        this.connectionDesc = IPUtil.getConnectionDesc(ctx.channel());
    }

    @Override
    public void run() {

        Long messageId = swallowMessage.getMessageId();
        if (logger.isDebugEnabled()) {
            logger.debug("[run][task begin]" + connectionDesc + "," + messageId);
        }

        doConsumerTask();
    }

    protected Transaction createConsumerClientTransaction(SwallowMessage swallowMessage) {

        Transaction transaction = Cat.getProducer().newTransaction("MsgConsumed", catNameStr);

        transaction.addData("mid", swallowMessage.getMessageId());
        transaction.addData("sha1", swallowMessage.getSha1());

        if (swallowMessage.getGeneratedTime() != null) {//监控延迟时间
            transaction.addData("delaytime", System.currentTimeMillis() - swallowMessage.getGeneratedTime().getTime());
        }
        return transaction;
    }

    protected void sendAck(Long messageId) {
        try {
            if (logger.isDebugEnabled()) {
                logger.debug("[run][send ack]" + connectionDesc + "," + messageId);
            }
            PktConsumerMessage consumermessage = new PktConsumerMessage(messageId, consumer.isClosed());
            ctx.channel().writeAndFlush(consumermessage);

        } catch (RuntimeException e) {
            logger.warn("[sendAck][Write to server error]" + connectionDesc, e);
        }
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
