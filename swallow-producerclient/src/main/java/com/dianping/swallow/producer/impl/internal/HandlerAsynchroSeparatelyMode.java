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
 * @author kezhu.wu
 */
public class HandlerAsynchroSeparatelyMode implements ProducerHandler {
    private static final Logger                   LOGGER                 = LoggerFactory
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
    private static final String                   MSG_PRODUCE            = "MsgProduce-Separately";                              //用于(1)(2)打点
    private static final String                   MSG_PRODUCE_RETRY      = "MsgProduceRetry-Separately";                         //用于(5)(6)打点
    private static final String                   FILE_QUEUE_GET_FAILED  = "FileQueueGetFailed-Separately";                      //用于(3)打点
    private static final String                   FILE_QUEUE_ADD_FAILED  = "FileQueueAddFailed-Separately";                      //用于(4)打点

    //从FileQueue中获取消息，并且发送消息的线程的工厂类
    private static final MQThreadFactory          THREAD_FACTORY         = new MQThreadFactory();

    //默认的filequeue切片大小，512MB
    private static final int                      MSG_AVG_LEN            = 512;
    private static final long                     DEFAULT_FILEQUEUE_SIZE = 100 * 1024 * 1024;

    //超时策略倍数
    private static final int                      DELAY_BASE_MULTI       = 5;

    private static Map<String, FileQueue<Packet>> messageQueues          = new HashMap<String, FileQueue<Packet>>();             //当前TopicName与Filequeue对应关系的集合
    private static Map<String, FileQueue<Packet>> failedMessageQueues    = new HashMap<String, FileQueue<Packet>>();             //当前TopicName与Filequeue对应关系的集合

    private final ProducerImpl                    producer;
    private final FileQueue<Packet>               messageQueue;                                                                  //Filequeue
    private final FileQueue<Packet>               failedMessageQueue;                                                            //存放失败的消息的Filequeue

    private final int                             failedBaseInterval;                                                            //发送抛出异常后，重新获取消息的间隔时间策略基数
    private final int                             retryBaseInterval;                                                             //超时策略基数
    private final int                             fileQueueFailedBaseInterval;                                                   //超时策略基数

