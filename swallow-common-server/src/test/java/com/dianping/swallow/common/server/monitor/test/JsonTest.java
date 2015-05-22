package com.dianping.swallow.common.server.monitor.test;

import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;


/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午6:07:24
 */
public class JsonTest {
	
	
	@Test
	public void testSerialize(){
		
		HashMap<String, String> test = new HashMap<String, String>();
		test.put("1", "1");
		test.put("2", "2");
		
		System.out.println(test);
		new HashSet<String>(test.keySet()).remove("1");
		System.out.println(test);
		
		
	}

}
