package com.dianping.swallow.web.model.stats;


import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 
 * @author qiyin
 *
 *         2015年9月6日 上午9:46:03
 */
@Document(collection = "CONSUMER_IP_STATS_DATA")
@CompoundIndexes({ @CompoundIndex(name = "IX_TOPICNAME_CONSUMERID_IP_TIMEKEY", def = "{'topicName':-1, 'consumerId': -1, 'ip': -1, 'timeKey': 1}") })
public class ConsumerIpStatsData extends ConsumerStatsData {

	private String topicName;

	private String consumerId;

	private String ip;

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

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((topicName == null) ? 0 : topicName.hashCode());
		result = prime * result + ((consumerId == null) ? 0 : consumerId.hashCode());
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
		ConsumerIpStatsData other = (ConsumerIpStatsData) obj;
		if (topicName == null) {
			if (other.topicName != null)
				return false;
		} else if (!topicName.equals(other.topicName))
			return false;
		if (consumerId == null) {
			if (other.consumerId != null)
				return false;
		} else if (!consumerId.equals(other.consumerId))
			return false;
		if (ip == null) {
			if (other.ip != null)
				return false;
		} else if (!ip.equals(other.ip))
			return false;
		return true;
	}

	public boolean checkStatsData() {
		if (this.getSendQps() == 0L && this.getAckQps() == 0L) {
			return false;
		}
		return true;
	}
	
	public boolean hasStatsData(){
		if (this.getSendQps() == 0L && this.getAckQps() == 0L) {
			return false;
		}
		return true;
	}

}
