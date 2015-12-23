package com.dianping.swallow.consumer.nuclear.impl;

import com.dianping.swallow.consumer.ConsumerConfig;

/**
 * @author qi.yin
 *         2015/12/22  下午4:59.
 */
public class NuclearConsumerConfig extends ConsumerConfig {

    public NuclearConsumerConfig() {
        super();
    }

    public NuclearConsumerConfig(boolean isAsync) {
        super();
        this.isAsync = isAsync;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setIsAsync(boolean isAsync) {
        this.isAsync = isAsync;
    }

    private boolean isAsync = false;

}
