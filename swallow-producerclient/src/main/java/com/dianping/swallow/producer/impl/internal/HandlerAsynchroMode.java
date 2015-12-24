package com.dianping.swallow.producer.impl.internal;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.swallow.common.internal.packet.Packet;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.packet.PktSwallowPACK;
import com.dianping.swallow.common.internal.producer.ProducerSwallowService;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.producer.ProducerHandler;
import com.geekhua.filequeue.FileQueue;
import com.geekhua.filequeue.exception.FileQueueClosedException;

/**
 * Producer的异步模式消息处理类
 * 
 * @author tong.song
 */
public class HandlerAsynchroMode implements ProducerHandler {
    private static final Logger          logger            = LoggerFactory.getLogger(HandlerAsynchroMode.class);
    private static final MQThreadFactory THREAD_FACTORY    = new MQThreadFactory();                             //从FileQueue中获取消息的线程池

    private static final int             DELAY_BASE_MULTI  = 5;                                                 //超时策略倍数

    //用于 Cat 打点的type
    private static final String          FILE_QUEUE_FAILED = "FileQueueFailed";
    private static final String          MSG_ASYNC_FAILED  = "MsgAsyncFailed";
    private static final String          MSG_PRODUCE_TRIED = "MsgProduceTried";

    private final ProducerImpl           producer;
    private final FileQueue<Packet>      messageQueue;                                                          //Filequeue
    private final int                    delayBase;                                                             //超时策略基数
    private final int                    fileQueueFailedBaseInterval;                                           //filequeue失败重试的策略基数

    private Thread[]                     asyncThreads;
    private volatile boolean             closed            = false;

    //构造函数
    public HandlerAsynchroMode(ProducerImpl producer) {
        this.producer = producer;
        delayBase = producer.getRetryBaseInterval();
        fileQueueFailedBaseInterval = producer.getFileQueueFailedBaseInterval();
        messageQueue = FileQueueHolder.getQueue(producer.getDestination().getName(), producer.getProducerConfig()
                .isSendMsgLeftLastSession(), producer.getProducerConfig().getFilequeueBaseDir());
        this.start();
    }

    /**
     * 异步处理只需将pkt放入filequeue即可，放入失败抛出异常
     */
    @Override
    public Packet doSendMsg(Packet pkt) throws SendFailedException {
        try {
            messageQueue.add(pkt);
        } catch (FileQueueClosedException e) {
            throw new SendFailedException("Add message to filequeue failed.", e);
        } catch (IOException e) {
            throw new SendFailedException("Add message to filequeue failed.", e);
        }
        return null;
    }

    //启动后台线程(发送消息的线程，监听关闭的线程)
    private void start() {
        //启动线程，并且把线程放到asyncThreads数组

        int threadPoolSize = producer.getProducerConfig().getThreadPoolSize();

        asyncThreads = new Thread[threadPoolSize];

        for (int i = 0; i < threadPoolSize; i++) {
            Thread t = THREAD_FACTORY.newThread(new TskGetAndSend(), "swallow-AsyncProducer-");
            t.setDaemon(true);
            t.start();
            asyncThreads[i] = t;
        }

        //监听关闭事件
        startShutdownook();
    }

