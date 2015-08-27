package com.dianping.swallow.web.monitor.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;

/**
 * 
 * @author qiyin
 *
 *         2015年8月27日 上午11:36:54
 */
@Document(collection = "PRODUCER_RE_TOPIC_STATS_DATA")
public class ProducerTopicReStatsData extends ReStatsData {

	private String topicName;

	private long qps;

	private long delay;

	public long getQps() {
		return qps;
	}

	public void setQps(long qps) {
		this.qps = qps;
	}

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public static ProducerTopicReStatsData createEntity(ProducerTopicStatsData statsData) {
		ProducerTopicReStatsData producerTopicReStatsData = new ProducerTopicReStatsData();
		producerTopicReStatsData.setFromTimeKey(statsData.getTimeKey());
		producerTopicReStatsData.setToTimeKey(statsData.getTimeKey());
		producerTopicReStatsData.setTopicName(statsData.getTopicName());
		producerTopicReStatsData.setDelay(statsData.getDelay());
		producerTopicReStatsData.setQps(statsData.getQps());
		return producerTopicReStatsData;
	}

	public static ProducerTopicReStatsData updateEntity(ProducerTopicReStatsData reStatsData,
			ProducerTopicStatsData statsData) {
		reStatsData.setToTimeKey(statsData.getTimeKey());
		reStatsData.setDelay(reStatsData.getDelay() + statsData.getDelay());
		reStatsData.setQps(reStatsData.getQps() + statsData.getQps());
		return reStatsData;
	}

}
