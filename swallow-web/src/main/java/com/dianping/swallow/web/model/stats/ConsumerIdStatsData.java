package com.dianping.swallow.web.model.stats;

import java.util.Date;

import org.springframework.context.annotation.Scope;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.event.ConsumerIdEvent;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.event.StatisType;

/**
 * 
 * @author qiyin
 *
 *         2015年7月31日 下午3:56:31
 */
@Service
@Scope("prototype")
@Document(collection = "ConsumerIdStatsData")
@CompoundIndexes({ @CompoundIndex(name = "timeKey_topicName_consumerId_index", def = "{'timeKey': 1, 'topicName': -1, 'consumerId': -1}") })
public class ConsumerIdStatsData extends ConsumerStatsData {

	private String topicName;

	private String consumerId;

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
	public String toString() {
		return "ConsumerIdStatsData [topicName=" + topicName + ", consumerId=" + consumerId + "]";
	}

	public boolean checkSendQps(long expectQps, long baseQps, long preQps, int flu) {
		if (!checkQpsPeak(expectQps, StatisType.SENDQPS_PEAK)) {
			return false;
		}
		if (!checkQpsValley(expectQps, StatisType.SENDQPS_VALLEY)) {
			return false;
		}
		if (!checkQpsFlu(baseQps, preQps, flu, StatisType.SENDQPS_FLU)) {
			return false;
		}
		return true;
	}

	public boolean checkSendDelay(long expectDelay) {
		return checkDelay(expectDelay, StatisType.SENDDELAY);
	}

	public boolean checkSendAccu(long expectAccu) {
		return checkAccu(expectAccu, StatisType.SENDACCU);
	}

	public boolean checkAckQps(long expectQps, long baseQps, long preQps, int flu) {
		if (!checkQpsPeak(expectQps, StatisType.ACKQPS_PEAK)) {
			return false;
		}
		if (!checkQpsValley(expectQps, StatisType.ACKQPS_VALLEY)) {
			return false;
		}
		if (!checkQpsFlu(baseQps, preQps, flu, StatisType.ACKQPS_FLU)) {
			return false;
		}
		return true;
	}

	public boolean checkAckDelay(long expectDelay) {
		return checkDelay(expectDelay, StatisType.ACKDELAY);
	}
	
	private boolean checkQpsPeak(long expectQps, StatisType statisType) {
		if (this.getSendQps() != 0L) {
			if (this.getSendQps() > expectQps) {
				eventReporter.report(new ConsumerIdEvent().setConsumerId(consumerId).setTopicName(topicName)
						.setCurrentValue(this.getSendQps()).setExpectedValue(expectQps).setStatisType(statisType)
						.setCreateTime(new Date()).setEventType(EventType.CONSUMER));
				return false;
			}
		}
		return true;
	}

	private boolean checkQpsValley(long expectQps, StatisType statisType) {
		if (this.getSendQps() != 0L) {
			if (this.getSendQps() < expectQps) {
				eventReporter.report(new ConsumerIdEvent().setConsumerId(consumerId).setTopicName(topicName)
						.setCurrentValue(this.getSendQps()).setExpectedValue(expectQps).setStatisType(statisType)
						.setCreateTime(new Date()).setEventType(EventType.CONSUMER));
				return false;
			}
		}
		return true;
	}

	private boolean checkQpsFlu(long baseQps, long preQps, int flu, StatisType statisType) {
		if (this.getSendQps() == 0L || preQps == 0L) {
			return true;
		}

		if (getSendQps() > baseQps || preQps > baseQps) {

			if ((getSendQps() >= preQps && (getSendQps() / preQps > flu))
					|| (getSendQps() < preQps && (preQps / getSendQps() > flu))) {

				eventReporter.report(new ConsumerIdEvent().setConsumerId(consumerId).setTopicName(topicName)
						.setCurrentValue(this.getSendQps()).setExpectedValue(preQps).setStatisType(statisType)
						.setCreateTime(new Date()).setEventType(EventType.CONSUMER));
				return false;

			}
		}
		return true;
	}

	private boolean checkDelay(long expectDelay, StatisType statisType) {
		if (this.getSendDelay() != 0L) {
			if (this.getSendDelay() > expectDelay) {
				eventReporter.report(new ConsumerIdEvent().setConsumerId(consumerId).setTopicName(topicName)
						.setCurrentValue(this.getSendDelay()).setExpectedValue(expectDelay).setStatisType(statisType)
						.setCreateTime(new Date()).setEventType(EventType.CONSUMER));
				return false;
			}
		}
		return true;
	}

	private boolean checkAccu(long expectAccu, StatisType statisType) {
		if (this.getSendDelay() != 0L) {
			if (this.getAccumulation() > expectAccu) {
				eventReporter.report(new ConsumerIdEvent().setConsumerId(consumerId).setTopicName(topicName)
						.setCurrentValue(this.getAccumulation()).setExpectedValue(expectAccu).setStatisType(statisType)
						.setCreateTime(new Date()).setEventType(EventType.CONSUMER));
				return false;
			}
		}
		return true;
	}

}
