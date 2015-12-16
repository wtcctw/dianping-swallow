package com.dianping.swallow.producer.impl.internal;

import java.io.IOException;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

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
 * @author kezhu.wu
 */
public class HandlerAsynchroSeparatelyMode implements ProducerHandler {
    private static final Logger          logger                = LogManager
                                                                       .getLogger(HandlerAsynchroSeparatelyMode.class);

    /**
     * Cat打点需要记录的信息：<br>
     * (1) 正常逻辑里，发送消息成功的次数 <br>
     * (2) 正常逻辑里，发送消息失败，放到重试队列的次数 (打印出消息内容) <br>
     * (3) FileQueue.get()，失败的次数 <br>
     * (4) FileQueue.add()到重试队列，失败的次数 (add失败，则是丢弃消息，打印出消息内容) <br>
     * (5) 重试队列里，重试成功的次数 <br>
     * (6) 重试队列里，重试失败的次数(失败会继续放回重试) <br>
     */
    //cat打点使用的几个名称
    private static final String          MSG_PRODUCE           = "MsgProduce-Separately";                              //用于(1)(2)打点
    private static final String          MSG_PRODUCE_RETRY     = "MsgProduceRetry-Separately";                         //用于(5)(6)打点
    private static final String          FILE_QUEUE_GET_FAILED = "FileQueueGetFailed-Separately";                      //用于(3)打点
    private static final String          FILE_QUEUE_ADD_FAILED = "FileQueueAddFailed-Separately";                      //用于(4)打点

    //从FileQueue中获取消息，并且发送消息的线程的工厂类
    private static final MQThreadFactory THREAD_FACTORY        = new MQThreadFactory();

    //超时策略倍数
    private static final int             DELAY_BASE_MULTI      = 5;

    private final ProducerImpl           producer;
    private final FileQueue<Packet>      messageQueue;                                                                 //Filequeue
    private final FileQueue<Packet>      failedMessageQueue;                                                           //存放失败的消息的Filequeue

    private final int                    failedBaseInterval;                                                           //发送抛出异常后，重新获取消息的间隔时间策略基数
    private final int                    retryBaseInterval;                                                            //超时策略基数
    private final int                    fileQueueFailedBaseInterval;                                                  //超时策略基数

    private Thread[]                     asyncThreads;
    private Thread                       retryThread;
    private volatile boolean             closed                = false;
    
    public static String RETRY_QUEUE_SUFFIX =  "#retry";

