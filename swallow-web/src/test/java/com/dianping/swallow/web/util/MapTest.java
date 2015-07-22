package com.dianping.swallow.web.util;

import java.util.NavigableMap;
import java.util.TreeMap;

import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年4月21日 下午6:39:47
 */
public class MapTest {

	@Test
	public void testNagMap(){
		
		NavigableMap<Long, String>  map = new TreeMap<Long, String>();
		map.put(2L, "2 v");
		map.put(1L, "1 v");
		map.put(3L, "3 v");

		System.out.println(map.navigableKeySet());
		
		System.out.println(map.keySet());
		
//		System.out.println(map);
//		map.pollFirstEntry();
//		System.out.println(map);

		
	}
}
