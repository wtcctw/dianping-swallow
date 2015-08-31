package com.dianping.swallow.web.monitor;

import java.util.List;

/**
 * 
 * @author qiyin
 *
 *         2015年8月26日 下午8:31:55
 */
public interface StatsDataOrderable {

	List<OrderStatsData> getOrderStatsData(int size);

	List<OrderStatsData> getOrderStatsData(int size, long start, long end);
}
