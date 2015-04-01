package com.dianping.swallow.consumer.internal.task;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.MessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.action.SwallowCatActionWrapper;
import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.packet.PktConsumerMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.processor.ConsumerProcessor;
import com.dianping.swallow.common.internal.util.CatUtil;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.consumer.Consumer;

/**
 * @author mengwenchao
 *
 * 2015年3月30日 下午4:38:46
 */
public class DefaultConsumerTask implements ConsumerTask{

    private final Logger logger = LoggerFactory.getLogger(getClass());

	@SuppressWarnings("unused")
	private final ChannelHandlerContext 	ctx;
	private final MessageEvent 				e;
	private final String 					catNameStr;
	private final String 					connectionDesc;
	private final ConsumerProcessor 		consumerProcessor;
	private final SwallowCatActionWrapper  	actionWrapper;
	private final Consumer 					consumer;
	private final TaskChecker				taskChecker;
	private final SwallowMessage 			swallowMessage;
	
	public DefaultConsumerTask(ChannelHandlerContext ctx, MessageEvent e, Consumer consumer, ConsumerProcessor consumerProcessor, 
			SwallowCatActionWrapper actionWrapper, TaskChecker taskChecker){
		
		this.ctx = ctx;
		this.e = e;
        this.catNameStr = consumer.getDest().getName() + ":" + consumer.getConsumerId() +  ":" + IPUtil.getStrAddress(e.getChannel().getLocalAddress());
        this.connectionDesc = IPUtil.getConnectionDesc(e);
        this.consumer = consumer;
        this.consumerProcessor  = consumerProcessor;
        this.actionWrapper = actionWrapper;
        this.taskChecker = taskChecker;
        this.swallowMessage = ((PktMessage) e.getMessage()).getContent();
	}
	
	@Override
	public void run() {
		
        Long messageId = swallowMessage.getMessageId();
		Transaction consumerClientTransaction = createConsumerClientTransaction(swallowMessage);
        if (logger.isDebugEnabled()) {
            logger.debug("[run][task begin]" + connectionDesc + "," + messageId);
        }
        try {
        	
        	beginTask();
        	consumerProcessor.beforeOnMessage(swallowMessage);
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
        } finally{
        	
        	endTask();
        	try {
				consumerProcessor.afterOnMessage(swallowMessage);
			} catch (SwallowException e1) {
				logger.error("[message process exception]" +  swallowMessage, e1);
			}
        	sendAck(e, swallowMessage.getMessageId());
            consumerClientTransaction.complete();
        }
	}
	private void beginTask() {
		try{
			taskChecker.addTask(this);
		}catch(Throwable th){
			logger.error("[beginTask]", th);
		}
	}

	private void endTask() {
		try{
			taskChecker.removeTask(this);
		}catch(Throwable th){
			logger.error("[endTask]", th);
		}
	}
	
	private Transaction createConsumerClientTransaction(SwallowMessage swallowMessage) {
		
		Transaction transaction = Cat.getProducer().newTransaction("MsgConsumed", catNameStr);
		
		transaction.addData("mid", swallowMessage.getMessageId());
		transaction.addData("sha1", swallowMessage.getSha1());
		
        if(swallowMessage.getGeneratedTime() != null){//监控延迟时间
            transaction.addData("delaytime", System.currentTimeMillis() - swallowMessage.getGeneratedTime().getTime());
        }
        return transaction;
	}
    
	private void sendAck(MessageEvent event, Long messageId) {
        try {
        	if(logger.isDebugEnabled()){
        		logger.debug("[run][send ack]" + connectionDesc + "," + messageId);
        	}
            PktConsumerMessage consumermessage = new PktConsumerMessage(messageId, consumer.isClosed());
            event.getChannel().write(consumermessage);
        } catch (RuntimeException e) {
            logger.warn("Write to server error.", e);
        }
	}
	
	@Override
	public String toString() {
		return catNameStr + "," + swallowMessage.getMessageId();
	}
}
