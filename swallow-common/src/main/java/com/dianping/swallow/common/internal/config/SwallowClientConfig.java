package com.dianping.swallow.common.internal.config;

/**
 * @author qi.yin
 *         2015/12/16  下午5:50.
 */
public interface SwallowClientConfig {

    boolean isOnMessageEnabled();

    boolean isLog4j2Enabled();

    boolean isConsumerAsync();

    boolean isConsumerOnline();
}
