package com.dianping.swallow.consumerserver.buffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 切换回topic缓存
 *
 * @author qi.yin
 *         2016/03/03  上午11:13.
 */
public class DefaultSwitchStrategy implements SwitchStrategy {

    private final Logger logger = LogManager.getLogger(getClass());

    private long lastSwitchMillis = System.currentTimeMillis();

    private int minSwitchInterval;//ms

    private int maxSwitchInterval;//ms

    private volatile long retrySwitchCount;

    private int switchTimeUnit;//min

    private volatile boolean isOverBuffer = false;


    public DefaultSwitchStrategy() {

    }

    public DefaultSwitchStrategy(int minSwitchInterval, int maxSwitchInterval, int switchTimeUnit) {
        this.minSwitchInterval = minSwitchInterval;
        this.maxSwitchInterval = maxSwitchInterval;
        this.switchTimeUnit = switchTimeUnit;
    }


    public boolean isSwitch() {

        if (isOverBuffer) {
            return true;
        }
        long tempMaxInterval = minSwitchInterval + switchTimeUnit * retrySwitchCount;

        if (tempMaxInterval > maxSwitchInterval) {
            tempMaxInterval = maxSwitchInterval;
        }
        if (System.currentTimeMillis() - lastSwitchMillis > tempMaxInterval) {
            return true;
        }
        return false;
    }

    public  void switched(int result) {

        if (result == 0) {
            lastSwitchMillis = System.currentTimeMillis();
            retrySwitchCount = 0;
            isOverBuffer = false;
        } else if (result == 1) {
            isOverBuffer = true;
            retrySwitchCount = 0;
        } else {
            retrySwitchCount++;
            isOverBuffer = false;
        }
    }

}
