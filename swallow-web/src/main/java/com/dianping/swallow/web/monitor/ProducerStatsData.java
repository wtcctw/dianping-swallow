package com.dianping.swallow.web.monitor;

/**
 * @author qi.yin
 *         2016/01/06  下午1:51.
 */
public class ProducerStatsData extends AbstractStatsData {

    public StatsData getStatsData() {
        return statsData;
    }

    private StatsData statsData;

    public ProducerStatsData(StatsData statsData) {
        this.statsData = statsData;
    }

}
