package com.dianping.swallow.web.dao;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;

import java.util.List;


/**
 * @author mingdongli
 *
 * 2015年8月11日上午10:07:00
 */
public interface ConsumerIdResourceDao extends Dao{

	boolean insert(ConsumerIdResource consumerIdResource);

	boolean update(ConsumerIdResource consumerIdResource);
	
	int remove(String topic, String consumerid);
	
	long count();

	ConsumerIdResource findByConsumerIdAndTopic(String topic, String consumerId);

	Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdParam consumerIdParam);

	List<ConsumerIdResource> findByTopic(String topic);

	Pair<Long, List<ConsumerIdResource>> find(ConsumerIdParam  consumerIdParam);
	
	List<ConsumerIdResource> findAll(String ...fields );

	ConsumerIdResource findDefault();
	
	Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdParam consumerIdParam);
	
	long countInactive();
	
	public static class ConsumerIdParam{
		
		private int offset;
		
		private int limit;
		
		private String consumerId;
		
		private String topic;
		
		private String consumerIp;
		
		private boolean inactive = true;

		public int getOffset() {
			return offset;
		}

		public void setOffset(int offset) {
			this.offset = offset;
		}

		public int getLimit() {
			return limit;
		}

		public void setLimit(int limit) {
			this.limit = limit;
		}

		public String getConsumerId() {
			return consumerId;
		}

		public void setConsumerId(String consumerId) {
			this.consumerId = consumerId;
		}

		public String getTopic() {
			return topic;
		}

		public void setTopic(String topic) {
			this.topic = topic;
		}

		public String getConsumerIp() {
			return consumerIp;
		}

		public void setConsumerIp(String consumerIp) {
			this.consumerIp = consumerIp;
		}

		public boolean isInactive() {
			return inactive;
		}

		public void setInactive(boolean inactive) {
			this.inactive = inactive;
		}
		
	}
}
