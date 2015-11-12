package com.dianping.swallow.common.server.monitor.data;

import com.dianping.swallow.common.internal.monitor.KeyMergeable;
import com.dianping.swallow.common.internal.monitor.Mergeable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.NavigableMap;


/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午5:04:56
 */
public interface Statisable<V> extends KeyMergeable {

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
	 */
	void build(QPX qpx, Long startKey, Long endKey, int intervalCount);
	
	@JsonIgnore
	boolean isEmpty();


	@JsonIgnore
	NavigableMap<Long, Long> getDelay(StatisType type);

	@JsonIgnore
	NavigableMap<Long, QpxData> getQpx(StatisType type);

	String toString(String key);
	
	public static class QpxData implements Mergeable{

		private Long qpx;

		private Long total;

		public QpxData(){

		}

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

		@Override
		public void merge(Mergeable merge) {
			if(!(merge instanceof QpxData)){
				throw new IllegalArgumentException("wrong type " + merge.getClass());
			}

			QpxData toMerge = (QpxData) merge;
			if(total == null){
				total = new Long(0);
			}
			total += toMerge.getTotal();
			if(qpx == null){
				qpx = new Long(0);
			}
			qpx += toMerge.getQpx();
		}

		@Override
		public Object clone() throws CloneNotSupportedException {

			throw new CloneNotSupportedException("clone not support");
		}
	}

}
