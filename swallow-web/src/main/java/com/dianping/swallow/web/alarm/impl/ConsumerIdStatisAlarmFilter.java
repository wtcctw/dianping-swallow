package com.dianping.swallow.web.alarm.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.event.ConsumerIdEvent;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.statis.ConsumerBaseStatsData;
import com.dianping.swallow.web.model.statis.ConsumerIdStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerDataWapper;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerIdStatisDataService;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerIdStatisAlarmFilter")
public class ConsumerIdStatisAlarmFilter extends AbstractStatisAlarmFilter implements MonitorDataListener {

	@Autowired
	private MessageManager alarmManager;

	@Autowired
	private ConsumerDataWapper consumerDataWapper;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	private Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap;

	@Autowired
	private ConsumerIdStatisDataService consumerIdStatisDataService;

	@Autowired
	private ConsumerIdAlarmSettingService consumerIdAlarmSettingService;

	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;

	@Autowired
	private ConsumerServerAlarmSettingService serverAlarmSettingService;

	@PostConstruct
	public void initialize() {
		super.initialize();
		consumerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	public boolean doAccept() {
		if (dataCount.get() > 0) {
			dataCount.incrementAndGet();
			consumerIdStatsDataMap = consumerDataWapper.getConsumerIdStatsData(lastTimeKey.get());
			return consumerIdAlarm();
		}
		return true;
	}

	private boolean consumerIdAlarm() {
		if (consumerIdStatsDataMap == null || consumerIdStatsDataMap.size() == 0) {
			return true;
		}
		ConsumerIdAlarmSetting consumerIdAlarmSetting = consumerIdAlarmSettingService.findDefault();
		if (consumerIdAlarmSetting == null) {
			return true;
		}
		List<String> topicWhiteList = serverAlarmSettingService.getTopicWhiteList();
		List<String> whiteList = topicAlarmSettingService.getConsumerIdWhiteList();
		ConsumerBaseAlarmSetting consumerAlarmSetting = consumerIdAlarmSetting.getConsumerAlarmSetting();
		QPSAlarmSetting sendQps = consumerAlarmSetting.getSendQpsAlarmSetting();
		QPSAlarmSetting ackQps = consumerAlarmSetting.getAckQpsAlarmSetting();
		long sendDelay = consumerAlarmSetting.getSendDelay();
		long ackDelay = consumerAlarmSetting.getAckDelay();
		long accumulation = consumerAlarmSetting.getAccumulation();
		for (Map.Entry<String, List<ConsumerIdStatsData>> consumerIdStatsDataEntry : consumerIdStatsDataMap.entrySet()) {
			String topic = consumerIdStatsDataEntry.getKey();
			if (topicWhiteList != null && topicWhiteList.contains(topic)) {
				continue;
			}
			List<ConsumerIdStatsData> consumerIdStatsDatas = consumerIdStatsDataEntry.getValue();
			for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
				if (whiteList == null || !whiteList.contains(consumerIdStatsData.getConsumerId())) {
					String consumerId = consumerIdStatsData.getConsumerId();
					ConsumerBaseStatsData consumerBaseStatsData = consumerIdStatsData.getStatisData();
					if (consumerBaseStatsData == null) {
						continue;
					}
					boolean isSendQpsOk = sendQpsAlarm(topic, consumerId, consumerBaseStatsData.getSendQpx(), sendQps);

					boolean isAckQpsOk = ackQpsAlarm(topic, consumerId, consumerBaseStatsData.getAckQpx(), ackQps);
					if (isSendQpsOk || isAckQpsOk) {
						ConsumerBaseStatsData baseStatsData = getSectionData(topic,
								consumerIdStatsData.getConsumerId(), consumerIdStatsData.getTimeKey());
						if (baseStatsData != null) {
							if (isSendQpsOk) {
								sendFluctuationAlarm(topic, consumerId, consumerBaseStatsData.getSendQpx(),
										baseStatsData.getSendQpx(), sendQps);
							}
							if (isAckQpsOk) {
								ackFluctuationAlarm(topic, consumerId, consumerBaseStatsData.getAckQpx(),
										baseStatsData.getAckQpx(), ackQps);
							}
						}
					}

					sendDelayAlarm(topic, consumerId, consumerBaseStatsData.getSendDelay() / 1000, sendDelay);
					ackDelayAlarm(topic, consumerId, consumerBaseStatsData.getAckDelay() / 1000, ackDelay);
					accumulationAlarm(topic, consumerId, consumerBaseStatsData.getAccumulation(), accumulation);
				}
			}
		}
		return true;
	}

