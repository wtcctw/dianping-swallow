package com.dianping.swallow.common.message;

import java.io.UnsupportedEncodingException;

import org.junit.Test;

/**
 * @author mengwenchao
 *
 * 2015年7月8日 下午2:05:15
 */
public class DestinationTest {
	
	
	@Test
	public void testDestination() throws UnsupportedEncodingException{
		
		String str = "payPlan_create_notifyy​";
//		Destination.topic(str);
//		String str = "pay";
		
		
		for(byte b : str.getBytes()){
			
			int data = b;
			System.out.print((char)b);
			System.out.println(" " + b);
		}
		
//		Destination.topic("​");
	}

}
