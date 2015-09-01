package com.dianping.swallow.web.dao;

import java.util.List;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;


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

	List<ConsumerIdResource> findByConsumerId(String consumerid);
	
	ConsumerIdResource findByConsumerIdAndTopic(String topic, String consumerId);

	Pair<Long, List<ConsumerIdResource>> findByTopic(ConsumerIdParam consumerIdParam);
	
	Pair<Long, List<ConsumerIdResource>> findByConsumerIp(ConsumerIdParam consumerIdParam);

	Pair<Long, List<ConsumerIdResource>> find(ConsumerIdParam  consumerIdParam);
	
	List<ConsumerIdResource> findAll(String ...fields );

	ConsumerIdResource findDefault();
	
	Pair<Long, List<ConsumerIdResource>> findConsumerIdResourcePage(ConsumerIdParam consumerIdParam);
	
	public static class ConsumerIdParam{
		
		private int offset;
		
		private int limit;
		
		private String consumerId;
		
		private String topic;
		
		private String consumerIp;

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
		
	}
}
