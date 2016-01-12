package com.dianping.swallow.web.model.stats;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiyin
 *         <p/>
 *         2015年10月19日 下午7:22:18
 */
public abstract class AbstractIpGroupStatsData<T extends AbstractIpStatsData> {

    public List<T> getIpStatsDatas() {
        return ipStatsDatas;
    }

    public void setIpStatsDatas(List<T> ipStatsDatas) {
        this.ipStatsDatas = ipStatsDatas;
    }

    private List<T> ipStatsDatas;

    public void addIpStatsData(T ipStatsData) {
        if (ipStatsDatas == null) {
            ipStatsDatas = new ArrayList<T>();
        }
        ipStatsDatas.add(ipStatsData);
    }

    public boolean hasStatsData(long totalThreshold) {
        if (ipStatsDatas == null || ipStatsDatas.isEmpty()) {
            return false;
        }
        long qpsCount = 0;
        for (T ipStatsData : ipStatsDatas) {
            qpsCount += ipStatsData.getQpsCount();
        }
        if ((qpsCount / ipStatsDatas.size()) >= totalThreshold) {
            return true;
        }
        return false;
    }
}