    public HandlerAsynchroSeparatelyMode(ProducerImpl producer) {
        this.producer = producer;
        this.retryBaseInterval = producer.getRetryBaseInterval();
        this.failedBaseInterval = producer.getFailedBaseInterval();
        this.fileQueueFailedBaseInterval = producer.getFileQueueFailedBaseInterval();
        this.messageQueue = FileQueueHolder.getQueue(producer.getDestination().getName(), producer.getProducerConfig()
                .isSendMsgLeftLastSession(), producer.getProducerConfig().getFilequeueBaseDir());
        this.failedMessageQueue = FileQueueHolder.getQueue(producer.getDestination().getName() + RETRY_QUEUE_SUFFIX, producer
                .getProducerConfig().isSendMsgLeftLastSession(), producer.getProducerConfig().getFilequeueBaseDir());
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

    //启动处理线程
    private void start() {
        //启动针对messageQueue的线程
        int threadPoolSize = producer.getProducerConfig().getThreadPoolSize();
        asyncThreads = new Thread[threadPoolSize];
        for (int i = 0; i < threadPoolSize; i++) {
            DefaultPullStrategy fileQueueStrategy = new DefaultPullStrategy(fileQueueFailedBaseInterval,
                    DELAY_BASE_MULTI * fileQueueFailedBaseInterval);
            DefaultPullStrategy failIntervalStrategy = new DefaultPullStrategy(failedBaseInterval, DELAY_BASE_MULTI
                    * failedBaseInterval);
            Thread t = THREAD_FACTORY.newThread(new MsgProduceTask(MSG_PRODUCE, messageQueue, fileQueueStrategy,
                    failIntervalStrategy), "swallow-AsyncSeparatelyProducer-");
            t.setDaemon(true);
            t.start();
            asyncThreads[i] = t;
        }

        //启动针对failedMessageQueue的一个线程
        DefaultPullStrategy fileQueueStrategy = new DefaultPullStrategy(fileQueueFailedBaseInterval, DELAY_BASE_MULTI
                * fileQueueFailedBaseInterval);
        DefaultPullStrategy retryIntervalStrategy = new DefaultPullStrategy(retryBaseInterval, DELAY_BASE_MULTI
                * retryBaseInterval);
        retryThread = THREAD_FACTORY.newThread(new MsgProduceTask(MSG_PRODUCE_RETRY, failedMessageQueue,
                fileQueueStrategy, retryIntervalStrategy), "swallow-AsyncSeparatelyProducer-Retry-");
        retryThread.setDaemon(true);
        retryThread.start();

        //监听关闭事件
        startShutdownook();
    }

    private void startShutdownook() {
        //启动close Monitor
        Thread hook = new Thread() {
            @Override
            public void run() {
                try {
                    logger.info("Swallow async(separately) producer stoping...");
                    closed = true;
                    //稍微等待线程执行
                    if (asyncThreads != null) {
                        for (Thread asyncThread : asyncThreads) {
                            asyncThread.join(100);
                        }
                    }
                    if (retryThread != null) {
                        retryThread.join(100);
                    }
                    //中断线程
                    if (asyncThreads != null) {
                        for (Thread asyncThread : asyncThreads) {
                            asyncThread.interrupt();
                        }
                    }
                    if (retryThread != null) {
                        retryThread.interrupt();
                    }
                    //确保线程执行完毕
                    if (asyncThreads != null) {
                        for (Thread asyncThread : asyncThreads) {
                            asyncThread.join();
                        }
                    }
                    if (retryThread != null) {
                        retryThread.join();
                    }

                    logger.info("Swallow async(separately) producer stoped.");

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        };
        hook.setDaemon(true);
        hook.setName("Swallow-ShutdownHook-" + this.producer.getDestination().getName());
        Runtime.getRuntime().addShutdownHook(hook);
    }

    //从failedFilequeue队列获取并发送Message
    private class MsgProduceTask implements Runnable {

        DefaultPullStrategy fileQueueStrategy;

        DefaultPullStrategy intervalStrategy;

        FileQueue<Packet>   queue;

        String              msgProduceCatType;

        public MsgProduceTask(String msgProduceCatType, FileQueue<Packet> queue, DefaultPullStrategy fileQueueStrategy,
                              DefaultPullStrategy intervalStrategy) {
            super();
            this.fileQueueStrategy = fileQueueStrategy;
            this.intervalStrategy = intervalStrategy;
            this.queue = queue;
            this.msgProduceCatType = msgProduceCatType;
        }

        @Override
        public void run() {
            ProducerSwallowService remoteService = producer.getRemoteService();

            Packet message = null;
            Packet pktRet = null;

            while (!closed) {
                try {
                    //将自己设置为CatEventID的子节点
                    MessageTree tree = Cat.getManager().getThreadLocalMessageTree();
                    tree.setMessageId(((PktMessage) message).getCatEventID());
                } catch (Exception e) {
                }

                //(1) 从filequeue获取message
                try {
                    //如果filequeue无元素则阻塞
                    message = queue.get();

                    fileQueueStrategy.succeess();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    continue;
                } catch (Exception e) {
                    //file queue get 失败的打点
                    Transaction fileQueueGetFailedTransaction = Cat.getProducer().newTransaction(FILE_QUEUE_GET_FAILED,
                            producer.getDestination().getName() + ":" + producer.getProducerIP());
                    fileQueueGetFailedTransaction.setStatus(e);
                    Cat.getProducer().logError(e);
                    fileQueueGetFailedTransaction.complete();

                    logger.error("Can not get msg from fileQueue, retry to get msg...", e);

                    fileQueueStrategy.fail(true);

                    continue;
                }

                //(2) 发送message
                Transaction msgProduceTransaction = Cat.getProducer().newTransaction(msgProduceCatType,
                        producer.getDestination().getName() + ":" + producer.getProducerIP());
                try {
                    pktRet = remoteService.sendMessage(message);

                    intervalStrategy.succeess();

                    msgProduceTransaction.addData("sha1", ((PktSwallowPACK) pktRet).getShaInfo());
                    msgProduceTransaction.setStatus(Message.SUCCESS);
                } catch (Exception e) {
                    try {
                        failedMessageQueue.add(message);

                        msgProduceTransaction.setStatus(e);
                        Cat.logError(e);
                        
                        logger.error("Message sent failed, this message will be retryed in a separately FileQueue: " + message.toString(), e);

                    } catch (Exception e1) {
                        //file queue add 失败的打点
                        Transaction fileQueueAddFailedTransaction = Cat.getProducer().newTransaction(
                                FILE_QUEUE_ADD_FAILED,
                                producer.getDestination().getName() + ":" + producer.getProducerIP());
                        fileQueueAddFailedTransaction.setStatus(e1);
                        Cat.logError(e1);
                        
                        String content = ((PktMessage) message).getContent().toKeyValuePairs();
                        fileQueueAddFailedTransaction.addData("content", content);
                        logger.warn("[run][fail message]" + content);
                        
                        
                        fileQueueAddFailedTransaction.complete();

                        logger.error("Message add to FileQueue failed, this message is skiped: " + message.toString(),
                                e1);
                    }

                    intervalStrategy.fail(true);

                } finally {
                    msgProduceTransaction.complete();
                }
            }
        }
    }

}
