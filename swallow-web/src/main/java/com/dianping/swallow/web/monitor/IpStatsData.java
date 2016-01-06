package com.dianping.swallow.web.monitor;

/**
 * @author qi.yin
 *         2016/01/06  下午2:06.
 */
public class IpStatsData implements Comparable {

    private String appName;

    private String ip;

    public AbstractStatsData statsData;

    public IpStatsData(String appName, String ip, AbstractStatsData statsData) {
        this.appName = appName;
        this.ip = ip;
        this.statsData = statsData;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public AbstractStatsData getStatsData() {
        return statsData;
    }

    public void setStatsData(AbstractStatsData statsData) {
        this.statsData = statsData;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Override
    public int compareTo(Object o) {
        IpStatsData other = (IpStatsData) o;
        return this.appName.compareTo(other.getAppName());
    }
}
