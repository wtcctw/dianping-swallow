package com.dianping.swallow.test;

import java.util.Date;

/**
 * @author mengwenchao
 *
 * 2015年2月26日 下午3:49:31
 */
public class IdToTime {
	
	
	
	public static void main(String []argc){

		long id = 6120056125938212942L;
		
		if(argc.length >= 1){
			id = Long.parseLong(argc[0]);
		}
		
		new IdToTime().makeTime(id);
		
		
	}

	private void makeTime(long id) {
		
		System.out.println(new Date((id >> 32) * 1000));
	}

}
