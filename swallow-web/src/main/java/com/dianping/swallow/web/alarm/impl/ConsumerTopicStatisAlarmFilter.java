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

	private volatile List<ConsumerTopicStatsData> topicStatisDatas;

	@Autowired
	private ConsumerTopicStatisDataService topicStatisDataService;

	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;

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
		QPSAlarmSetting sendQps = consumerAlarmSetting.getSenderQpsAlarmSetting();
		QPSAlarmSetting ackQps = consumerAlarmSetting.getAckQpsAlarmSetting();
		List<String> whiteList = topicAlarmSetting.getWhiteList();
		long sendDelay = consumerAlarmSetting.getSenderDelay();
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
				boolean isContinue = true;
				if (sendQps != null) {
					isContinue = sendQpsAlarm(topic, consumerBaseStatsData.getSenderQpx(), sendQps.getPeak(),
							sendQps.getValley());
				}
				if (!isContinue) {
					return false;
				}
				if (ackQps != null) {
					isContinue = ackQpsAlarm(topic, consumerBaseStatsData.getAckQpx(), ackQps.getPeak(),
							ackQps.getValley());
				}
				if (!isContinue) {
					return false;
				}
				ConsumerBaseStatsData baseStatsData = getSectionData(topicStatisData.getTopicName(),
						topicStatisData.getTimeKey());
				if (sendQps != null && baseStatsData != null) {
					isContinue = sendFluctuationAlarm(topic, consumerBaseStatsData.getSenderQpx(),
							baseStatsData.getSenderQpx(), sendQps.getFluctuation());
				}
				if (!isContinue) {
					return false;
				}
				if (ackQps != null && baseStatsData != null) {
					isContinue = ackFluctuationAlarm(topic, consumerBaseStatsData.getAckQpx(),
							baseStatsData.getAckQpx(), ackQps.getFluctuation());
				}
				if (!isContinue) {
					return false;
				}
				isContinue = sendDelayAlarm(topic, sendDelay, consumerBaseStatsData.getSenderDelay());
				if (!isContinue) {
					return false;
				}
				isContinue = ackDelayAlarm(topic, ackDelay, consumerBaseStatsData.getAckDelay());
				if (!isContinue) {
					return false;
				}
			}
		}
		return true;

	}

	private boolean sendQpsAlarm(String topic, long qpx, long peak, long valley) {
		if (qpx > peak) {
			alarmManager.consumerTopicStatisSQpsPAlarm(topic, qpx);
			return false;
		}
		if (qpx < valley) {
			alarmManager.consumerTopicStatisSQpsVAlarm(topic, qpx);
			return false;
		}
		return true;
	}

	private boolean ackQpsAlarm(String topic, long qpx, long peak, long valley) {
		if (qpx > peak) {
			alarmManager.consumerTopicStatisSQpsPAlarm(topic, qpx);
			return false;
		}
		if (qpx < valley) {
			alarmManager.consumerTopicStatisSQpsVAlarm(topic, qpx);
			return false;
		}
		return true;
	}

	private boolean sendFluctuationAlarm(String topic, long qpx, long expectedQpx, int fluctuation) {
		if (qpx > expectedQpx && (qpx / expectedQpx) < fluctuation) {
			alarmManager.consumerTopicStatisSQpsFAlarm(topic, qpx, expectedQpx);
			return false;
		}
		if (qpx < expectedQpx && (expectedQpx / qpx) < fluctuation) {
			alarmManager.consumerTopicStatisSQpsFAlarm(topic, qpx, expectedQpx);
			return false;
		}
		return true;
	}

	private boolean ackFluctuationAlarm(String topic, long qpx, long expectedQpx, int fluctuation) {
		if (qpx > expectedQpx && (qpx / expectedQpx) < fluctuation) {
			alarmManager.consumerTopicStatisAQpsFAlarm(topic, qpx, expectedQpx);
			return false;
		}
		if (qpx < expectedQpx && (expectedQpx / qpx) < fluctuation) {
			alarmManager.consumerTopicStatisAQpsFAlarm(topic, qpx, expectedQpx);
			return false;
		}
		return true;
	}

	private ConsumerBaseStatsData getSectionData(String topicName, long timeKey) {
		List<ConsumerTopicStatsData> topicStatsDatas = topicStatisDataService.findSectionData(topicName, timeKey
				- getTimeSection(), timeKey + getTimeSection());
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
			if (topicStatsData.getConsumerStatisData().getSenderQpx() == 0) {
				sumSendQpx += topicStatsData.getConsumerStatisData().getSenderQpx();
				sendCount++;
			} else if (topicStatsData.getConsumerStatisData().getAckQpx() == 0) {
				sumAckQpx += topicStatsData.getConsumerStatisData().getAckQpx();
				ackCount++;
			}
		}
		if (sendCount != 0) {
			baseStatsData.setSenderQpx(sumSendQpx / sendCount);
		}
		if (ackCount != 0) {
			baseStatsData.setSenderQpx(sumAckQpx / ackCount);
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
