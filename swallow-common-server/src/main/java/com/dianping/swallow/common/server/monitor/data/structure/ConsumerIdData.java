package com.dianping.swallow.common.server.monitor.data.structure;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.springframework.data.annotation.Transient;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.message.SwallowMessageUtil;
import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.dianping.swallow.common.internal.util.MapUtil;
import com.dianping.swallow.common.server.monitor.data.TotalBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月26日 上午9:48:42
 */
public class ConsumerIdData extends AbstractTotalable implements KeyMergeable, TotalBuilder, Serializable {

    private static final long serialVersionUID = 1L;

    public static final String ACK_DELAY_FOR_UNIT_KEY = "ACK_DELAY_FOR_UNIT_KEY";

    @Transient
    protected transient final Logger logger = LogManager.getLogger(getClass());

    private MessageInfoTotalMap sendMessages = new MessageInfoTotalMap();
    private MessageInfoTotalMap ackMessages = new MessageInfoTotalMap();

    @Transient
    @JsonIgnore
    private Map<Long, Long> messageSendTimes = new ConcurrentHashMap<Long, Long>();

    public ConsumerIdData() {

    }

    public MessageInfoTotalMap getSendMessages() {
        return sendMessages;
    }

    public MessageInfoTotalMap getAckMessages() {
        return ackMessages;
    }

    @Transient
    @JsonIgnore
    public MessageInfo getTotalSendMessages() {
        return sendMessages.getTotal();
    }

    @Transient
    @JsonIgnore
    public MessageInfo getTotalAckMessages() {
        return ackMessages.getTotal();

    }

    @Override
    public void buildTotal() {
        sendMessages.buildTotal();
        ackMessages.buildTotal();
    }

    @Override
    public Object getTotal() {
        return null;
    }

    @Override
    public void setTotal() {
        super.setTotal();
        sendMessages.setTotal();
        ackMessages.setTotal();

    }

    public void merge(Mergeable merge) {

        checkType(merge);

        ConsumerIdData toMerge = (ConsumerIdData) merge;


        sendMessages.merge(toMerge.sendMessages);
        ackMessages.merge(toMerge.ackMessages);
    }

    private void checkType(Mergeable merge) {
        if (!(merge instanceof ConsumerIdData)) {
            throw new IllegalArgumentException("wrong type " + merge.getClass());
        }
    }

    @Override
    public void merge(String key, KeyMergeable merge) {

        checkType(merge);

        ConsumerIdData toMerge = (ConsumerIdData) merge;
        sendMessages.merge(key, toMerge.sendMessages);
        ackMessages.merge(key, toMerge.ackMessages);
    }

    public void sendMessage(String consumerIp, SwallowMessage message) {

        //记录消息发送时间
        messageSendTimes.put(message.getMessageId(), System.currentTimeMillis());

        MessageInfo messageInfo = MapUtil.getOrCreate(sendMessages, consumerIp, MessageInfo.class);

        long saveTime = SwallowMessageUtil.getSaveTime(message);
        if (saveTime <= 0) {
            saveTime = System.currentTimeMillis();
        }

        messageInfo.addMessage(message.getMessageId(), message.size(), saveTime, System.currentTimeMillis());

    }

    public void ackMessage(String consumerIp, SwallowMessage message) {

        Long messageId = message.getMessageId();
        Long sendTime = messageSendTimes.get(messageId);

        if (sendTime == null) {
            logger.warn("[ackMessage][unfound message]" + messageId + "," + this);
            sendTime = System.currentTimeMillis();
        }

        try {
            MessageInfo messageInfo = MapUtil.getOrCreate(ackMessages, consumerIp, MessageInfo.class);
            long current = System.currentTimeMillis();

            if (System.getProperty(ACK_DELAY_FOR_UNIT_KEY) != null) {//for unit test
                sendTime = current - Long.parseLong(System.getProperty(ACK_DELAY_FOR_UNIT_KEY));
            }
            messageInfo.addMessage(messageId, message.size(), sendTime, current);

        } finally {
            messageSendTimes.remove(messageId);
        }
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof ConsumerIdData)) {
            return false;
        }

        ConsumerIdData cmp = (ConsumerIdData) obj;
        return cmp.sendMessages.equals(sendMessages)
                && cmp.ackMessages.equals(ackMessages);
    }

    @Override
    public String toString() {
        return JsonBinder.getNonEmptyBinder().toJson(this);
    }

    @Override
    public int hashCode() {

        return (int) (sendMessages.hashCode() ^ ackMessages.hashCode());
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ConsumerIdData clone = (ConsumerIdData) super.clone();
        clone.sendMessages = (MessageInfoTotalMap) sendMessages.clone();
        clone.ackMessages = (MessageInfoTotalMap) ackMessages.clone();
        return clone;
    }

}

