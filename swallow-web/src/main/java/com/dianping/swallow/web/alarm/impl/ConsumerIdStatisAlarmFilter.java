package com.dianping.swallow.web.alarm.impl;

import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.manager.AlarmManager;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.statis.ConsumerBaseStatsData;
import com.dianping.swallow.web.model.statis.ConsumerIdStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerDataWapper;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerIdStatisDataService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

/**
 *
 * @author qiyin
 *
 */
@Service("consumerIdStatisAlarmFilter")
public class ConsumerIdStatisAlarmFilter extends AbstractStatisAlarmFilter implements MonitorDataListener {

	@Autowired
	private AlarmManager alarmManager;

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
		List<String> whiteList = topicAlarmSettingService.getConsumerIdWhiteList();
		ConsumerBaseAlarmSetting consumerAlarmSetting = consumerIdAlarmSetting.getConsumerAlarmSetting();
		QPSAlarmSetting sendQps = consumerAlarmSetting.getSendQpsAlarmSetting();
		QPSAlarmSetting ackQps = consumerAlarmSetting.getAckQpsAlarmSetting();
		long sendDelay = consumerAlarmSetting.getSendDelay();
		long ackDelay = consumerAlarmSetting.getAckDelay();
		long accumulation = consumerAlarmSetting.getAccumulation();
		for (Map.Entry<String, List<ConsumerIdStatsData>> consumerIdStatsDataEntry : consumerIdStatsDataMap.entrySet()) {
			String topic = consumerIdStatsDataEntry.getKey();
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

					sendDelayAlarm(topic, consumerId, consumerBaseStatsData.getSendDelay(), sendDelay);
					ackDelayAlarm(topic, consumerId, consumerBaseStatsData.getAckDelay(), ackDelay);
					accumulationAlarm(topic, consumerId, consumerBaseStatsData.getAccumulation(), accumulation);
				}
			}
		}
		return true;
	}

	private boolean sendQpsAlarm(String topic, String consumerId, long qpx, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				alarmManager.consumerIdStatisSQpsPAlarm(topic, consumerId, qpx, qps.getPeak());
				return false;
			}
			if (qpx < qps.getValley()) {
				alarmManager.consumerIdStatisSQpsVAlarm(topic, consumerId, qpx, qps.getValley());
				return false;
			}
		}
		return true;

	}

	private boolean ackQpsAlarm(String topic, String consumerId, long qpx, QPSAlarmSetting qps) {
		if (qps != null && qpx != 0L) {
			if (qpx > qps.getPeak()) {
				alarmManager.consumerIdStatisAQpsPAlarm(topic, consumerId, qpx, qps.getPeak());
				return false;
			}
			if (qpx < qps.getValley()) {
				alarmManager.consumerIdStatisAQpsVAlarm(topic, consumerId, qpx, qps.getValley());
				return false;
			}
		}
		return true;

	}

	private boolean sendFluctuationAlarm(String topic, String consumerId, long qpx, long expectedQpx,
			QPSAlarmSetting qps) {
		if (qpx != 0 && expectedQpx != 0 && qps != null) {
			if (qpx > expectedQpx && (qpx / expectedQpx) < qps.getFluctuation()) {
				alarmManager.consumerIdStatisSQpsFAlarm(topic, consumerId, qpx, expectedQpx);
				return false;
			}
			if (qpx < expectedQpx && (expectedQpx / qpx) < qps.getFluctuation()) {
				alarmManager.consumerIdStatisSQpsFAlarm(topic, consumerId, qpx, expectedQpx);
				return false;
			}
		}
		return true;
	}

	private boolean ackFluctuationAlarm(String topic, String consumerId, long qpx, long expectedQpx, QPSAlarmSetting qps) {
		if (qpx != 0 && expectedQpx != 0 && qps != null) {
			if (qpx > expectedQpx && (qpx / expectedQpx) < qps.getFluctuation()) {
				alarmManager.consumerIdStatisAQpsFAlarm(topic, consumerId, qpx, expectedQpx);
				return false;
			}
			if (qpx < expectedQpx && (expectedQpx / qpx) < qps.getFluctuation()) {
				alarmManager.consumerIdStatisAQpsFAlarm(topic, consumerId, qpx, expectedQpx);
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
			alarmManager.consumerIdStatisSQpsDAlarm(topic, consumerId, delay, expectDelay);
			return false;
		}
		return true;
	}

	private boolean ackDelayAlarm(String topic, String consumerId, long delay, long expectDelay) {
		if (delay > expectDelay) {
			alarmManager.consumerIdStatisAQpsDAlarm(topic, consumerId, delay, expectDelay);
			return false;
		}
		return true;
	}

	private boolean accumulationAlarm(String topic, String consumerId, long accumulation, long expectedAccumulation) {
		if (accumulation > expectedAccumulation) {
			alarmManager.consumerIdStatisSAccuAlarm(topic, consumerId, accumulation, expectedAccumulation);
			return false;
		}
		return true;
	}
}
