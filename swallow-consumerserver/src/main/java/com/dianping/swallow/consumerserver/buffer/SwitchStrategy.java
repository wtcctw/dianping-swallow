package com.dianping.swallow.consumerserver.buffer;

/**
 * @author qi.yin
 *         2016/03/03  上午11:11.
 */
public interface SwitchStrategy {

    boolean isSwitch();

    void switched(int result);
}
