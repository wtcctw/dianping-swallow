package com.dianping.swallow.web.alarmer.impl;

import java.util.List;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.ConsumerIdAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIdAlarmSettingService;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;
import com.dianping.swallow.web.service.ConsumerServerAlarmSettingService;
import com.dianping.swallow.web.service.TopicAlarmSettingService;

@Component
public class ConsumerIdStatsAlarmer extends AbstractStatsAlarmer implements MonitorDataListener {

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private ConsumerStatsDataWapper consumerStatsDataWapper;

	@Autowired
	private ConsumerIdStatsDataService consumerIdStatsDataService;

	@Autowired
	private ConsumerIdAlarmSettingService consumerIdAlarmSettingService;

	@Autowired
	private TopicAlarmSettingService topicAlarmSettingService;

	@Autowired
	private ConsumerServerAlarmSettingService serverAlarmSettingService;

	@Override
	public void doInitialize() throws Exception {
		super.doInitialize();
		consumerDataRetriever.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {
		dataCount.incrementAndGet();
	}

	@Override
	public void doAlarm() {
		if (dataCount.get() <= 0) {
			return;
		}
		dataCount.decrementAndGet();
		final List<ConsumerIdStatsData> consumerIdStatsDatas = consumerStatsDataWapper
				.getConsumerIdStatsDatas(lastTimeKey.get());
		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "doAlarm");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				consumerIdAlarm(consumerIdStatsDatas);
			}
		});
	}

	private void consumerIdAlarm(List<ConsumerIdStatsData> consumerIdStatsDatas) {
		if (consumerIdStatsDatas == null || consumerIdStatsDatas.size() == 0) {
			return;
		}
		ConsumerIdAlarmSetting consumerIdAlarmSetting = consumerIdAlarmSettingService.findDefault();
		if (consumerIdAlarmSetting == null) {
			return;
		}
		List<String> topicWhiteList = serverAlarmSettingService.getTopicWhiteList();
		List<String> whiteList = topicAlarmSettingService.getConsumerIdWhiteList();
		ConsumerBaseAlarmSetting consumerAlarmSetting = consumerIdAlarmSetting.getConsumerAlarmSetting();
		QPSAlarmSetting sendQps = consumerAlarmSetting.getSendQpsAlarmSetting();
		QPSAlarmSetting ackQps = consumerAlarmSetting.getAckQpsAlarmSetting();
		long sendDelay = consumerAlarmSetting.getSendDelay();
		long ackDelay = consumerAlarmSetting.getAckDelay();
		long accumulation = consumerAlarmSetting.getAccumulation();

		for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
			String topicName = consumerIdStatsData.getTopicName();
			String consumerId = consumerIdStatsData.getConsumerId();
			if (StringUtils.equals(TOTAL_KEY, topicName)) {
				continue;
			}
			if (topicWhiteList != null && topicWhiteList.contains(topicName)) {
				continue;
			}
			if (StringUtils.equals(TOTAL_KEY, consumerId)) {
				continue;
			}
			if (whiteList == null || !whiteList.contains(consumerId)) {

				boolean isSendQps = sendQpsAlarm(consumerIdStatsData, sendQps);
				boolean isAckQps = ackSendAlarm(consumerIdStatsData, ackQps);
				if (isSendQps && isAckQps) {
					long timeKey = consumerIdStatsData.getTimeKey();
					Pair<Long, Long> preResult = getExpectedQps(topicName, consumerId, timeKey);
					sendQpsFluAlarm(consumerIdStatsData, preResult.getFirst(), sendQps);
					ackQpsFluAlarm(consumerIdStatsData, preResult.getSecond(), ackQps);
				}
				sendDelayAlarm(consumerIdStatsData, sendDelay);
				sendAccuAlarm(consumerIdStatsData, accumulation);
				ackDelayAlarm(consumerIdStatsData, ackDelay);
			}
		}

	}

	private boolean sendQpsAlarm(ConsumerIdStatsData consumerIdStatsData, QPSAlarmSetting qps) {
		if (qps != null) {
			if (!consumerIdStatsData.checkSendQpsPeak(qps.getPeak())) {
				return false;
			}
			if (!consumerIdStatsData.checkSendQpsValley(qps.getValley())) {
				return false;
			}
		}
		return true;
	}

	private boolean sendQpsFluAlarm(ConsumerIdStatsData consumerIdStatsData, long preQps, QPSAlarmSetting qps) {
		if (qps != null) {
			return consumerIdStatsData.checkSendQpsFlu(qps.getFluctuationBase(), preQps, qps.getFluctuation());
		}
		return true;
	}

	private boolean sendDelayAlarm(ConsumerIdStatsData consumerIdStatsData, long expectDelay) {
		return consumerIdStatsData.checkSendDelay(expectDelay);
	}

	private boolean sendAccuAlarm(ConsumerIdStatsData consumerIdStatsData, long expectAccu) {
		return consumerIdStatsData.checkSendAccu(expectAccu);
	}

	private boolean ackSendAlarm(ConsumerIdStatsData consumerIdStatsData, QPSAlarmSetting qps) {
		if (qps != null) {
			if (!consumerIdStatsData.checkAckQpsPeak(qps.getPeak())) {
				return false;
			}
			if (!consumerIdStatsData.checkAckQpsValley(qps.getValley())) {
				return false;
			}
		}
		return true;
	}

	private boolean ackQpsFluAlarm(ConsumerIdStatsData consumerIdStatsData, long preQps, QPSAlarmSetting qps) {
		if (qps != null) {
			return consumerIdStatsData.checkAckQpsFlu(qps.getFluctuationBase(), preQps, qps.getFluctuation());
		}
		return true;
	}

	private boolean ackDelayAlarm(ConsumerIdStatsData consumerIdStatsData, long expectDelay) {
		return consumerIdStatsData.checkAckDelay(expectDelay);
	}

	public Pair<Long, Long> getExpectedQps(String topicName, String consumerId, long timeKey) {
		long preDayTimeKey = getPreDayKey(timeKey);
		long startKey = preDayTimeKey - getTimeSection();
		long endKey = preDayTimeKey + getTimeSection();
		List<ConsumerIdStatsData> consumerIdStatsDatas = consumerIdStatsDataService.findSectionData(topicName,
				consumerId, startKey, endKey);
		int sendDataCount = 0;
		long sendSumQps = 0L;
		int ackDataCount = 0;
		long ackSumQps = 0L;
		Pair<Long, Long> result = new Pair<Long, Long>();
		result.setFirst(0L);
		result.setSecond(0L);
		if (consumerIdStatsDatas != null) {
			for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
				if (consumerIdStatsData.getSendQps() > 0L) {
					sendSumQps += consumerIdStatsData.getSendQps();
					sendDataCount++;
				}
				if (consumerIdStatsData.getAckQps() > 0L) {
					ackSumQps += consumerIdStatsData.getAckQps();
					ackDataCount++;
				}
			}
			if (sendDataCount > 0) {
				result.setFirst(sendSumQps / sendDataCount);
			}
			if (ackDataCount > 0) {
				result.setSecond(ackSumQps / ackDataCount);
			}
		}
		return result;
	}
}
