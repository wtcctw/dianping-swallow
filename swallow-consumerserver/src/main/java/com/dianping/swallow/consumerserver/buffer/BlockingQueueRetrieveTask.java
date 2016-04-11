package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.common.consumer.MessageFilter;
import com.dianping.swallow.common.internal.consumer.ConsumerInfo;
import com.dianping.swallow.common.internal.message.SwallowMessage;
import com.dianping.swallow.common.internal.util.EnvUtil;

import java.util.List;

/**
 * @author qi.yin
 *         2016/03/15  下午5:28.
 */
public abstract class BlockingQueueRetrieveTask extends AbstractRetrieveTask {

    protected CloseableBlockingQueue<SwallowMessage> blockingQueue;

    protected ConsumerInfo consumerInfo;

    public BlockingQueueRetrieveTask(RetrieveStrategy retrieveStrategy, ConsumerInfo consumerInfo, MessageRetriever messageRetriever,
                                     CloseableBlockingQueue<SwallowMessage> blockingQueue, MessageFilter messageFilter) {
        super(retrieveStrategy, consumerInfo.getDest(), messageRetriever, messageFilter);
        this.blockingQueue = blockingQueue;
        this.consumerInfo = consumerInfo;
    }

    @Override
    protected void putMessage(List<SwallowMessage> messages) {

        blockingQueue.putMessage(messages);
    }

    @Override
    protected void setTailId(Long tailId) {
        blockingQueue.setTailMessageId(tailId);
    }

    @Override
    protected Long getTailId() {
        return blockingQueue.getTailMessageId();
    }

    @Override
    public String toString() {
        return "[" + getClass().getSimpleName() + "@" + this.hashCode() + "," + consumerInfo + "]";
    }

    @Override
    public boolean equals(Object obj) {

        if (obj == null) {
            return false;
        }
        if (!(obj.getClass().equals(getClass()))) {
            return false;
        }
        BlockingQueueRetrieveTask cmp = (BlockingQueueRetrieveTask) obj;
        return cmp.consumerInfo.equals(consumerInfo);
    }

    @Override
    public int hashCode() {
        return consumerInfo.hashCode();
    }

    @Override
    protected String getDetail(){
        return consumerInfo.toString();
    }

}
