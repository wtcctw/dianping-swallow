package com.dianping.swallow.kafka.exception;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:43:47
 */
public class KafkaException extends Exception{
	
	private static final long serialVersionUID = 1L;

	public KafkaException(String message){
		super(message);
		
	}
	
	public KafkaException(String message, Throwable th){
		super(message, th);
	}

}
