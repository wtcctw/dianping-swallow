package com.dianping.swallow.web.model.event;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.codehaus.plexus.util.StringUtils;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.RelatedType;
import com.dianping.swallow.web.util.DateUtil;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:13:54
 */
public class TopicEvent extends StatisEvent {

	private static final Map<String, Long> lastAlarms = new ConcurrentHashMap<String, Long>();

	private String topicName;

	public String getTopicName() {
		return topicName;
	}

	public TopicEvent setTopicName(String topicName) {
		this.topicName = topicName;
		return this;
	}

	@Override
	public void alarm() {
		switch (getEventType()) {
		case CONSUMER:
			switch (getStatisType()) {
			case SENDQPS_PEAK:
				sendMessage(AlarmType.CONSUMER_TOPIC_SENDQPS_PEAK);
				break;
			case SENDQPS_VALLEY:
				sendMessage(AlarmType.CONSUMER_TOPIC_SENDQPS_VALLEY);
				break;
			case SENDQPS_FLU:
				sendMessage(AlarmType.CONSUMER_TOPIC_SENDQPS_FLUCTUATION);
				break;
			case SENDDELAY:
				sendMessage(AlarmType.CONSUMER_TOPIC_SENDMESSAGE_DELAY);
				break;
			case ACKQPS_PEAK:
				sendMessage(AlarmType.CONSUMER_TOPIC_ACKQPS_PEAK);
				break;
			case ACKQPS_VALLEY:
				sendMessage(AlarmType.CONSUMER_TOPIC_ACKQPS_VALLEY);
				break;
			case ACKQPS_FLU:
				sendMessage(AlarmType.CONSUMER_TOPIC_ACKQPS_FLUCTUATION);
				break;
			case ACKDELAY:
				sendMessage(AlarmType.CONSUMER_TOPIC_ACKMESSAGE_DELAY);
				break;
			default:
				break;
			}
			break;
		case PRODUCER:
			switch (getStatisType()) {
			case SENDQPS_PEAK:
				sendMessage(AlarmType.PRODUCER_TOPIC_QPS_PEAK);
				break;
			case SENDQPS_VALLEY:
				sendMessage(AlarmType.PRODUCER_TOPIC_QPS_VALLEY);
				break;
			case SENDQPS_FLU:
				sendMessage(AlarmType.PRODUCER_TOPIC_QPS_FLUCTUATION);
				break;
			case SENDDELAY:
				sendMessage(AlarmType.PRODUCER_TOPIC_MESSAGE_DELAY);
				break;
			default:
				break;
			}
		}
	}

	@Override
	public String getMessage(String template) {
		String message = template;
		if (StringUtils.isNotBlank(message)) {
			message = StringUtils.replace(message, AlarmMeta.TOPIC_TEMPLATE, getTopicName());
			message = StringUtils.replace(message, AlarmMeta.CURRENTVALUE_TEMPLATE, Long.toString(getCurrentValue()));
			message = StringUtils.replace(message, AlarmMeta.EXPECTEDVALUE_TEMPLATE, Long.toString(getExpectedValue()));
			message = StringUtils.replace(message, AlarmMeta.DATE_TEMPLATE, DateUtil.getDefaulFormat());
		}
		return message;
	}

	@Override
	public String getRelated() {
		return topicName;
	}

	@Override
	public boolean isSendAlarm(AlarmType alarmType, int timeSpan) {
		String key = topicName + KEY_SPLIT + alarmType.getNumber();
		return isAlarm(lastAlarms, key, timeSpan);
	}

	@Override
	public Set<String> getRelatedIps() {
		switch (getEventType()) {
		case PRODUCER:
			return ipCollectorService.getProducerTopicIps(topicName);
		case CONSUMER:
			return ipCollectorService.getConsumerTopicIps(topicName);
		}
		return null;
	}

	@Override
	public RelatedType getRelatedType() {
		switch (getEventType()) {
		case PRODUCER:
			return RelatedType.P_TOPIC;
		case CONSUMER:
			return RelatedType.C_TOPIC;
		}
		return null;
	}
}
