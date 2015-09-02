package com.dianping.swallow.web.model.stats;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
/**
 * 
 * @author qiyin
 *
 * 2015年8月24日 上午10:03:32
 */
@Document(collection = "CONSUMER_TOPIC_STATS_DATA")
@CompoundIndexes({ @CompoundIndex(name = "IX_TIMEKEY_TOPICNAME", def = "{'timeKey': 1, 'topicName': -1}") })
public class ConsumerTopicStatsData extends ConsumerStatsData {
	
	private String topicName;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	@Override
	public String toString() {
		return "ConsumerTopicStatsData [topicName=" + topicName + "]"+ super.toString();
	}
	
}
