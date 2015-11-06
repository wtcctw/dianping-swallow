package com.dianping.swallow.web.monitor.impl;

import com.dianping.swallow.common.server.monitor.data.StatisDetailType;

/**
 * @author qi.yin
 *         2015/11/03  下午2:10.
 */
public class ProducerIpStatsDataDesc extends ProducerStatsDataDesc {


    private String ip;

    public ProducerIpStatsDataDesc(String topic, String ip) {

        super(topic, StatisDetailType.SAVE_DELAY);
        this.ip = ip;
    }

    public ProducerIpStatsDataDesc(String topic, String ip, StatisDetailType dt) {
        super(topic, dt);
        this.ip = ip;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
