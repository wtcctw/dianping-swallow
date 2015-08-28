package com.dianping.swallow.web.dashboard.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * @author mingdongli
 *
 *         2015年8月13日上午11:47:30
 */
public class FixSizedPriorityQueue {

	private PriorityBlockingQueue<Entry> queue;
	
	private Date time;

	private Comparator<Entry> comparator;

	private int maxSize;

	public FixSizedPriorityQueue(int maxSize, Comparator<Entry> comparator) {
		if (maxSize <= 0)
			throw new IllegalArgumentException();
		this.maxSize = maxSize;
		this.comparator = comparator;
		this.queue = new PriorityBlockingQueue<Entry>(maxSize, comparator);
	}

	public synchronized void add(Entry e) {
		if (queue.size() < maxSize) {
			queue.add(e);
		} else {
			Entry peek = queue.peek();
			if (comparator.compare(peek, e) < 0) {
				queue.poll();
				queue.add(e);
			}
		}
	}

	public List<Entry> sortedList() {
		List<Entry> list = new ArrayList<Entry>(queue);
		Collections.sort(list, comparator);
		Collections.reverse(list);
		return list;
	}

	public PriorityBlockingQueue<Entry> getQueue() {
		return queue;
	}

	public Comparator<Entry> getComparator() {
		return comparator;
	}

	@JsonProperty
	public void setComparator(Comparator<Entry> comparator) {
		this.comparator = comparator;
	}
	
	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

}
