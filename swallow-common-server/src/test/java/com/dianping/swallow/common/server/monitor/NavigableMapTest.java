package com.dianping.swallow.common.server.monitor;

import java.util.NavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年5月19日 下午6:03:17
 */
public class NavigableMapTest {
	
	@Test
	public void deleteBefore(){
		
		NavigableMap<Long, String>  map = new ConcurrentSkipListMap<Long, String>();
		for(int i=0;i<10;i++){
			map.put((long) i, String.valueOf(i));
		}
		
		System.out.println("first:" + map.pollFirstEntry());
		System.out.println("last:" + map.pollLastEntry());
		
		System.out.println(map.headMap(-1L));
		
	}

}
