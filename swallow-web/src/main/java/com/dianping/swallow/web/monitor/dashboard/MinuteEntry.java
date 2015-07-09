package com.dianping.swallow.web.monitor.dashboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author mingdongli
 *
 * 2015年7月7日上午9:36:34
 */
public class MinuteEntry {

	private String time;

	List<Entry> delayEntry = new ArrayList<Entry>(); //cid
	
	private static final int ENTRYSIZE = 12;
	
	public MinuteEntry() {

	}

	public String getTime() {
		return time;
	}

	public MinuteEntry setTime(String time) {
		this.time = time;
		return this;
	}

	public List<Entry> getDelayEntry() {
		return delayEntry;
	}

	public MinuteEntry setDelayEntry(List<Entry> delayEntry) {
		this.delayEntry = delayEntry;
		return this;
	}
	
	public MinuteEntry addEntry(Entry entry){
		
		int entrySize = delayEntry.size();
		if(entrySize >= ENTRYSIZE){
			if(entry.getNumAlarm() > delayEntry.get(entrySize - 1).getNumAlarm()){
				delayEntry.remove(entrySize - 1);
				delayEntry.add(entry);
				Collections.sort(delayEntry);
			}
			return this;
		}
		delayEntry.add(entry);
		Collections.sort(delayEntry);
		return this;
	}

	public static void main(String[] args) {
		
		MinuteEntry me = new MinuteEntry();
		Entry e1 = new Entry();
		e1.setNumAlarm(3);
		Entry e2 = new Entry();
		e2.setNumAlarm(2);
		Entry e3 = new Entry();
		e3.setNumAlarm(1);
		Entry e4 = new Entry();
		e4.setNumAlarm(0);
		Entry e5 = new Entry();
		e5.setNumAlarm(1);
		me.addEntry(e1);
		me.addEntry(e2);
		me.addEntry(e3);
		me.addEntry(e4);
		me.addEntry(e5);
		List<Entry> entryList = me.getDelayEntry();
		Collections.sort(entryList);
		for(int i = 0; i < entryList.size(); ++i){
			System.out.println(entryList.get(i));
		}
	}
}

