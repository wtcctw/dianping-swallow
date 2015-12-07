package com.dianping.swallow.web.service;

import java.util.List;

import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年9月15日 下午5:39:55
 */
public interface ConsumerIpStatsDataService {

	boolean insert(ConsumerIpStatsData ipStatsData);
	
	boolean insert(List<ConsumerIpStatsData> ipStatsDatas);

	boolean removeLessThanTimeKey(long timeKey);

	List<ConsumerIpStatsData> find(String topicName, String consumerId, String ip, long startKey, long endKey);

	ConsumerIpQpsPair findAvgQps(String topicName, String consumerId, String ip, long startKey, long endKey);

	public static class ConsumerIpQpsPair {

		public ConsumerIpQpsPair(long sendQps, long ackQps) {
			this.sendQps = sendQps;
			this.ackQps = ackQps;
		}

		private long sendQps;

		private long ackQps;

		public long getSendQps() {
			return sendQps;
		}

		public void setSendQps(long sendQps) {
			this.sendQps = sendQps;
		}

		public long getAckQps() {
			return ackQps;
		}

		public void setAckQps(long ackQps) {
			this.ackQps = ackQps;
		}
	}
}
