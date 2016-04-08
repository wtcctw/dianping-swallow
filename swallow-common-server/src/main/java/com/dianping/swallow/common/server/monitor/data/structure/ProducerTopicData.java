package com.dianping.swallow.common.server.monitor.data.structure;

import com.dianping.swallow.common.internal.util.MapUtil;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月26日 上午9:42:55
 */
public class ProducerTopicData extends TotalMap<MessageInfo> {

    private static final long serialVersionUID = 1L;

    public void sendMessage(String producerIp, long messageId, long msgSize, long sendTime, long saveTime) {

        MessageInfo messageInfo = MapUtil.getOrCreate(this, producerIp, MessageInfo.class);
        messageInfo.addMessage(messageId, msgSize, sendTime, saveTime);
    }

    @Override
    public boolean equals(Object obj) {

        if (!(obj instanceof ProducerTopicData)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return hashCode();
    }

    @Override
    protected MessageInfo createValue() {
        return new MessageInfo();
    }
}

