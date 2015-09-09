package com.dianping.swallow.web.alarmer.container;

import java.util.List;

import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.resource.TopicResource;

/**
 * 
 * @author qiyin
 *
 *         2015年8月17日 下午5:49:38
 */
public interface AlarmResourceContainer {

	ConsumerServerResource findConsumerServerResource(String ip);

	ProducerServerResource findProducerServerResource(String ip);

	TopicResource findTopicResource(String topic);

	ConsumerIdResource findConsumerIdResource(String topicName, String consumerId);

	List<ConsumerServerResource> findConsumerServerResources(boolean isDefault);

	List<ProducerServerResource> findProducerServerResources(boolean isDefault);

	List<TopicResource> findTopicResources(boolean isDefault);

	List<ConsumerIdResource> findConsumerIdResources(boolean isDefault);

	List<ConsumerServerResource> findConsumerMasterServerResources();

	List<ConsumerServerResource> findConsumerSlaveServerResources();
}
