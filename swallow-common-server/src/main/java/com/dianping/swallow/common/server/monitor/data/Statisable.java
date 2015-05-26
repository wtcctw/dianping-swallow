package com.dianping.swallow.common.server.monitor.data;

import java.util.NavigableMap;

import com.fasterxml.jackson.annotation.JsonIgnore;


/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午5:04:56
 */
public interface Statisable<V> {

	void add(Long time, V added);
	
	/**
	 * 删除掉<time之前的数据
	 * @param time
	 */
	void removeBefore(Long time);
	
	/**
	 * >= startKey <= endKey
	 * @param qpx
	 * @param startKey
	 * @param endKey
	 * @param intervalCount
	 * @param step for debug log
	 */
	void build(QPX qpx, Long startKey, Long endKey, int intervalCount);
	
	
	/**
	 * 判断内部存储的数据是否无效，如果无效，清除信息
	 * @return
	 */
	void clean();

	boolean isEmpty();


	@JsonIgnore
	NavigableMap<Long, Long> getDelay(StatisType type);

	@JsonIgnore
	NavigableMap<Long, Long> getQpx(StatisType type);

}
