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
	
	@JsonIgnore
	boolean isEmpty();


	@JsonIgnore
	NavigableMap<Long, Long> getDelay(StatisType type);

	@JsonIgnore
	NavigableMap<Long, QpxData> getQpx(StatisType type);

	String toString(String key);
	
	public static class QpxData {

		private Long qpx;

		private Long total;

		public QpxData(Long qpx, Long total) {
			this.qpx = qpx;
			this.total = total;
		}

		public Long getQpx() {
			return qpx;
		}

		public void setQpx(Long qpx) {
			this.qpx = qpx;
		}

		public Long getTotal() {
			return total;
		}

		public void setTotal(Long total) {
			this.total = total;
		}
	}

}
