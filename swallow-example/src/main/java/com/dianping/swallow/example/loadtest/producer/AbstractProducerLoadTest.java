package com.dianping.swallow.example.loadtest.producer;


import com.dianping.swallow.example.loadtest.AbstractLoadTest;

/**
 * @author mengwenchao
 *
 * 2015年1月26日 下午10:00:50
 */
public abstract class AbstractProducerLoadTest extends AbstractLoadTest{
	
	
    public static final int messageSize = 1000;
    public static final String message;
    

	static {
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<messageSize;i++){
			sb.append("c");
		}
		message = sb.toString();
	}	
	
}
