package com.dianping.swallow.web.model.stats;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:57:04
 */
@Document(collection = "PRODUCER_TOPIC_STATS_DATA")
@CompoundIndexes({ @CompoundIndex(name = "IX_TOPICNAME_TIMEKEY", def = "{'topicName': -1, 'timeKey': 1}") })
public class ProducerTopicStatsData extends ProducerStatsData {

	private String topicName;

	private long totalQps;

	private long totalDelay;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public long getTotalQps() {
		return totalQps;
	}

	public void setTotalQps(long totalQps) {
		this.totalQps = totalQps;
	}

	public long getTotalDelay() {
		return totalDelay;
	}

	public void setTotalDelay(long totalDelay) {
		this.totalDelay = totalDelay;
	}

	public boolean checkQpsPeak(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() > expectQps) {
				report(eventFactory.createTopicEvent().setTopicName(topicName), this.getQps(), expectQps,
						StatisType.SENDQPS_PEAK);
				return false;
			}
		}
		return true;
	}

	public boolean checkQpsValley(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() < expectQps) {
				report(eventFactory.createTopicEvent().setTopicName(topicName), this.getQps(), expectQps,
						StatisType.SENDQPS_VALLEY);
				return false;
			}
		}
		return true;
	}

	public boolean checkQpsFlu(long baseQps, long preQps, int flu) {
		if (this.getQps() == 0L || preQps == 0L) {
			return true;
		}

		if (getQps() > baseQps || preQps > baseQps) {

			if ((getQps() >= preQps && (getQps() / preQps > flu)) || (getQps() < preQps && (preQps / getQps() > flu))) {

				report(eventFactory.createTopicEvent().setTopicName(topicName), this.getQps(), preQps,
						StatisType.SENDQPS_FLU);
				return false;
			}
		}
		return true;
	}

	public boolean checkDelay(long expectDelay) {
		long delay = this.getDelay() / 1000;
		if (delay == 0L || expectDelay == 0L) {
			return true;
		}
		if (delay > expectDelay) {
			report(eventFactory.createTopicEvent().setTopicName(topicName), delay, expectDelay, StatisType.SENDDELAY);
			return false;
		}
		return true;
	}

	public void setTotalStatsDatas(ProducerTopicStatsData lastStatsData, int sampleInterval) {
		long currQps = this.getQpsTotal();
		if (lastStatsData != null) {
			this.totalQps = lastStatsData.getTotalQps() + currQps;
			this.totalDelay = lastStatsData.getTotalDelay() + this.getDelay() * currQps;
		} else {
			this.totalQps = currQps;
			this.totalDelay = this.getDelay() * currQps;
		}
	}

	@Override
	public String toString() {
		return "ProducerTopicStatsData [topicName=" + topicName + ", totalQps=" + totalQps + ", totalDelay="
				+ totalDelay + "]" + super.toString();
	}

}
