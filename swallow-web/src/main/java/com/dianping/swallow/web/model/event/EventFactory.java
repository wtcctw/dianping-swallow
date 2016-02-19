package com.dianping.swallow.web.model.event;

import com.dianping.swallow.web.monitor.jmx.event.BrokerKafkaEvent;
import com.dianping.swallow.web.monitor.jmx.event.ControllerKafkaEvent;
import com.dianping.swallow.web.monitor.jmx.event.UnderReplicaEvent;

/**
 * 
 * @author qiyin
 *
 *         2015年10月19日 上午9:34:21
 */
public interface EventFactory {

	TopicEvent createTopicEvent();

	ServerEvent createServerEvent();

	ServerStatisEvent createServerStatisEvent();

	ConsumerIdEvent createConsumerIdEvent();

	ConsumerClientEvent createCClientEvent();

	ProducerClientEvent createPClientEvent();

	MongoConfigEvent createMongoConfigEvent();

	BrokerKafkaEvent createBrokerKafkaEvent();

	ControllerKafkaEvent createControllerKafkaEvent();

	UnderReplicaEvent createUnderReplicaEvent();

}
