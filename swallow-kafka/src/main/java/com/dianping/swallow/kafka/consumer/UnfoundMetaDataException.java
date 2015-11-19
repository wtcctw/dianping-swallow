package com.dianping.swallow.kafka.consumer;

import com.dianping.swallow.kafka.TopicAndPartition;
import com.dianping.swallow.kafka.exception.KafkaRuntimeException;

/**
 * @author mengwenchao
 *
 * 2015年11月19日 下午4:14:31
 */
public class UnfoundMetaDataException extends KafkaRuntimeException{
	
	private static final long serialVersionUID = 1L;

	public UnfoundMetaDataException(TopicAndPartition tp){
		super("meta not found:" + tp);
	}

}
