package com.dianping.swallow.consumerserver.worker.impl;


import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.consumer.ConsumerType;
import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ACKHandlerType;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.dao.MessageDAO;
import com.dianping.swallow.common.internal.heartbeat.DefaultHeartBeatReceiver;
import com.dianping.swallow.common.internal.heartbeat.HeartBeatReceiver;
import com.dianping.swallow.common.internal.heartbeat.NoHeartBeatListener;
import com.dianping.swallow.common.internal.observer.impl.AbstractObservableLifecycle;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.server.monitor.collector.ConsumerCollector;
import com.dianping.swallow.consumerserver.auth.ConsumerAuthController;
import com.dianping.swallow.consumerserver.buffer.SwallowBuffer;
import com.dianping.swallow.consumerserver.pool.ConsumerThreadPoolManager;
import com.dianping.swallow.consumerserver.util.ConsumerUtil;
import com.dianping.swallow.consumerserver.worker.ConsumerWorker;
import com.dianping.swallow.consumerserver.worker.SendAckManager;

/***
 * 一个ConsumerWorkerImpl负责处理一个(topic,consumerId)的消费者集群，使用单线程获取消息，并顺序推送给Consumer
 *
 * @author kezhu.wu
 */
public final class ConsumerWorkerImpl extends AbstractObservableLifecycle implements ConsumerWorker, NoHeartBeatListener {

    /**
     * 允许"最小的空洞waitAckMessage"存活的时间的阈值,单位秒，默认5分钟
     */
    private ConsumerInfo consumerInfo;

    private MessageFilter messageFilter;


    private ExecutorService ackExecutor;
    private ExecutorService sendMessageExecutor;
    private ConsumerAuthController consumerAuthController;

    private volatile boolean started = false;

    /**
     * 可用来发送消息的channel
     */
    private Queue<Channel> freeChannels = new ConcurrentLinkedQueue<Channel>();

    /**
     * 存放已连接的channel，key是channel，value是ip
     */
    private ConcurrentHashMap<Channel, String> connectedChannels = new ConcurrentHashMap<Channel, String>();

    private ConsumerThreadPoolManager consumerThreadPoolManager;

    private HeartBeatReceiver heartBeatReceiver;

    private ConsumerCollector consumerCollector;

    protected final Logger ackLogger = LogManager.getLogger("ackLogger");

    private MessageDAO<?> messageDao;

    private SwallowBuffer swallowBuffer;

    private long startMessageId;

    private List<SendAckManager> sendAckManagers = new ArrayList<SendAckManager>(2);

    private long sequence;

    @SuppressWarnings("deprecation")
    public ConsumerWorkerImpl(long sequence, ConsumerInfo consumerInfo, ConsumerWorkerManager workerManager,
                              ConsumerAuthController consumerAuthController,
                              ConsumerThreadPoolManager consumerThreadPoolManager, long startMessageId,
                              ConsumerCollector consumerCollector) {
        this.sequence = sequence;
        this.consumerInfo = consumerInfo;
        this.messageDao = workerManager.getMessageDAO();
        this.swallowBuffer = workerManager.getSwallowBuffer();
        this.consumerThreadPoolManager = consumerThreadPoolManager;
        this.consumerAuthController = consumerAuthController;
        this.consumerCollector = consumerCollector;
        this.startMessageId = startMessageId;

        // consumerInfo的type不允许AT_MOST模式，遇到则修改成AT_LEAST模式（因为AT_MOST会导致ack插入比较频繁，所以不用它）
        if (this.consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_MOST_ONCE) {
            this.consumerInfo.setConsumerType(ConsumerType.DURABLE_AT_LEAST_ONCE);
            logger.warn("ConsumerClient[consumerInfo=" + consumerInfo
                    + "] used ConsumerType.DURABLE_AT_MOST_ONCE. Now change it to ConsumerType.DURABLE_AT_LEAST_ONCE.");
        }

        this.ackExecutor = this.consumerThreadPoolManager.getServiceHandlerThreadPool();
        this.sendMessageExecutor = this.consumerThreadPoolManager.getSendMessageThreadPool();

        heartBeatReceiver = new DefaultHeartBeatReceiver(consumerThreadPoolManager.getScheduledThreadPool(), this);

    }


    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();

