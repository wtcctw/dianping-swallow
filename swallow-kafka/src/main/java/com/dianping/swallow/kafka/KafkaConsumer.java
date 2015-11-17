package com.dianping.swallow.kafka;

import java.util.List;



/**
 * 使用SimpleApi构造适合Swallow的Consumer
 * @author mengwenchao
 *
 * 2015年11月16日 下午5:23:51
 */
public interface KafkaConsumer {

	List<KafkaMessage> getMessageGreatThan(TopicAndPartition topicAndPartition, Long offset, int fetchSize);

	List<KafkaMessage> getMessageGreatThan(TopicAndPartition topicAndPartition, Long offset);
	
	Long getMaxMessageId(TopicAndPartition tp);

	Long getMinMessageId(TopicAndPartition tp);

	void saveAck(TopicAndPartition tp, String groupId, Long ack);
	
	Long getAck(TopicAndPartition tp, String groupId);

}
