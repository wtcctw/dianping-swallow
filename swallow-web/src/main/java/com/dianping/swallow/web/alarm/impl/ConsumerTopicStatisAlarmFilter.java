package com.dianping.swallow.web.alarm.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.alarm.TopicAlarmSetting;
import com.dianping.swallow.web.model.statis.ConsumerBaseStatsData;
import com.dianping.swallow.web.model.statis.ConsumerTopicStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerDataWapper;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerTopicStatisDataService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerTopicStatisAlarmFilter")
public class ConsumerTopicStatisAlarmFilter extends AbstractStatisAlarmFilter implements MonitorDataListener {

	@Autowired
	private AlarmManager alarmManager;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerDataWapper consumerDataWapper;

	private List<ConsumerTopicStatsData> topicStatisDatas;

	@Autowired
	private ConsumerTopicStatisDataService topicStatisDataService;

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
		if (dataCount.getAndDecrement() > 0) {
			dataCount.incrementAndGet();
			topicStatisDatas = consumerDataWapper.getTopicStatsData(lastTimeKey.get());
			return topicAlarm();
		}
		return true;
	}

	public boolean topicAlarm() {
		TopicAlarmSetting topicAlarmSetting = topicAlarmSettingService.findOne();
		if (topicAlarmSetting == null || topicAlarmSetting.getConsumerAlarmSetting() == null) {
			return true;
		}
		ConsumerBaseAlarmSetting consumerAlarmSetting = topicAlarmSetting.getConsumerAlarmSetting();
		QPSAlarmSetting sendQps = consumerAlarmSetting.getSendQpsAlarmSetting();
		QPSAlarmSetting ackQps = consumerAlarmSetting.getAckQpsAlarmSetting();
		List<String> whiteList = serverAlarmSettingService.getTopicWhiteList();
		long sendDelay = consumerAlarmSetting.getSendDelay();
		long ackDelay = consumerAlarmSetting.getAckDelay();
		if (topicStatisDatas == null) {
			return true;
		}
		for (ConsumerTopicStatsData topicStatisData : topicStatisDatas) {
			if (whiteList == null || !whiteList.contains(topicStatisData.getTopicName())) {
				String topic = topicStatisData.getTopicName();
				ConsumerBaseStatsData consumerBaseStatsData = topicStatisData.getConsumerStatisData();
				if (consumerBaseStatsData == null) {
					continue;
				}
				boolean isSendQpsOk = sendQpsAlarm(topic, consumerBaseStatsData.getSendQpx(), sendQps);

				boolean isAckQpsOk = ackQpsAlarm(topic, consumerBaseStatsData.getAckQpx(), ackQps);
				if (isSendQpsOk || isAckQpsOk) {
					ConsumerBaseStatsData baseStatsData = getSectionData(topic, topicStatisData.getTimeKey());
					if (baseStatsData != null) {
						if (isSendQpsOk) {
							sendFluctuationAlarm(topic, consumerBaseStatsData.getSendQpx(), baseStatsData.getSendQpx(),
									sendQps);
						}
						if (isAckQpsOk) {
							ackFluctuationAlarm(topic, consumerBaseStatsData.getAckQpx(), baseStatsData.getAckQpx(),
									ackQps);
						}
					}
				}

				sendDelayAlarm(topic, sendDelay, consumerBaseStatsData.getSendDelay());
				ackDelayAlarm(topic, ackDelay, consumerBaseStatsData.getAckDelay());
			}
		}
		return true;

	}

	private boolean sendQpsAlarm(String topic, long qpx, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				alarmManager.consumerTopicStatisSQpsPAlarm(topic, qpx);
				return false;
			}
			if (qpx < qps.getValley()) {
				alarmManager.consumerTopicStatisSQpsVAlarm(topic, qpx);
				return false;
			}
		}
		return true;
	}

	private boolean ackQpsAlarm(String topic, long qpx, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				alarmManager.consumerTopicStatisSQpsPAlarm(topic, qpx);
				return false;
			}
			if (qpx < qps.getValley()) {
				alarmManager.consumerTopicStatisSQpsVAlarm(topic, qpx);
				return false;
			}
		}
		return true;
	}

	private boolean sendFluctuationAlarm(String topic, long qpx, long expectedQpx, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > expectedQpx && (qpx / expectedQpx) > qps.getFluctuation()) {
				alarmManager.consumerTopicStatisSQpsFAlarm(topic, qpx, expectedQpx);
				return false;
			}
			if (qpx < expectedQpx && (expectedQpx / qpx) > qps.getFluctuation()) {
				alarmManager.consumerTopicStatisSQpsFAlarm(topic, qpx, expectedQpx);
				return false;
			}
		}
		return true;
	}

	private boolean ackFluctuationAlarm(String topic, long qpx, long expectedQpx, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > expectedQpx && (qpx / expectedQpx) > qps.getFluctuation()) {
				alarmManager.consumerTopicStatisAQpsFAlarm(topic, qpx, expectedQpx);
				return false;
			}
			if (qpx < expectedQpx && (expectedQpx / qpx) > qps.getFluctuation()) {
				alarmManager.consumerTopicStatisAQpsFAlarm(topic, qpx, expectedQpx);
				return false;
			}
		}
		return true;
	}

	private ConsumerBaseStatsData getSectionData(String topicName, long timeKey) {
		long preDayTimeKey = getPreDayKey(timeKey);
		List<ConsumerTopicStatsData> topicStatsDatas = topicStatisDataService.findSectionData(topicName, preDayTimeKey
				- getTimeSection(), preDayTimeKey + getTimeSection());
		int sendCount = 0;
		int ackCount = 0;
		long sumSendQpx = 0;
		long sumAckQpx = 0;
		if (topicStatsDatas == null || topicStatsDatas.size() == 0) {
			return null;
		}
		ConsumerBaseStatsData baseStatsData = new ConsumerBaseStatsData();
		for (ConsumerTopicStatsData topicStatsData : topicStatsDatas) {
			if (topicStatsData == null || topicStatsData.getConsumerStatisData() == null) {
				continue;
			}
			if (topicStatsData.getConsumerStatisData().getSendQpx() != 0) {
				sumSendQpx += topicStatsData.getConsumerStatisData().getSendQpx();
				sendCount++;
			} else if (topicStatsData.getConsumerStatisData().getAckQpx() != 0) {
				sumAckQpx += topicStatsData.getConsumerStatisData().getAckQpx();
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

	private boolean sendDelayAlarm(String topic, long delay, long expectDelay) {
		if (delay > expectDelay) {
			alarmManager.consumerTopicStatisSQpsDAlarm(topic, delay, expectDelay);
			return false;
		}
		return true;
	}

	private boolean ackDelayAlarm(String topic, long delay, long expectDelay) {
		if (delay > expectDelay) {
			alarmManager.consumerTopicStatisAQpsDAlarm(topic, delay, expectDelay);
			return false;
		}
		return true;
	}
}
