package com.dianping.swallow.kafka.exception;

/**
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:43:47
 */
public class KafkaRuntimeException extends RuntimeException{
	
	private static final long serialVersionUID = 1L;

	public KafkaRuntimeException(String message){
		super(message);
		
	}
	
	public KafkaRuntimeException(String message, Throwable th){
		super(message, th);
	}

}
