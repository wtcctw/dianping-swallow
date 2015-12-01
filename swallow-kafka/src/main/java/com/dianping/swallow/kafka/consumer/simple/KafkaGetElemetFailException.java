package com.dianping.swallow.kafka.consumer.simple;

import java.net.InetSocketAddress;

import com.dianping.swallow.kafka.exception.KafkaRuntimeException;

/**
 * @author mengwenchao
 *
 * 2015年11月27日 下午10:16:39
 */
public class KafkaGetElemetFailException extends KafkaRuntimeException{

	private static final long serialVersionUID = 1L;

	public KafkaGetElemetFailException(InetSocketAddress addr, Throwable th) {
		super(addr.toString() + " get failed", th);
	}

}
