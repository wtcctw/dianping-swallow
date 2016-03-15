package com.dianping.swallow.consumerserver.buffer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 切换回topic缓存
 * @author qi.yin
 *         2016/03/03  上午11:13.
 */
public class DefaultSwitchStrategy implements SwitchStrategy {

    private final Logger logger = LogManager.getLogger(getClass());

    private long minSwitchInterval;

    private long bufferInterval;

    public boolean isSwitch() {
        return false;
    }

}
