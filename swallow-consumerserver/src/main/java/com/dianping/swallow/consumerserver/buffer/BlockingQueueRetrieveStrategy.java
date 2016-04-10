package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.common.internal.consumer.ConsumerInfo;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qi.yin
 *         2016/03/15  下午8:40.
 */
public class BlockingQueueRetrieveStrategy extends AbstractRetrieveStrategy {

    private AtomicInteger messageCount = new AtomicInteger();
    private ConsumerInfo consumerInfo;

    public BlockingQueueRetrieveStrategy(ConsumerInfo consumerInfo, int minRetrieveInterval, int maxThreshold, int maxRetriverTaskCountPerConsumer) {
        super(consumerInfo.getDest(), minRetrieveInterval, maxThreshold, maxRetriverTaskCountPerConsumer);
        this.consumerInfo = consumerInfo;
    }

    @Override
    public boolean isRetrieve() {

        RetrieveStatus rs = status.peekLast();
        if (rs == null) {
            return true;
        }

        long currentTime = System.currentTimeMillis();

        if (zeroCount > 0 && (currentTime - rs.getRetrieveTime() < getZeroDelayTime())) {
            return false;
        }

        if (rs.getCount() <= minRetrieveCount && (currentTime - rs.getRetrieveTime() < minRetrieveInterval)) {
            return false;
        }

        if (messageCount.get() >= maxThreshold) {
            if (logger.isInfoEnabled()) {
                logger.info("[isRetrieve][message exceed maxthreshold]" + consumerInfo + "," + maxThreshold + ", " + messageCount.get());
            }
            return false;
        }

        return true;
    }

    @Override
    public void increaseMessageCount() {
        messageCount.incrementAndGet();
    }

    @Override
    public void increaseMessageCount(int count) {
        messageCount.addAndGet(count);
    }

    @Override
    public void decreaseMessageCount() {
        messageCount.decrementAndGet();
    }

    @Override
    public void decreaseMessageCount(int count) {
        messageCount.addAndGet(-count);
    }

    @Override
    public int messageCount() {
        return messageCount.get();
    }
}
