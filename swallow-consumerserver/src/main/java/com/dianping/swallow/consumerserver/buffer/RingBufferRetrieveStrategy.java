package com.dianping.swallow.consumerserver.buffer;

import com.dianping.swallow.common.message.Destination;

/**
 * @author qi.yin
 *         2016/03/15  下午8:05.
 */
public class RingBufferRetrieveStrategy extends AbstractRetrieveStrategy {


    public RingBufferRetrieveStrategy(Destination dest, int minRetrieveInterval, int maxThreshold, int maxRetriverTaskCountPerDest) {
        super(dest, minRetrieveInterval, maxThreshold, maxRetriverTaskCountPerDest);
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

        return true;
    }

}
