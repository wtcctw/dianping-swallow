package com.dianping.swallow.common.internal.config;

import com.dianping.swallow.common.internal.codec.impl.JsonBinder;

import java.util.Arrays;

/**
 * @author qi.yin
 *         2016/01/20  下午3:55.
 */
public class GroupConfig {

    private String[] producerIps;

    private String[] consumerIps;

    public GroupConfig() {

    }

    public GroupConfig(String[] producerIps, String[] consumerIps) {
        this.producerIps = producerIps;
        this.consumerIps = consumerIps;
    }

    public String[] getProducerIps() {
        return producerIps;
    }

    public void setProducerIps(String[] producerIps) {
        this.producerIps = producerIps;
    }

    public String[] getConsumerIps() {
        return consumerIps;
    }

    public void setConsumerIps(String[] consumerIps) {
        this.consumerIps = consumerIps;
    }

    public String toJson() {
        return JsonBinder.getNonEmptyBinder().toJson(this);

    }

    public static GroupConfig fromJson(String jsonString) {

        return JsonBinder.getNonEmptyBinder().fromJson(jsonString, GroupConfig.class);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroupConfig that = (GroupConfig) o;

        if (!Arrays.equals(producerIps, that.producerIps)) return false;

        return Arrays.equals(consumerIps, that.consumerIps);

    }

    @Override
    public int hashCode() {
        int result = producerIps != null ? Arrays.hashCode(producerIps) : 0;
        result = 31 * result + (consumerIps != null ? Arrays.hashCode(consumerIps) : 0);
        return result;
    }
}
