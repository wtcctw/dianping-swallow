package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.common.message.Destination;

import java.util.List;

/**
 * @author qi.yin
 *         2016/03/15  下午5:29.
 */
public class RingBufferRetrieveTask extends AbstractRetrieveTask {

    private CloseableRingBuffer<SwallowMessage> messageBuffer;

    public RingBufferRetrieveTask(RetrieveStrategy retrieveStrategy, Destination dest,
                                  MessageRetriever messageRetriever, CloseableRingBuffer<SwallowMessage> messageBuffer) {
        super(retrieveStrategy, dest, messageRetriever, null);
        this.messageBuffer = messageBuffer;
    }

    @Override
    protected void putMessage(List<SwallowMessage> messages) {
        messageBuffer.putMessages(messages);
    }

    @Override
    protected void setTailId(Long tailId) {
        messageBuffer.setTailMessageId(tailId);
    }

    @Override
    protected Long getTailId() {
        return messageBuffer.getTailMessageId();
    }

    @Override
    protected String getConsumerId() {
        return null;
    }

    @Override
    protected String getDetail() {
        return dest.toString();
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "@" + this.hashCode() + "," + dest + "]";
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (!(obj.getClass().equals(getClass()))) {
            return false;
        }
        RingBufferRetrieveTask cmp = (RingBufferRetrieveTask) obj;
        return cmp.dest.equals(dest);
    }

    @Override
    public int hashCode() {
        return dest.hashCode();
    }
}
