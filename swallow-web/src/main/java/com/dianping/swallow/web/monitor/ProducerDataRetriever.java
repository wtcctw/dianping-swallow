package com.dianping.swallow.web.monitor;

import java.util.List;
import java.util.Map;

import com.dianping.swallow.common.server.monitor.data.QPX;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月21日 上午10:38:07
 */
public interface ProducerDataRetriever extends MonitorDataRetriever {

    boolean dataExistInMemory(long start, long end);

    StatsData getSaveDelay(String topic, long start, long end);

    StatsData getSaveDelay(String topic) throws Exception;

    Map<String, StatsData> getAllIpDelay(String topic, long start, long end);

    Map<String, StatsData> getAllIpDelay(String topic);

    StatsData getQpx(String topic, QPX qpx, long start, long end);

    StatsData getQpx(String topic, QPX qpx);

    Map<String, StatsData> getAllIpQpx(String topic, long start, long end);

    Map<String, StatsData> getAllIpQpx(String topic);

    Map<String, StatsData> getServerQpx(QPX qpx, long start, long end);

    Map<String, StatsData> getServerQpx(QPX qpx);

    List<OrderStatsData> getOrder(int size);

    List<OrderStatsData> getOrder(int size, long start, long end);
}
