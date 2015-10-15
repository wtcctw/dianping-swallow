package com.dianping.swallow.web.container;

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
public interface ResourceContainer {

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

	List<ConsumerServerResourcePair> findConsumerServerResourcePairs();

	public static class ConsumerServerResourcePair {

		private ConsumerServerResource masterResource;

		private ConsumerServerResource slaveResource;

		public ConsumerServerResourcePair() {

		}

		public ConsumerServerResourcePair(ConsumerServerResource masterResource, ConsumerServerResource slaveResource) {
			this.masterResource = masterResource;
			this.slaveResource = slaveResource;
		}

		public ConsumerServerResource getMasterResource() {
			return masterResource;
		}

		public void setMasterResource(ConsumerServerResource masterResource) {
			this.masterResource = masterResource;
		}

		public ConsumerServerResource getSlaveResource() {
			return slaveResource;
		}

		public void setSlaveResource(ConsumerServerResource slaveResource) {
			this.slaveResource = slaveResource;
		}

		@Override
		public String toString() {
			return "ConsumerServerResourcePair [masterResource=" + masterResource + ", slaveResource=" + slaveResource
					+ "]";
		}
		
	}
}
