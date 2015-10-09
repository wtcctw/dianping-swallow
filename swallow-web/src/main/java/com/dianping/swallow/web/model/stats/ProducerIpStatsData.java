package com.dianping.swallow.web.model.stats;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
		result = prime * result + ((ip == null) ? 0 : ip.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProducerIpStatsData other = (ProducerIpStatsData) obj;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		return true;
	}

	public boolean checkPreStatsData(ProducerIpStatsData statsData) {
		if (this.getQps() == 0L && this.getDelay() == 0L) {
			if (statsData.getQps() != 0L || statsData.getDelay() != 0L) {
				return false;
			}
		}
		return true;
	}

	public boolean checkStatsData() {
		if (this.getQps() == 0L) {
			return false;
		}
		return true;
	}

	public boolean hasStatsData() {
		if (this.getQps() <= 0L) {
			return false;
		}
		return true;
	}

	public boolean checkGroupStatsData(List<ProducerIpStatsData> statsDatas) {
		if (this.getQps() == 0L) {
			if (statsDatas != null && statsDatas.size() > 0) {
				for (ProducerIpStatsData statsData : statsDatas) {
					if (StringUtils.equals(statsData.getIp(), this.ip)) {
						continue;
					} else {
						if (statsData.getQps() != 0L) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

}
