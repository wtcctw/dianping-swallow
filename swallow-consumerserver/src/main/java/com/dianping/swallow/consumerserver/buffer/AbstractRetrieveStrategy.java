package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.common.message.Destination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author qi.yin
 *         2016/03/15  下午8:04.
 */
public abstract class AbstractRetrieveStrategy implements RetrieveStrategy {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected Deque<RetrieveStatus> status = new LinkedList<RetrieveStatus>();

    private Destination dest;

    protected final int statusCount = 10;

    /*上次读取消息数值小于此值，间隔一定时间再取*/
    protected int minRetrieveCount = 10;

    protected int minRetrieveInterval = 20;//如果上次读取的数据小于minRetrieveCount，则在此时间内不读取数据
    protected long maxRetrieveInterval = 500;
    /**
     * 如果读取消息一直为0，不停延时
     */
    protected volatile int zeroCount = 0;

    /*队列最大消息数，大于此值，不取*/
    protected int maxThreshold;
    protected AtomicInteger taskCount = new AtomicInteger();

    protected int maxRetriverTaskCount = 3;

    public AbstractRetrieveStrategy(Destination dest, int minRetrieveInterval, int maxThreshold, int maxRetriverTaskCount) {
        this.minRetrieveInterval = minRetrieveInterval;
        this.maxThreshold = maxThreshold;
        this.dest = dest;
        this.maxRetriverTaskCount = maxRetriverTaskCount;
    }


    protected long getZeroDelayTime() {

        long delay = minRetrieveInterval * zeroCount;

        if (delay > maxRetrieveInterval || delay <= 0) {
            delay = maxRetrieveInterval;
        }
        return delay;
    }

    @Override
    public void retrieved(int count) {
        if (count == 0) {
            zeroCount++;
        } else {
            zeroCount = 0;
        }

        status.offer(new RetrieveStatus(count, System.currentTimeMillis()));
        if (status.size() > statusCount) {
            status.poll();
        }
    }

    @Override
    public void beginRetrieve() {

    }

    @Override
    public void endRetrieve() {
        taskCount.decrementAndGet();
    }

    @Override
    public void offerNewTask() {
        taskCount.incrementAndGet();
    }

    @Override
    public boolean canPutNewTask() {
        if (taskCount.get() >= maxRetriverTaskCount) {
            return false;
        }
        return true;
    }

    public int getMaxRetriverTaskCount() {
        return maxRetriverTaskCount;
    }

    @Override
    public void increaseMessageCount() {

    }

    @Override
    public void increaseMessageCount(int count) {

    }

    @Override
    public void decreaseMessageCount() {

    }

    @Override
    public void decreaseMessageCount(int count) {

    }

    @Override
    public int messageCount() {
        return 0;
    }


    static class RetrieveStatus {

        private int count;
        private long retrieveTime;

        public RetrieveStatus(int count, long retireveTime) {
            this.count = count;
            this.retrieveTime = retireveTime;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public long getRetrieveTime() {
            return retrieveTime;
        }

        public void setRetrieveTime(long retrieveTime) {
            this.retrieveTime = retrieveTime;
        }

    }
}
