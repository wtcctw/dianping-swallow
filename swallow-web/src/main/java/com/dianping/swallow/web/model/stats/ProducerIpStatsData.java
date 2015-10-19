package com.dianping.swallow.web.model.stats;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.StatisEvent;

/**
 * 
 * @author qiyin
 *
 *         2015年9月6日 上午9:45:54
 */
@Document(collection = "PRODUCER_IP_STATS_DATA")
@CompoundIndexes({ @CompoundIndex(name = "IX_TOPICNAME_IP_TIMEKEY", def = "{'topicName': -1, 'ip': -1, 'timeKey': 1}") })
public class ProducerIpStatsData extends ProducerStatsData {

	private String ip;

	private String topicName;

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

	public boolean checkPreStatsData(ProducerIpStatsData statsData) {
		if (this.getQps() == 0L && this.getDelay() == 0L) {
			if (statsData.getQps() != 0L || statsData.getDelay() != 0L) {
				return false;
			}
		}
		return true;
	}
		
	public boolean hasStatsData() {
		if (this.getQps() == 0L && this.getQpsTotal() == 0L) {
			return false;
		}
		return true;
	}

	@Override
	public StatisEvent createEvent() {
		throw new UnsupportedOperationException();
	}
}
