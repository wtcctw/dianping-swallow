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

	private volatile Map<String, List<ConsumerIdStatsData>> consumerIdStatsDataMap;

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
		ConsumerIdAlarmSetting consumerIdAlarmSetting = consumerIdAlarmSettingService.findOne();
		if (consumerIdAlarmSetting == null) {
			return true;
		}
		List<String> whiteList = topicAlarmSettingService.getConsumerIdWhiteList();
		ConsumerBaseAlarmSetting consumerAlarmSetting = consumerIdAlarmSetting.getConsumerAlarmSetting();
		QPSAlarmSetting sendQps = consumerAlarmSetting.getSenderQpsAlarmSetting();
		QPSAlarmSetting ackQps = consumerAlarmSetting.getAckQpsAlarmSetting();
		long sendDelay = consumerAlarmSetting.getSenderDelay();
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
					boolean isContinue = true;
					if (sendQps != null) {
						isContinue = sendQpsAlarm(topic, consumerId, consumerBaseStatsData.getSenderQpx(),
								sendQps.getPeak(), sendQps.getValley());
					}
					if (!isContinue) {
						return false;
					}
					if (ackQps != null) {
						isContinue = ackQpsAlarm(topic, consumerId, consumerBaseStatsData.getAckQpx(),
								ackQps.getPeak(), ackQps.getValley());
					}
					if (!isContinue) {
						return false;
					}
					ConsumerBaseStatsData baseStatsData = getSectionData(topic, consumerIdStatsData.getConsumerId(),
							consumerIdStatsData.getTimeKey());
					if (sendQps != null && baseStatsData != null) {
						isContinue = sendFluctuationAlarm(topic, consumerId, consumerBaseStatsData.getSenderQpx(),
								baseStatsData.getSenderQpx(), sendQps.getFluctuation());
					}
					if (!isContinue) {
						return false;
					}
					if (ackQps != null && baseStatsData != null) {
						isContinue = ackFluctuationAlarm(topic, consumerId, consumerBaseStatsData.getAckQpx(),
								baseStatsData.getAckQpx(), ackQps.getFluctuation());
					}
					if (!isContinue) {
						return false;
					}
					isContinue = sendDelayAlarm(topic, consumerId, sendDelay, consumerBaseStatsData.getSenderDelay());
					if (!isContinue) {
						return false;
					}
					isContinue = ackDelayAlarm(topic, consumerId, ackDelay, consumerBaseStatsData.getAckDelay());
					if (!isContinue) {
						return false;
					}
					isContinue = accumulationAlarm(topic, consumerId, consumerBaseStatsData.getAccumulation(),
							accumulation);
					if (!isContinue) {
						return false;
					}
				}
			}
		}
		return true;
	}

	private boolean sendQpsAlarm(String topic, String consumerId, long qpx, long peak, long valley) {
		if (qpx > peak) {
			alarmManager.consumerIdStatisSQpsPAlarm(topic, consumerId, qpx);
			return false;
		}
		if (qpx < valley) {
			alarmManager.consumerIdStatisSQpsVAlarm(topic, consumerId, qpx);
			return false;
		}
		return true;

	}

	private boolean ackQpsAlarm(String topic, String consumerId, long qpx, long peak, long valley) {
		if (qpx > peak) {
			alarmManager.consumerIdStatisAQpsPAlarm(topic, consumerId, qpx);
			return false;
		}
		if (qpx < valley) {
			alarmManager.consumerIdStatisAQpsVAlarm(topic, consumerId, qpx);
			return false;
		}
		return true;

	}

	private boolean sendFluctuationAlarm(String topic, String consumerId, long qpx, long expectedQpx, int fluctuation) {
		if (qpx > expectedQpx && (qpx / expectedQpx) < fluctuation) {
			alarmManager.consumerIdStatisSQpsFAlarm(topic, consumerId, qpx, expectedQpx);
			return false;
		}
		if (qpx < expectedQpx && (expectedQpx / qpx) < fluctuation) {
			alarmManager.consumerIdStatisSQpsFAlarm(topic, consumerId, qpx, expectedQpx);
			return false;
		}
		return true;
	}

	private boolean ackFluctuationAlarm(String topic, String consumerId, long qpx, long expectedQpx, int fluctuation) {
		if (qpx > expectedQpx && (qpx / expectedQpx) < fluctuation) {
			alarmManager.consumerIdStatisAQpsFAlarm(topic, consumerId, qpx, expectedQpx);
			return false;
		}
		if (qpx < expectedQpx && (expectedQpx / qpx) < fluctuation) {
			alarmManager.consumerIdStatisAQpsFAlarm(topic, consumerId, qpx, expectedQpx);
			return false;
		}
		return true;
	}

	private ConsumerBaseStatsData getSectionData(String topicName, String consumerId, long timeKey) {
		List<ConsumerIdStatsData> consumerIdStatsDatas = consumerIdStatisDataService.findSectionData(topicName,
				consumerId, timeKey - getTimeSection(), timeKey + getTimeSection());
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
			if (consumerIdStatsData.getStatisData().getSenderQpx() == 0) {
				sumSendQpx += consumerIdStatsData.getStatisData().getSenderQpx();
				sendCount++;
			} else if (consumerIdStatsData.getStatisData().getAckQpx() == 0) {
				sumAckQpx += consumerIdStatsData.getStatisData().getAckQpx();
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
