package com.dianping.swallow.common.server.monitor.test;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import org.junit.Test;

import com.dianping.swallow.common.internal.codec.JsonBinder;


/**
 * @author mengwenchao
 *
 * 2015年5月20日 下午6:07:24
 */
public class JsonTest {
	
	
	@Test
	public void testSerialize(){
		
		HashMap<String, String> test = new HashMap<String, String>();
		
		for(int i=0;i<100;i++){
			test.put(String.valueOf(i), "value" + i);
		}
		
		new HashSet<String>(test.keySet()).remove("1");
		
		System.out.println(JsonBinder.getNonEmptyBinder().toJson(test));
		System.out.println(JsonBinder.getNonEmptyBinder().toPrettyJson(test));
		
	}
	
	@Test
	public void testSimple(){
		
		
		System.out.println(new Date(286542684L*5*1000));
		int count = 3*3600/5;
		System.out.println(count * 3600000 );
		System.out.println(System.currentTimeMillis() - count * 3600000);
		
	}

}