    /**
     * 获取指定topicName及选项的FileQueue，如果已经存在则返回引用，如果不存在就创建新的FileQueue
     * 
     * @param topicName 消息目的地名称
     * @param sendMsgLeftLastSessions 是否重启续传
     * @param messageQueuesHolder
     * @return 指定参数的FileQueue
     */
    private synchronized static FileQueue<Packet> getMessageQueue(String topicName, boolean sendMsgLeftLastSessions,
                                                                  String filequeueBaseDir,
                                                                  Map<String, FileQueue<Packet>> messageQueuesHolder,
                                                                  boolean retry) {
        if (retry) {
            topicName = topicName + "#retry";//topicName不能有#号，故此处选择#号
        }
        //如果Map里已经存在该filequeue，在要求“不续传”的情况下， 忽略该请求
        if (messageQueuesHolder.containsKey(topicName)) {
            return messageQueuesHolder.get(topicName);
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
        messageQueuesHolder.put(topicName, newQueue);
        return messageQueuesHolder.get(topicName);
    }

    public HandlerAsynchroSeparatelyMode(ProducerImpl producer) {
        this.producer = producer;
        this.retryBaseInterval = producer.getRetryBaseInterval();
        this.failedBaseInterval = producer.getFailedBaseInterval();
        this.fileQueueFailedBaseInterval = producer.getFileQueueFailedBaseInterval();
        this.messageQueue = getMessageQueue(producer.getDestination().getName(), producer.getProducerConfig()
                .isSendMsgLeftLastSession(), producer.getProducerConfig().getFilequeueBaseDir(), messageQueues, false);
        this.failedMessageQueue = getMessageQueue(producer.getDestination().getName(), producer.getProducerConfig()
                .isSendMsgLeftLastSession(), producer.getProducerConfig().getFilequeueBaseDir(), failedMessageQueues,
                true);
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
        for (int idx = 0; idx < threadPoolSize; idx++) {
            DefaultPullStrategy fileQueueStrategy = new DefaultPullStrategy(fileQueueFailedBaseInterval,
                    DELAY_BASE_MULTI * fileQueueFailedBaseInterval);
            DefaultPullStrategy failIntervalStrategy = new DefaultPullStrategy(failedBaseInterval, DELAY_BASE_MULTI
                    * failedBaseInterval);
            Thread t = THREAD_FACTORY.newThread(new MsgProduceTask(MSG_PRODUCE, messageQueue, fileQueueStrategy,
                    failIntervalStrategy), "swallow-AsyncSeparatelyProducer-");
            t.setDaemon(true);
            t.start();
        }
        //启动针对failedMessageQueue的一个线程
        DefaultPullStrategy fileQueueStrategy = new DefaultPullStrategy(fileQueueFailedBaseInterval, DELAY_BASE_MULTI
                * fileQueueFailedBaseInterval);
        DefaultPullStrategy retryIntervalStrategy = new DefaultPullStrategy(retryBaseInterval, DELAY_BASE_MULTI
                * retryBaseInterval);
        Thread t = THREAD_FACTORY.newThread(new MsgProduceTask(MSG_PRODUCE_RETRY, failedMessageQueue,
                fileQueueStrategy, retryIntervalStrategy), "swallow-AsyncSeparatelyProducer-Retry-");
        t.setDaemon(true);
        t.start();
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
            Packet ack = null;

            while (!Thread.interrupted()) {
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

                } catch (Exception e) {
                    //file queue get 失败的打点
                    Transaction fileQueueGetFailedTransaction = Cat.getProducer().newTransaction(FILE_QUEUE_GET_FAILED,
                            producer.getDestination().getName() + ":" + producer.getProducerIP());
                    fileQueueGetFailedTransaction.setStatus(e);
                    Cat.getProducer().logError(e);
                    fileQueueGetFailedTransaction.complete();

                    LOGGER.error("Can not get msg from fileQueue, retry to get msg...", e);

                    try {
                        fileQueueStrategy.fail(true);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }

                    continue;
                }

                //(2) 发送message
                Transaction msgProduceTransaction = Cat.getProducer().newTransaction(msgProduceCatType,
                        producer.getDestination().getName() + ":" + producer.getProducerIP());
                try {
                    ack = remoteService.sendMessage(message);

                    intervalStrategy.succeess();

                    msgProduceTransaction.addData("sha1", ((PktSwallowPACK) ack).getShaInfo());
                    msgProduceTransaction.setStatus(Message.SUCCESS);
                } catch (Exception e) {
                    try {
                        failedMessageQueue.add(message);

                        msgProduceTransaction.setStatus(e);
                        Cat.getProducer().logError(e);
                        msgProduceTransaction.addData("content", ((PktMessage) message).getContent().toKeyValuePairs());

                        LOGGER.error("Message sent failed, this message will be retryed in a separately FileQueue: "
                                + message.toString(), e);

                    } catch (Exception e1) {
                        //file queue add 失败的打点
                        Transaction fileQueueAddFailedTransaction = Cat.getProducer().newTransaction(
                                FILE_QUEUE_ADD_FAILED,
                                producer.getDestination().getName() + ":" + producer.getProducerIP());
                        fileQueueAddFailedTransaction.setStatus(e1);
                        Cat.getProducer().logError(e1);
                        fileQueueAddFailedTransaction.addData("content", ((PktMessage) message).getContent()
                                .toKeyValuePairs());
                        fileQueueAddFailedTransaction.complete();

                        LOGGER.error("Message add to FileQueue failed, this message is skiped: " + message.toString(),
                                e1);
                    }

                    try {
                        intervalStrategy.fail(true);
                    } catch (InterruptedException e1) {
                        Thread.currentThread().interrupt();
                    }

                } finally {
                    msgProduceTransaction.complete();
                }
            }
        }
    }

}
