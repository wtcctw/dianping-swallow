package com.dianping.swallow.web.model.stats;

import com.dianping.swallow.web.model.event.StatisEvent;

/**
 * 
 * @author qiyin
 *
 *         2015年10月19日 下午7:21:31
 */
public abstract class AbstractIpStatsData extends StatsData {
	
	private String ip;

	private String topicName;
	
	@Override
	public StatisEvent createEvent() {
		throw new UnsupportedOperationException();
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}
	
	public abstract boolean hasStatsData();

	public abstract boolean hasStatsData(long qpsThreshold,long totalThreshold);
	
	public abstract IpStatsDataKey createStatsDataKey();
	
	public static abstract class IpStatsDataKey {

		public IpStatsDataKey(AbstractIpStatsData ipStatsData) {
			this.topicName = ipStatsData.getTopicName();
			this.ip = ipStatsData.getIp();
		}

		private String topicName;

		private String ip;

		public String getTopicName() {
			return topicName;
		}

		public String getIp() {
			return ip;
		}

		@Override
		public String toString() {
			return "AbstractIpStatsDataKey [topicName=" + topicName + ", ip=" + ip + "]";
		}

	}

}
