package com.dianping.swallow.web.monitor;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.statis.CasKeys;

import java.util.List;
import java.util.Map;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月21日 上午10:38:07
 */
public interface ProducerDataRetriever extends MonitorDataRetriever {

    boolean dataExistInMemory(CasKeys keys, long start, long end);

    StatsData getSaveDelay(String topic, long start, long end);

    StatsData getSaveDelay(String topic) throws Exception;

    Map<String, StatsData> getAllIpDelay(String topic, long start, long end);

    Map<String, StatsData> getAllIpDelay(String topic);

    List<IpStatsData> getAllIpDelayList(String topic, long start, long end);

    List<IpStatsData> getAllIpDelayList(String topic);

    StatsData getQpx(String topic, QPX qpx, long start, long end);

    StatsData getQpx(String topic, QPX qpx);

    Map<String, StatsData> getAllIpQpx(String topic, long start, long end);

    Map<String, StatsData> getAllIpQpx(String topic);

    List<IpStatsData> getAllIpQpxList(String topic, long start, long end);

    List<IpStatsData> getAllIpQpxList(String topic);

    Map<String, StatsData> getServerQpx(QPX qpx, long start, long end);

    Map<String, StatsData> getServerQpx(QPX qpx);

    List<OrderStatsData> getOrder(int size);

    List<OrderStatsData> getOrder(int size, long start, long end);

}
