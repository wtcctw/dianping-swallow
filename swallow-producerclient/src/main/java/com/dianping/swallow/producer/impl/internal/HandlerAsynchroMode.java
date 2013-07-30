package com.dianping.swallow.producer.impl.internal;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.cat.message.spi.MessageTree;
import com.dianping.lion.EnvZooKeeperConfig;
import com.dianping.swallow.common.internal.packet.Packet;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.packet.PktSwallowPACK;
import com.dianping.swallow.common.internal.producer.ProducerSwallowService;
import com.dianping.swallow.common.internal.threadfactory.DefaultPullStrategy;
import com.dianping.swallow.common.internal.threadfactory.MQThreadFactory;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.producer.ProducerHandler;
import com.geekhua.filequeue.Config;
import com.geekhua.filequeue.FileQueue;
import com.geekhua.filequeue.FileQueueImpl;
import com.geekhua.filequeue.exception.FileQueueClosedException;

/**
 * Producer的异步模式消息处理类
 * 
 * @author tong.song
 */
public class HandlerAsynchroMode implements ProducerHandler {
    private static final Logger                   LOGGER                 = LoggerFactory
                                                                                 .getLogger(HandlerAsynchroMode.class);
    private static final MQThreadFactory          THREAD_FACTORY         = new MQThreadFactory();                      //从FileQueue中获取消息的线程池

    private static final long                     DEFAULT_FILEQUEUE_SIZE = 100 * 1024 * 1024;                          //默认的filequeue切片大小，512MB
    private static final int                      DELAY_BASE_MULTI       = 5;                                          //超时策略倍数
    private static final int                      MSG_AVG_LEN            = 512;

    //用于 Cat 打点的type
    private static final String                   FILE_QUEUE_FAILED      = "FileQueueFailed";
    private static final String                   MSG_ASYNC_FAILED       = "MsgAsyncFailed";
    private static final String                   MSG_PRODUCE_TRIED      = "MsgProduceTried";

    private static Map<String, FileQueue<Packet>> messageQueues          = new HashMap<String, FileQueue<Packet>>();   //当前TopicName与Filequeue对应关系的集合

    private final ProducerImpl                    producer;
    private final FileQueue<Packet>               messageQueue;                                                        //Filequeue
    private final int                             delayBase;                                                           //超时策略基数
    private final int                             fileQueueFailedBaseInterval;                                         //filequeue失败重试的策略基数
    private Thread[]                              asyncThreads;
    private volatile boolean                      closed                 = false;

    /**
     * 获取指定topicName及选项的FileQueue，如果已经存在则返回引用，如果不存在就创建新的FileQueue
     * 
     * @param topicName 消息目的地名称
     * @param sendMsgLeftLastSessions 是否重启续传
     * @return 指定参数的FileQueue
     */
    private synchronized static FileQueue<Packet> getMessageQueue(String topicName, boolean sendMsgLeftLastSessions,
                                                                  String filequeueBaseDir) {
        //如果Map里已经存在该filequeue，在要求“不续传”的情况下， 忽略该请求
        if (messageQueues.containsKey(topicName)) {
            return messageQueues.get(topicName);
        }

        Config fileQueueConfig = new Config();
        fileQueueConfig.setName(topicName);
        fileQueueConfig.setFileSiz(DEFAULT_FILEQUEUE_SIZE);
        fileQueueConfig.setMsgAvgLen(MSG_AVG_LEN);
        if (filequeueBaseDir != null) {
            fileQueueConfig.setBaseDir(filequeueBaseDir);
        } else {
            /*
             * 为了避免测试环境filequeue目录公用导致出错的问题，当未自定义设置filequeue的目录，而且是alpha或qa环境时，
             * 则使用带有uuid的目录。但这样重启应用后filequeue目录变了故无法做到续传。
             */
            String env = EnvZooKeeperConfig.getEnv();
            if ("qa".equals(env) || "alpha".equals(env)) {
                String uuid = UUID.randomUUID().toString();
                filequeueBaseDir = "/data/appdatas/filequeue/" + uuid;
                fileQueueConfig.setBaseDir(filequeueBaseDir);
                LOGGER.info("env is '" + env + "' and 'filequeueBaseDir' is not set, so randomize the filequeue dir: "
                        + filequeueBaseDir);
            }
        }
        //如果Map里不存在该filequeue，此handler又要求将之前的文件删除，则删除
        if (!sendMsgLeftLastSessions) {//如果不续传，则需要把/data/appdatas/filequeue/<topicName> 目录删除掉
            File file = new File(fileQueueConfig.getBaseDir(), topicName);
            if (file.exists()) {
                try {
                    FileUtils.deleteDirectory(file);
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        FileQueue<Packet> newQueue = new FileQueueImpl<Packet>(fileQueueConfig);
        messageQueues.put(topicName, newQueue);
        return messageQueues.get(topicName);
    }

    //构造函数
    public HandlerAsynchroMode(ProducerImpl producer) {
        this.producer = producer;
        delayBase = producer.getRetryBaseInterval();
        fileQueueFailedBaseInterval = producer.getFileQueueFailedBaseInterval();
        messageQueue = getMessageQueue(producer.getDestination().getName(), producer.getProducerConfig()
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
                    LOGGER.info("Swallow async producer stoping...");
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
                    LOGGER.info("Swallow async producer stoped.");

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
                    LOGGER.error("Can not get msg from fileQueue.", e);

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
                        if (leftRetryTimes > 0 && !closed) {
                            Transaction retryTransaction = Cat.getProducer().newTransaction(MSG_PRODUCE_TRIED,
                                    producer.getDestination().getName() + ":" + producer.getProducerIP());

                            defaultPullStrategy.fail(true);

                            int retryCount = sendTimes - leftRetryTimes;
                            retryTransaction.addData("Retry", retryCount);
                            retryTransaction.setStatus(e);
                            retryTransaction.complete();
                            LOGGER.warn("Retry sending message(cause '" + e.getMessage() + "') " + retryCount
                                    + " times:" + message.toString());
                            //发送失败，重发
                            continue;
                        }
                        Transaction failedTransaction = Cat.getProducer().newTransaction(MSG_ASYNC_FAILED,
                                producer.getDestination().getName() + ":" + producer.getProducerIP());
                        failedTransaction.addData("content", ((PktMessage) message).getContent().toKeyValuePairs());
                        failedTransaction.setStatus(Message.SUCCESS);
                        failedTransaction.complete();

                        produceTransaction.setStatus(e);
                        Cat.getProducer().logError(e);
                        LOGGER.error("Message sent failed: " + message.toString(), e);
                    }

                    //如果发送成功或失败，不能重试了，则跳出循环
                    break;
                }

                produceTransaction.complete();
            }
        }
    }
}
