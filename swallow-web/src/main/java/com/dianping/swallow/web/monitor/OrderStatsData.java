package com.dianping.swallow.web.monitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author qiyin
 *
 *         2015年8月25日 上午11:22:25
 */
public class OrderStatsData {

	private static final int DEFAULT_SIZE = 10;

	private int capacity = DEFAULT_SIZE;

	private StatsDataDesc info;

	@SuppressWarnings("unused")
	private List<OrderEntity> dataResults;

	private PriorityBlockingQueue<OrderEntity> datas;

	private long start;

	private long end;

	public OrderStatsData() {

	}

	public OrderStatsData(StatsDataDesc info, long start, long end) {
		this(DEFAULT_SIZE, info, start, end);
	}

	public OrderStatsData(int capacity, StatsDataDesc info, long start, long end) {
		this.capacity = capacity;
		this.datas = new PriorityBlockingQueue<OrderEntity>(capacity);
		this.info = info;
		this.start = start;
		this.end = end;
	}

	public void add(OrderEntity entity) {
		if (this.datas.size() < this.capacity) {
			this.datas.add(entity);
		} else {
			OrderEntity o = this.datas.peek();
			if (entity.getSumData() > o.getSumData()) {
				this.datas.poll();
				this.datas.add(entity);
			}
		}
	}

	public StatsDataDesc getInfo() {
		return info;
	}

	public void setInfo(StatsDataDesc info) {
		this.info = info;
	}

	@JsonIgnore
	public PriorityBlockingQueue<OrderEntity> getDatas() {
		return datas;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public List<OrderEntity> getDataResults() {
		List<OrderEntity> orderEntitys = new ArrayList<OrderEntity>(datas);
		Collections.sort(orderEntitys);
		Collections.reverse(orderEntitys);
		return orderEntitys;
	}

	public void setDataResults(List<OrderEntity> dataResults) {
		this.dataResults = dataResults;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

}
