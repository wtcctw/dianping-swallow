package com.dianping.swallow.web.model.dashboard;

import java.util.Date;

import com.dianping.swallow.web.common.MaxHeap;



/**
 * @author mingdongli
 *
 * 2015年7月7日上午9:36:34
 */
public class MinuteEntry {

	private Date time;

	MaxHeap<Entry> delayEntry;
	
	public MinuteEntry() {
		
		Entry[] entry = new Entry[MaxHeap.DEFAULT_MAX];
		delayEntry = new MaxHeap<Entry>(entry);
	}

	public Date getTime() {
		
		return time;
	}

	public MinuteEntry setTime(Date time) {
		
		this.time = time;
		return this;
	}

	
	public MaxHeap<Entry> getDelayEntry() {
		return delayEntry;
	}

	public MinuteEntry setDelayEntry(MaxHeap<Entry> delayEntry) {
		
		this.delayEntry = delayEntry;
		return this;
	}

	public boolean addEntry(Entry entry){
		
		return delayEntry.insert(entry);
	}

}

