package com.dianping.swallow.client.nuclear.impl;

import com.dianping.swallow.consumer.ConsumerConfig;

/**
 * @author qi.yin
 *         2015/12/22  下午4:59.
 */
public class NuclearConsumerConfig extends ConsumerConfig {

    private boolean isAsync = false;

    public NuclearConsumerConfig() {
    }

    public NuclearConsumerConfig(boolean isAsync) {
        this.isAsync = isAsync;
    }

    public boolean isAsync() {
        return isAsync;
    }

    public void setIsAsync(boolean isAsync) {
        this.isAsync = isAsync;
    }



}
