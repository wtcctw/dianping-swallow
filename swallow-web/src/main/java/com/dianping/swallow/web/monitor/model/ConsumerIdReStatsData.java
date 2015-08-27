package com.dianping.swallow.web.monitor.model;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 上午11:37:02
 */
@Document(collection = "CONSUMERID_RE_STATS_DATA")
public class ConsumerIdReStatsData extends ReStatsData {

	@Indexed(name = "IX_CONSUMERID")
	private String consumerId;

	@Indexed(name = "IX_TOPICNAME")
	private String topicName;

	private long sendQps;

	private long sendDelay;

	private long ackQps;

	private long ackDelay;

	private long accumulation;

	public String getConsumerId() {
		return consumerId;
	}

	public void setConsumerId(String consumerId) {
		this.consumerId = consumerId;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public long getSendQps() {
		return sendQps;
	}

	public void setSendQps(long sendQps) {
		this.sendQps = sendQps;
	}

	public long getSendDelay() {
		return sendDelay;
	}

	public void setSendDelay(long sendDelay) {
		this.sendDelay = sendDelay;
	}

	public long getAckQps() {
		return ackQps;
	}

	public void setAckQps(long ackQps) {
		this.ackQps = ackQps;
	}

	public long getAckDelay() {
		return ackDelay;
	}

	public void setAckDelay(long ackDelay) {
		this.ackDelay = ackDelay;
	}

	public long getAccumulation() {
		return accumulation;
	}

	public void setAccumulation(long accumulation) {
		this.accumulation = accumulation;
	}

	@Override
	public String toString() {
		return "ConsumerIdReStatsData [consumerId=" + consumerId + ", topicName=" + topicName + ", sendQps=" + sendQps
				+ ", sendDelay=" + sendDelay + ", ackQps=" + ackQps + ", ackDelay=" + ackDelay + ", accumulation="
				+ accumulation + "]";
	}

	public static ConsumerIdReStatsData createEntity(ConsumerIdStatsData statsData) {
		ConsumerIdReStatsData consumerIdReStatsData = new ConsumerIdReStatsData();
		consumerIdReStatsData.setTopicName(statsData.getTopicName());
		consumerIdReStatsData.setConsumerId(statsData.getConsumerId());
		consumerIdReStatsData.setSendDelay(statsData.getSendDelay());
		consumerIdReStatsData.setAckDelay(statsData.getAckDelay());
		consumerIdReStatsData.setSendQps(statsData.getSendQps());
		consumerIdReStatsData.setAckQps(statsData.getAckQps());
		consumerIdReStatsData.setAccumulation(statsData.getAccumulation());
		return consumerIdReStatsData;
	}

	public static ConsumerIdReStatsData updateEntity(ConsumerIdReStatsData reStatsData, ConsumerIdStatsData statsData) {
		reStatsData.setToTimeKey(statsData.getTimeKey());
		reStatsData.setSendDelay(reStatsData.getSendDelay() + statsData.getSendDelay());
		reStatsData.setAckDelay(reStatsData.getAckDelay() + statsData.getAckDelay());
		reStatsData.setSendQps(reStatsData.getSendQps() + statsData.getSendQps());
		reStatsData.setAckQps(reStatsData.getAckQps() + statsData.getAckQps());
		reStatsData.setAccumulation(reStatsData.getAccumulation() + statsData.getAccumulation());
		return reStatsData;
	}

}
