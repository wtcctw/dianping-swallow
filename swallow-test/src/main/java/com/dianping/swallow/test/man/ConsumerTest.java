package com.dianping.swallow.test.man;

import com.dianping.swallow.consumer.ConsumerFactory;
import com.dianping.swallow.consumer.impl.ConsumerFactoryImpl;

/**
 * @author mengwenchao
 *
 * 2015年12月18日 下午5:33:29
 */
public class ConsumerTest {
	
	
	
	public static void main(String []argc){

		String topic = System.getProperty("topic", "pay_movie_ticket");
		
		ConsumerFactory factory = ConsumerFactoryImpl.getInstance();
		
		System.out.println(factory.getTopicAddress(topic));
		
		
	}

}
