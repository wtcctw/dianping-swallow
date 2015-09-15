package com.dianping.swallow.web.model.stats;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
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
@CompoundIndexes({ @CompoundIndex(name = "IX_TIMEKEY_TOPICNAME_CONSUMERID_IP", def = "{'timeKey': 1, 'topicName':-1, 'consumerId': -1, 'ip': -1}") })
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

	public boolean checkPreStatsData(ConsumerIpStatsData statsData) {
		if (this.getSendQps() == 0L && this.getAckQps() == 0L && this.getSendDelay() == 0L && this.getAckDelay() == 0L) {
			if (this.getAccumulation() > 0) {
				return false;
			} else {
				if (statsData.getSendQps() != 0L || statsData.getAckQps() != 0L || statsData.getSendDelay() != 0L
						|| statsData.getAckDelay() != 0L) {
					return false;
				}
			}
		}
		return true;
	}

	public boolean checkGroupStatsData(List<ConsumerIpStatsData> statsDatas) {
		if (this.getSendQps() == 0L && this.getAckQps() == 0L && this.getSendDelay() == 0L && this.getAckDelay() == 0L) {
			if (statsDatas != null && statsDatas.size() > 0) {
				for (ConsumerIpStatsData statsData : statsDatas) {
					if (StringUtils.equals(statsData.getIp(), this.ip)) {
						continue;
					} else {
						if (statsData.getSendQps() != 0L || statsData.getAckQps() != 0L
								|| statsData.getSendDelay() != 0L || statsData.getAckDelay() != 0L) {
							return false;
						}
					}
				}
			}
		}
		return true;
	}

}
