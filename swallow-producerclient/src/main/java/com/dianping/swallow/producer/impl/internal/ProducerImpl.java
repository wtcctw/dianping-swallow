package com.dianping.swallow.producer.impl.internal;

import java.util.Date;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.dianping.cat.Cat;
import com.dianping.cat.message.Message;
import com.dianping.cat.message.Transaction;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.packet.PktMessage;
import com.dianping.swallow.common.internal.packet.PktSwallowPACK;
import com.dianping.swallow.common.internal.processor.ProducerProcessor;
import com.dianping.swallow.common.internal.producer.ProducerSwallowService;
import com.dianping.swallow.common.message.Destination;
import com.dianping.swallow.common.producer.exceptions.SendFailedException;
import com.dianping.swallow.producer.Producer;
import com.dianping.swallow.producer.ProducerConfig;
import com.dianping.swallow.producer.ProducerHandler;

/**
 * 实现Producer接口的类
 *
 * @author tong.song
 */
public class ProducerImpl implements Producer {
    //常量定义
    private static final Logger LOGGER = LogManager.getLogger(ProducerImpl.class); //日志

    private final int[] sizeRangeArr = new int[]{1, 2, 4, 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4092};

    //变量定义
    private final Destination destination;                                  //Producer消息目的
    private final ProducerConfig producerConfig = new ProducerConfig();
    private final String producerIP;                                   //Producer IP地址
    private final String producerVersion;                              //Producer版本号
    private final ProducerSwallowService remoteService;
    private final int retryBaseInterval;
    private final int failedBaseInterval;
    private final int fileQueueFailedBaseInterval;
    private final ProducerHandler producerHandler;
    private ProducerProcessor producerProcessor;

    /**
     * @param destination                 此Producer发送消息的目的地
     * @param producerConfig              Producer的配置信息
     * @param producerIP                  本机IP地址
     * @param producerVersion             Producer版本号
     * @param remoteService               远程调用服务接口
     * @param retryBaseInterval           重试时的时间间隔起始值
     * @param fileQueueFailedBaseInterval filequeue失败时重试的时间间隔起始值
     * @param producerProcessor
     */
    public ProducerImpl(Destination destination, ProducerConfig producerConfig, String producerIP,
                        String producerVersion, ProducerSwallowService remoteService, int retryBaseInterval, int failedBaseInterval, int fileQueueFailedBaseInterval, ProducerProcessor producerProcessor) {
        if (producerConfig != null) {
            this.producerConfig.setAsyncRetryTimes(producerConfig.getAsyncRetryTimes());
            this.producerConfig.setMode(producerConfig.getMode());
            this.producerConfig.setSendMsgLeftLastSession(producerConfig.isSendMsgLeftLastSession());
            this.producerConfig.setSyncRetryTimes(producerConfig.getSyncRetryTimes());
            this.producerConfig.setThreadPoolSize(producerConfig.getThreadPoolSize());
            this.producerConfig.setZipped(producerConfig.isZipped());
            this.producerConfig.setFilequeueBaseDir(producerConfig.getFilequeueBaseDir());
        } else {
            LOGGER.warn("config is null, use default settings.");
        }

        //设置Producer的IP地址及版本号,设置远程调用
        this.destination = destination;
        this.producerIP = producerIP;
        this.producerVersion = producerVersion;
        this.remoteService = remoteService;
        this.retryBaseInterval = retryBaseInterval;
        this.failedBaseInterval = failedBaseInterval;
        this.fileQueueFailedBaseInterval = fileQueueFailedBaseInterval;
        this.producerProcessor = producerProcessor;

        //设置Producer工作模式
        switch (this.producerConfig.getMode()) {
            case SYNC_MODE:
                producerHandler = new HandlerSynchroMode(this);
                break;
            case ASYNC_MODE:
                producerHandler = new HandlerAsynchroMode(this);
                break;
            case ASYNC_SEPARATELY_MODE:
                producerHandler = new HandlerAsynchroSeparatelyMode(this);
                break;
            default:
                producerHandler = new HandlerAsynchroMode(this);
                break;
        }

    }

    /**
     * 将Object类型的content发送到指定的Destination
     *
     * @param content 待发送的消息内容
     * @return 异步模式返回null，同步模式返回将content转化为json字符串后，与其对应的SHA-1签名
     * @throws SendFailedException 发送失败则抛出此异常
     */
    @Override
    public String sendMessage(Object content) throws SendFailedException {
        return sendMessage(content, null, null);
    }

    /**
     * 将Object类型的content发送到指定的Destination
     *
     * @param content     待发送的消息内容
     * @param messageType 消息类型，用于消息过滤
     * @return 异步模式返回null，同步模式返回content的SHA-1字符串
     * @throws SendFailedException 发送失败则抛出此异常
     */
    @Override
    public String sendMessage(Object content, String messageType) throws SendFailedException {
        return sendMessage(content, null, messageType);
    }