        if (consumerInfo.getConsumerType() == ConsumerType.DURABLE_AT_LEAST_ONCE) {
            SendAckManager backupSendAckManager = new BackupSendAckManager(consumerInfo, swallowBuffer, messageDao, messageFilter);
            backupSendAckManager.initialize();

            sendAckManagers.add(backupSendAckManager);
        }

        SendAckManager sendAckManager = new NormalSendAckManager(consumerInfo, swallowBuffer, messageDao, startMessageId, messageFilter);
        sendAckManager.initialize();

        sendAckManagers.add(sendAckManager);

        for (SendAckManager manager : sendAckManagers) {
            addObserver(manager);
        }
    }

    @Override
    public void handleGreet(final Channel channel, final int clientThreadCount, MessageFilter messageFilter) {

        setMessageFilter(messageFilter);

        ackExecutor.execute(new Runnable() {
            @Override
            public void run() {

                connectedChannels.putIfAbsent(channel, IPUtil.getIpFromChannel(channel));
                started = true;
                for (int i = 0; i < clientThreadCount; i++) {
                    freeChannels.add(channel);
                }
            }
        });
    }


    @Override
    public void handleAck(final Channel channel, final long ackId, final ACKHandlerType type) {

        ackExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String consumerIp = IPUtil.getIpFromChannel(channel);


                    ConsumerMessage message = removeWaitAckMessages(ackId);

                    Long backupMessageId = null;
                    if (message != null) {
                        consumerCollector.ackMessage(consumerInfo, consumerIp, message.getMessage());
                        backupMessageId = message.getMessage().getBackupMessageId();
                    }

                    String ackIdStr = ackId + ",";//为日志解析
                    if (backupMessageId != null) {
                        ackIdStr += backupMessageId;
                    }

                    if (ackLogger.isInfoEnabled()) {
                        ackLogger.info(consumerInfo.getDest().getName() + "," + consumerInfo.getConsumerId() + ","
                                + ackIdStr + "," + IPUtil.simpleLogIp(connectedChannels.get(channel)));
                    }


                    if (ACKHandlerType.CLOSE_CHANNEL.equals(type)) {
                        if (logger.isInfoEnabled()) {
                            logger.info("receive ack(type=" + type + ") from " + consumerIp);
                        }
                        channel.close();
                    } else if (ACKHandlerType.SEND_MESSAGE.equals(type)) {
                        freeChannels.add(channel);
                    }
                } catch (Exception e) {
                    logger.error("handleAck wrong!", e);
                }
            }
        });
    }

    private ConsumerMessage removeWaitAckMessages(long ackId) {

        ConsumerMessage consumerMessage = null;

        for (SendAckManager sendAckManager : sendAckManagers) {
            consumerMessage = sendAckManager.ack(ackId);
            if (consumerMessage != null) {
                break;
            }
        }
        return consumerMessage;
    }

    @Override
    public void handleHeartBeat(Channel channel) {
        heartBeatReceiver.beat(channel);
    }

    @Override
    public void onNoHeartBeat(Channel channel) {
        if (logger.isInfoEnabled()) {
            logger.info("[onNoHeartBeat][close channel]" + channel);
        }
        channel.close();
    }


    @Override
    public void recordAck() {

        for (SendAckManager sendAckManager : sendAckManagers) {
            sendAckManager.recordAck();
        }

    }


    @Override
    public synchronized void handleChannelDisconnect(Channel channel) {
        connectedChannels.remove(channel);

        for (SendAckManager sendAckManager : sendAckManagers) {
            sendAckManager.destClosed(channel);
        }
    }


    @Override
    public boolean sendMessage() {

        final Channel channel = freeChannels.poll();
        if (channel == null || !channel.isActive()) {
            return false;
        }


        final ConsumerMessage message = getMessage();
        if (message == null) {
            if (channel != null) {
                boolean result = freeChannels.offer(channel);
                if (!result) {
                    logger.error("[sendMessage][channel put back error]" + consumerInfo);
                }
            }
            return false;
        }
        sendMessageExecutor.execute(new Runnable() {

            @Override
            public void run() {

                try {
                    boolean isAuth = consumerAuthController.isValid(consumerInfo, ((InetSocketAddress) channel.remoteAddress()).getAddress().getHostAddress());

                    if (!isAuth) {
                        logger.error(ConsumerUtil.getPrettyConsumerInfo(consumerInfo, channel) + " Consumer is disabled, channel will close.");
                        channel.close();
                    } else {
                        sendMessage(channel, message);
                    }
                } catch (InterruptedException e) {
                    logger.error("Get message from messageQueue thread InterruptedException", e);
                } catch (RuntimeException e) {
                    logger.error("Get message from messageQueue thread Exception", e);
                }
            }
        });
        return true;
    }


    private ConsumerMessage getMessage() {

        ConsumerMessage consumerMessage = null;
        for (SendAckManager sendAckManager : sendAckManagers) {
            consumerMessage = sendAckManager.send();
            if (consumerMessage != null) {
                break;
            }
        }

        return consumerMessage;
    }


    private void sendMessage(Channel channel, ConsumerMessage consumerMessage) throws InterruptedException {


        PktMessage pktMessage = new PktMessage(consumerInfo.getDest(), consumerMessage.getMessage());

        String consumerIpPort = IPUtil.getIpFromChannel(channel);

        Transaction consumerServerTransaction = Cat.getProducer().newTransaction(
                "Out:" + this.consumerInfo.getDest().getName(), consumerInfo.getConsumerId() + ":" + consumerIpPort);

        try {


            consumerCollector.sendMessage(consumerInfo, consumerIpPort, consumerMessage.getMessage());
            consumerMessage.beginSend(channel);

            if (logger.isDebugEnabled()) {
                logger.debug("[sendMessage][channel write]");
            }

            ChannelFuture future = channel.writeAndFlush(pktMessage);

            future.addListener(new SendChannelListener(consumerMessage));

            consumerServerTransaction.addData("mid", pktMessage.getContent().getMessageId());
            consumerServerTransaction.setStatus(com.dianping.cat.message.Message.SUCCESS);
        } catch (RuntimeException e) {

            consumerMessage.exceptionWhileSend(e);

            logger.error(consumerInfo.toString() + "：channel write error." + consumerMessage, e);

            consumerServerTransaction.setStatus(e);
        } finally {
            consumerServerTransaction.complete();
        }
    }

    @Override
    public void dispose() {

        consumerCollector.removeConsumer(consumerInfo);

        for (SendAckManager sendAckManager : sendAckManagers) {
            try {
                sendAckManager.dispose();
            } catch (Exception e) {
                logger.error("[dispose]" + sendAckManager, e);
            }
        }

        heartBeatReceiver.cancelCheck();

        for (Channel channel : connectedChannels.keySet()) {
            if (channel != null) {
                try {
                    if (logger.isInfoEnabled()) {
                        logger.info("[dispose]" + channel);
                    }
                    channel.close();
                } catch (Exception e) {
                    logger.error("[dispose]" + channel, e);
                }
            }
        }
    }

    private void setMessageFilter(MessageFilter newFilter) {

        MessageFilter oldFilter = this.messageFilter;
        if (oldFilter == newFilter) {
            return;
        }

        if (oldFilter != null && !oldFilter.equals(messageFilter)) {
            return;
        }


        if (logger.isInfoEnabled()) {
            logger.info("[setMessageFilter][messagefilterChanged]" + oldFilter + "," + newFilter);
        }
        this.messageFilter = newFilter;

        updateObservers(new ConsumerConfigChanged(oldFilter, newFilter));
    }


    @Override
    public boolean allChannelDisconnected() {

        return started && connectedChannels.isEmpty();
    }


    class SendChannelListener implements ChannelFutureListener {

        private ConsumerMessage consumerMessage;

        public SendChannelListener(ConsumerMessage consumerMessage) {
            this.consumerMessage = consumerMessage;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {

            if (future.isSuccess()) {
                consumerMessage.successSend();
            } else {
                consumerMessage.exceptionWhileSend(future.cause());
            }
        }
    }


    @Override
    public MessageFilter getMessageFilter() {

        return this.messageFilter.clone();
    }


    @Override
    public Set<Channel> connectedChannels() {
        return new HashSet<Channel>(connectedChannels.keySet());
    }

    @Override
    public List<SendAckManager> getSendAckManagers() {
        return sendAckManagers;
    }

    @Override
    public ConsumerWorkerStatus getStatus() {
        return new ConsumerWorkerStatus(this);
    }

    @Override
    public long getSequence() {
        return sequence;
    }
}
