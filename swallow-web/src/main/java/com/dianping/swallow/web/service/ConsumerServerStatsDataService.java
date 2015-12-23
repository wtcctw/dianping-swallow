package com.dianping.swallow.web.service;

import java.util.List;
import java.util.Map;
import java.util.NavigableMap;

import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午2:40:15
 */
public interface ConsumerServerStatsDataService extends StatsDataService{

	boolean insert(ConsumerServerStatsData serverStatsData);
	
	boolean insert(List<ConsumerServerStatsData> serverStatsDatas);

	List<ConsumerServerStatsData> findSectionData(String ip, long startKey, long endKey);

	Map<String, StatsDataMapPair> findSectionQpsData(long startKey, long endKey);
	
	long findQpsByServerIp(String ip, long startKey, long endKey);

	public static class StatsDataMapPair {
		
		private NavigableMap<Long, Long> sendStatsData;
		
		private NavigableMap<Long, Long> ackStatsData;

		public NavigableMap<Long, Long> getSendStatsData() {
			return sendStatsData;
		}

		public void setSendStatsData(NavigableMap<Long, Long> sendStatsData) {
			this.sendStatsData = sendStatsData;
		}

		public NavigableMap<Long, Long> getAckStatsData() {
			return ackStatsData;
		}

		public void setAckStatsData(NavigableMap<Long, Long> ackStatsData) {
			this.ackStatsData = ackStatsData;
		}
		
	}
}