    /**
     * 将Object类型的content发送到指定的Destination
     *
     * @param content    待发送的消息内容
     * @param properties 消息属性，留作后用
     * @return 异步模式返回null，同步模式返回content的SHA-1字符串
     * @throws SendFailedException 发送失败则抛出此异常
     */
    @Override
    public String sendMessage(Object content, Map<String, String> properties) throws SendFailedException {
        return sendMessage(content, properties, null);
    }

    /**
     * 将Object类型的content发送到指定的Destination
     *
     * @param content     待发送的消息内容
     * @param properties  消息属性，留作后用
     * @param messageType 消息类型，用于消息过滤
     * @return 异步模式返回null，同步模式返回content的SHA-1字符串
     * @throws SendFailedException 发送失败则抛出此异常
     */
    @Override
    public String sendMessage(Object content, Map<String, String> properties, String messageType)
            throws SendFailedException {
        if (content == null) {
            throw new IllegalArgumentException("Message content can not be null.");
        }
        //根据content生成SwallowMessage
        SwallowMessage swallowMsg = new SwallowMessage();
        String ret = null;

        Transaction producerTransaction = Cat.getProducer().newTransaction("MsgProduced", destination.getName() + ":" + producerIP);
        try {
            //根据content生成SwallowMessage
            swallowMsg.setContent(content);
            swallowMsg.setVersion(producerVersion);
            swallowMsg.setGeneratedTime(new Date());
            swallowMsg.setSourceIp(producerIP);

            if (messageType != null) {
                swallowMsg.setType(messageType);
            }
            if (properties != null) {
                for (Map.Entry<String, String> entry : properties.entrySet()) {
                    if (!(entry.getKey() instanceof String)
                            || (entry.getValue() != null && !(entry.getValue() instanceof String))) {
                        throw new IllegalArgumentException("Type of properties should be Map<String, String>.");
                    }
                }
                swallowMsg.setProperties(properties);
            }

            try {
                producerProcessor.beforeSend(swallowMsg);
            } catch (SwallowException e) {
                throw new SendFailedException("[fail]" + swallowMsg, e);
            }

            //构造packet
            PktMessage pktMessage = new PktMessage(destination, swallowMsg);
            pktMessage.setCatEventID(getCatEventId());

            switch (producerConfig.getMode()) {
                case SYNC_MODE://同步模式
                    PktSwallowPACK pktSwallowPACK = (PktSwallowPACK) producerHandler.doSendMsg(pktMessage);
                    if (pktSwallowPACK != null) {
                        ret = pktSwallowPACK.getShaInfo();
                    }
                    break;
                case ASYNC_MODE://异步模式
                    producerHandler.doSendMsg(pktMessage);
                    break;
                case ASYNC_SEPARATELY_MODE://异步模式
                    producerHandler.doSendMsg(pktMessage);
                    break;
            }
            //使用cat纪录Message大小
            recordMsgSizeEvent(swallowMsg.size());

            producerTransaction.setStatus(Message.SUCCESS);

        } catch (SendFailedException e) {
            //使用CAT监控处理消息的时间
            producerTransaction.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } catch (RuntimeException e) {
            //使用CAT监控处理消息的时间
            producerTransaction.setStatus(e);
            Cat.getProducer().logError(e);
            throw e;
        } finally {
            producerTransaction.complete();
        }

        return ret;
    }

    private String getCatEventId() {
        try {
            return Cat.getProducer().createMessageId();
        } catch (Exception e) {
            return "UnknownMessageId";
        }
    }

    /**
     * @return 返回远程调用接口
     */
    public ProducerSwallowService getRemoteService() {
        return remoteService;
    }

    /**
     * @return 返回ProducerConfig
     */
    public ProducerConfig getProducerConfig() {
        return producerConfig;
    }

    /**
     * @return 返回producer消息目的地
     */
    public Destination getDestination() {
        return destination;
    }

    /**
     * @return 重试的时间间隔起始值
     */
    public int getRetryBaseInterval() {
        return retryBaseInterval;
    }

    /**
     * @return 发送失败后，间隔多久进行重新获取的时间间隔起始值
     */
    public int getFailedBaseInterval() {
        return failedBaseInterval;
    }

    public int getFileQueueFailedBaseInterval() {
        return fileQueueFailedBaseInterval;
    }

    public String getProducerIP() {
        return producerIP;
    }

    private void recordMsgSizeEvent(long size) {
        int sizeK = (int) Math.ceil(size * 1.0 / 1000);
        String rangeValue;
        if (sizeK <= 1) {
            sizeK = 1;
        }
        int index = (int) Math.ceil(Math.log(sizeK) / Math.log(2));
        if (index >= sizeRangeArr.length) {
            rangeValue = ">" + String.valueOf(sizeRangeArr[sizeRangeArr.length - 1]) + "K";
        } else {
            rangeValue = "<=" + String.valueOf(sizeRangeArr[index]) + "K";
        }

        Cat.getProducer().logEvent("MsgProduced.msgSize", rangeValue, Message.SUCCESS, "msgSize=" + String.valueOf(size));
    }

}
