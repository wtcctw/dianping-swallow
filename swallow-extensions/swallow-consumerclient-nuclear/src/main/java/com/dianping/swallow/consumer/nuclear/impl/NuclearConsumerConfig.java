package com.dianping.swallow.consumer.nuclear.impl;

import com.dianping.swallow.common.internal.util.EnvUtil;
import com.dianping.swallow.consumer.ConsumerConfig;

/**
 * @author qi.yin
 *         2015/12/22  下午4:59.
 */
public class NuclearConsumerConfig extends ConsumerConfig {


    public NuclearConsumerConfig() {
        super();
    }

    public NuclearConsumerConfig(boolean isOnline, boolean isAsync) {
        this.isOnline = isOnline;
        this.isAsync = isAsync;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    private boolean isOnline = EnvUtil.isProduct() ? true : false;

    public boolean isAsync() {
        return isAsync;
    }

    public void setIsAsync(boolean isAsync) {
        this.isAsync = isAsync;
    }

    private boolean isAsync = false;


}