    private void startShutdownook() {
        //启动close Monitor
        Thread hook = new Thread() {
            @Override
            public void run() {
                try {
                    logger.info("Swallow async producer stoping...");
                    closed = true;
                    if (asyncThreads != null) {
                        for (Thread asyncThread : asyncThreads) {
                            asyncThread.join(100);//稍微等待线程执行
                        }
                    }
                    if (asyncThreads != null) {
                        for (Thread asyncThread : asyncThreads) {
                            asyncThread.interrupt();//中断线程
                        }
                    }
                    if (asyncThreads != null) {
                        for (Thread asyncThread : asyncThreads) {
                            asyncThread.join();//确保线程执行完毕
                        }
                    }
                    logger.info("Swallow async producer stoped.");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        hook.setDaemon(true);
        hook.setName("Swallow-ShutdownHook-" + this.producer.getDestination().getName());
        Runtime.getRuntime().addShutdownHook(hook);
    }

    //从filequeue队列获取并发送Message
    private class TskGetAndSend implements Runnable {

        private final int              sendTimes      = producer.getProducerConfig().getAsyncRetryTimes() == Integer.MAX_VALUE ? Integer.MAX_VALUE
                                                              : producer.getProducerConfig().getAsyncRetryTimes() + 1;
        private int                    leftRetryTimes = sendTimes;
        private Packet                 message        = null;
        private ProducerSwallowService remoteService  = producer.getRemoteService();

        @Override
        public void run() {
            //异步模式下，每个线程单独有一个延时策略，以保证不同的线程不会互相冲突
            DefaultPullStrategy defaultPullStrategy = new DefaultPullStrategy(delayBase, DELAY_BASE_MULTI * delayBase);
            DefaultPullStrategy fileQueueStrategy = new DefaultPullStrategy(fileQueueFailedBaseInterval,
                    DELAY_BASE_MULTI * fileQueueFailedBaseInterval);

            Packet pktRet = null;

            while (!closed) {

                defaultPullStrategy.succeess();//重置延时

                try {
                    //将自己设置为CatEventID的子节点
                    MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
                    tree.setMessageId(((PktMessage) message).getCatEventID());
                } catch (Exception e) {
                }

                Transaction produceTransaction;

                try {
                    //从filequeue获取message，如果filequeue无元素则阻塞
                    message = messageQueue.get();

                    fileQueueStrategy.succeess();

                    produceTransaction = Cat.getProducer().newTransaction(MSG_PRODUCE_TRIED,
                            producer.getDestination().getName() + ":" + producer.getProducerIP());
                } catch (InterruptedException e) {
                	logger.error("[run]", e);
                    Thread.currentThread().interrupt();
                    continue;
                } catch (Exception e) {
                    Transaction fileQueueTransaction = Cat.getProducer().newTransaction(FILE_QUEUE_FAILED,
                            producer.getDestination().getName() + ":" + producer.getProducerIP());
                    fileQueueTransaction.setStatus(e);
                    Cat.getProducer().logError(e);
                    fileQueueTransaction.complete();

                    produceTransaction = Cat.getProducer().newTransaction(MSG_PRODUCE_TRIED,
                            producer.getDestination().getName() + ":" + producer.getProducerIP());
                    produceTransaction.setStatus(e);
                    Cat.getProducer().logError(e);
                    produceTransaction.complete();
                    logger.error("[run][Can not get msg from fileQueue].", e);

                    fileQueueStrategy.fail(true);

                    continue;
                }

                //发送message，重试次数从Producer获取
                for (leftRetryTimes = sendTimes; leftRetryTimes > 0;) {
                    leftRetryTimes--;
                    try {
                        pktRet = remoteService.sendMessage(message);

                        produceTransaction.addData("sha1", ((PktSwallowPACK) pktRet).getShaInfo());
                        produceTransaction.setStatus(Message.SUCCESS);
                    } catch (Exception e) {
                        //如果剩余重试次数>0且未关闭，则重试
                    	logger.error("[run][send message exception]", e);
                        if (leftRetryTimes > 0 && !closed) {
                            Transaction retryTransaction = Cat.getProducer().newTransaction(MSG_PRODUCE_TRIED,
                                    producer.getDestination().getName() + ":" + producer.getProducerIP());

                            defaultPullStrategy.fail(true);

                            int retryCount = sendTimes - leftRetryTimes;
                            retryTransaction.addData("Retry", retryCount);
                            retryTransaction.setStatus(e);
                            retryTransaction.complete();
                            logger.warn("Retry sending message(cause '" + e.getMessage() + "') " + retryCount
                                    + " times:" + message.toString());
                            //发送失败，重发
                            continue;
                        }
                        Transaction failedTransaction = Cat.getProducer().newTransaction(MSG_ASYNC_FAILED,
                                producer.getDestination().getName() + ":" + producer.getProducerIP());
                        
                        String content = ((PktMessage) message).getContent().toKeyValuePairs();
                        failedTransaction.addData("content", content);
                        logger.warn("[run][fail message]" + content);
                        
                        failedTransaction.setStatus(Message.SUCCESS);
                        failedTransaction.complete();

                        produceTransaction.setStatus(e);
                        Cat.getProducer().logError(e);
                        logger.error("Message sent failed: " + message.toString(), e);
                    }

                    //如果发送成功或失败，不能重试了，则跳出循环
                    break;
                }

                produceTransaction.complete();
            }
        }
    }
}
