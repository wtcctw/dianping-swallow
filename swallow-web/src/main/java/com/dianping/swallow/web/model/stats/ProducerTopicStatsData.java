package com.dianping.swallow.web.model.stats;

import java.util.Date;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:57:04
 */
@Document(collection = "ProducerTopicStatsData")
@CompoundIndexes({ @CompoundIndex(name = "timeKey_topicName_index", def = "{'timeKey': 1, 'topicName': -1}") })
public class ProducerTopicStatsData extends ProducerStatsData {

	private String topicName;

	public String getTopicName() {
		return topicName;
	}

	public void setTopicName(String topicName) {
		this.topicName = topicName;
	}

	public boolean checkQpsPeak(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() > expectQps) {
				eventReporter.report(eventFactory.createTopicEvent().setTopicName(topicName)
						.setCurrentValue(this.getQps()).setExpectedValue(expectQps)
						.setStatisType(StatisType.SENDQPS_PEAK).setCreateTime(new Date())
						.setEventType(EventType.PRODUCER));
				return false;
			}
		}
		return true;
	}

	public boolean checkQpsValley(long expectQps) {
		if (this.getQps() != 0L) {
			if (this.getQps() < expectQps) {
				eventReporter.report(eventFactory.createTopicEvent().setTopicName(topicName)
						.setCurrentValue(this.getQps()).setExpectedValue(expectQps)
						.setStatisType(StatisType.SENDQPS_VALLEY).setCreateTime(new Date())
						.setEventType(EventType.PRODUCER));
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

				eventReporter.report(eventFactory.createTopicEvent().setTopicName(topicName)
						.setCurrentValue(this.getQps()).setExpectedValue(preQps).setStatisType(StatisType.SENDQPS_FLU)
						.setCreateTime(new Date()).setEventType(EventType.PRODUCER));
				return false;

			}
		}
		return true;
	}

	public boolean checkDelay(long expectDelay) {
		if (this.getDelay() == 0L || expectDelay == 0L) {
			return true;
		}
		if ((this.getDelay() / 1000) > expectDelay) {
			eventReporter.report(eventFactory.createTopicEvent().setTopicName(topicName)
					.setCurrentValue(this.getDelay()).setExpectedValue(expectDelay).setStatisType(StatisType.SENDDELAY)
					.setCreateTime(new Date()).setEventType(EventType.PRODUCER));
			return false;
		}
		return true;
	}

}
