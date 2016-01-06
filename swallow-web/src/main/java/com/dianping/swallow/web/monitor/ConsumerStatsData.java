package com.dianping.swallow.web.monitor;

/**
 * @author qi.yin
 *         2016/01/06  下午1:52.
 */
public class ConsumerStatsData extends AbstractStatsData {

    private String consumerId;
    private StatsData sendData;
    private StatsData ackData;

    public ConsumerStatsData(String consumerId, StatsData sendData, StatsData ackData) {
        this.consumerId = consumerId;
        this.sendData = sendData;
        this.ackData = ackData;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public StatsData getSendData() {
        return sendData;
    }

    public StatsData getAckData() {
        return ackData;
    }
}