	private boolean sendQpsAlarm(String topic, String consumerId, long qpx, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
				consumerIdEvent.setTopicName(topic);
				consumerIdEvent.setConsumerId(consumerId);
				consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_SENDQPS_PEAK);
				consumerIdEvent.setCurrentValue(qpx);
				consumerIdEvent.setExpectedValue(qps.getPeak());
				consumerIdEvent.setEventType(EventType.CONSUMER);
				consumerIdEvent.setCreateTime(new Date());
				eventReporter.report(consumerIdEvent);
				return false;
			}
			if (qpx < qps.getValley()) {
				ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
				consumerIdEvent.setTopicName(topic);
				consumerIdEvent.setConsumerId(consumerId);
				consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_SENDQPS_VALLEY);
				consumerIdEvent.setCurrentValue(qpx);
				consumerIdEvent.setExpectedValue(qps.getValley());
				consumerIdEvent.setEventType(EventType.CONSUMER);
				consumerIdEvent.setCreateTime(new Date());
				eventReporter.report(consumerIdEvent);
				return false;
			}
		}
		return true;

	}

	private boolean ackQpsAlarm(String topic, String consumerId, long qpx, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
				consumerIdEvent.setTopicName(topic);
				consumerIdEvent.setConsumerId(consumerId);
				consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_ACKQPS_PEAK);
				consumerIdEvent.setCurrentValue(qpx);
				consumerIdEvent.setExpectedValue(qps.getPeak());
				consumerIdEvent.setEventType(EventType.CONSUMER);
				consumerIdEvent.setCreateTime(new Date());
				eventReporter.report(consumerIdEvent);
				return false;
			}
			if (qpx < qps.getValley()) {
				ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
				consumerIdEvent.setTopicName(topic);
				consumerIdEvent.setConsumerId(consumerId);
				consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_ACKQPS_VALLEY);
				consumerIdEvent.setCurrentValue(qpx);
				consumerIdEvent.setExpectedValue(qps.getValley());
				consumerIdEvent.setEventType(EventType.CONSUMER);
				consumerIdEvent.setCreateTime(new Date());
				eventReporter.report(consumerIdEvent);
				return false;
			}
		}
		return true;

	}

	private boolean sendFluctuationAlarm(String topic, String consumerId, long qpx, long expectedQpx,
			QPSAlarmSetting qps) {
		if (qpx != 0 && expectedQpx != 0 && qps != null) {
			if (qpx < qps.getFluctuationBase() && expectedQpx < qps.getFluctuationBase()) {
				return true;
			}
			if ((qpx > expectedQpx && (qpx / expectedQpx) > qps.getFluctuation())
					|| (qpx < expectedQpx && (expectedQpx / qpx) > qps.getFluctuation())) {
				ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
				consumerIdEvent.setTopicName(topic);
				consumerIdEvent.setConsumerId(consumerId);
				consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_SENDQPS_FLUCTUATION);
				consumerIdEvent.setCurrentValue(qpx);
				consumerIdEvent.setExpectedValue(expectedQpx);
				consumerIdEvent.setEventType(EventType.CONSUMER);
				consumerIdEvent.setCreateTime(new Date());
				eventReporter.report(consumerIdEvent);
				return false;
			}
		}
		return true;
	}

	private boolean ackFluctuationAlarm(String topic, String consumerId, long qpx, long expectedQpx, QPSAlarmSetting qps) {
		if (qpx != 0 && expectedQpx != 0 && qps != null) {
			if (qpx < qps.getFluctuationBase() && expectedQpx < qps.getFluctuationBase()) {
				return true;
			}
			if ((qpx > expectedQpx && (qpx / expectedQpx) > qps.getFluctuation())
					|| (qpx < expectedQpx && (expectedQpx / qpx) > qps.getFluctuation())) {
				ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
				consumerIdEvent.setTopicName(topic);
				consumerIdEvent.setConsumerId(consumerId);
				consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_ACKQPS_FLUCTUATION);
				consumerIdEvent.setCurrentValue(qpx);
				consumerIdEvent.setExpectedValue(expectedQpx);
				consumerIdEvent.setEventType(EventType.CONSUMER);
				consumerIdEvent.setCreateTime(new Date());
				eventReporter.report(consumerIdEvent);
				return false;
			}
		}
		return true;
	}

	private ConsumerBaseStatsData getSectionData(String topicName, String consumerId, long timeKey) {
		long preDayTimeKey = getPreDayKey(timeKey);
		List<ConsumerIdStatsData> consumerIdStatsDatas = consumerIdStatisDataService.findSectionData(topicName,
				consumerId, preDayTimeKey - getTimeSection(), preDayTimeKey + getTimeSection());
		int sendCount = 0;
		int ackCount = 0;
		long sumSendQpx = 0;
		long sumAckQpx = 0;
		if (consumerIdStatsDatas == null || consumerIdStatsDatas.size() == 0) {
			return null;
		}
		ConsumerBaseStatsData baseStatsData = new ConsumerBaseStatsData();
		for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
			if (consumerIdStatsData == null || consumerIdStatsData.getStatisData() == null) {
				continue;
			}
			if (consumerIdStatsData.getStatisData().getSendQpx() != 0) {
				sumSendQpx += consumerIdStatsData.getStatisData().getSendQpx();
				sendCount++;
			} else if (consumerIdStatsData.getStatisData().getAckQpx() != 0) {
				sumAckQpx += consumerIdStatsData.getStatisData().getAckQpx();
				ackCount++;
			}
		}
		if (sendCount != 0) {
			baseStatsData.setSendQpx(sumSendQpx / sendCount);
		}
		if (ackCount != 0) {
			baseStatsData.setSendQpx(sumAckQpx / ackCount);
		}
		return baseStatsData;

	}

	private boolean sendDelayAlarm(String topic, String consumerId, long delay, long expectDelay) {
		if (delay > expectDelay) {
			ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
			consumerIdEvent.setTopicName(topic);
			consumerIdEvent.setConsumerId(consumerId);
			consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_SENDMESSAGE_DELAY);
			consumerIdEvent.setCurrentValue(delay);
			consumerIdEvent.setExpectedValue(expectDelay);
			consumerIdEvent.setEventType(EventType.CONSUMER);
			consumerIdEvent.setCreateTime(new Date());
			eventReporter.report(consumerIdEvent);
			return false;
		}
		return true;
	}

	private boolean ackDelayAlarm(String topic, String consumerId, long delay, long expectDelay) {
		if (delay > expectDelay) {
			ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
			consumerIdEvent.setTopicName(topic);
			consumerIdEvent.setConsumerId(consumerId);
			consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_ACKMESSAGE_DELAY);
			consumerIdEvent.setCurrentValue(delay);
			consumerIdEvent.setExpectedValue(expectDelay);
			consumerIdEvent.setEventType(EventType.CONSUMER);
			consumerIdEvent.setCreateTime(new Date());
			eventReporter.report(consumerIdEvent);
			return false;
		}
		return true;
	}

	private boolean accumulationAlarm(String topic, String consumerId, long accumulation, long expectedAccumulation) {
		if (accumulation > expectedAccumulation) {
			ConsumerIdEvent consumerIdEvent = new ConsumerIdEvent();
			consumerIdEvent.setTopicName(topic);
			consumerIdEvent.setConsumerId(consumerId);
			consumerIdEvent.setAlarmType(AlarmType.CONSUMER_CONSUMERID_SENDMESSAGE_ACCUMULATION);
			consumerIdEvent.setCurrentValue(accumulation);
			consumerIdEvent.setExpectedValue(expectedAccumulation);
			consumerIdEvent.setEventType(EventType.CONSUMER);
			consumerIdEvent.setCreateTime(new Date());
			eventReporter.report(consumerIdEvent);
			return false;
		}
		return true;
	}
}
