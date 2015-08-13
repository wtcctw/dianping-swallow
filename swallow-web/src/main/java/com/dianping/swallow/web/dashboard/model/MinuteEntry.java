package com.dianping.swallow.web.dashboard.model;

import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;



/**
 * @author mingdongli
 *
 * 2015年7月7日上午9:36:34
 */
public class MinuteEntry {

	@Indexed
	private Date time;

	MinHeap delayEntry;
	
	public MinuteEntry() {
		
		delayEntry = new MinHeap();
	}

	public Date getTime() {
		
		return time;
	}

	public MinuteEntry setTime(Date time) {
		
		this.time = time;
		return this;
	}

	
	public MinHeap getDelayEntry() {
		return delayEntry;
	}

	public MinuteEntry setDelayEntry(MinHeap delayEntry) {
		
		this.delayEntry = delayEntry;
		return this;
	}

	public boolean addEntry(Entry entry){
		
		return delayEntry.insert(entry);
	}

}

