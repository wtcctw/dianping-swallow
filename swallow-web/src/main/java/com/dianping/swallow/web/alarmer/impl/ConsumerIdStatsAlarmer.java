package com.dianping.swallow.web.alarmer.impl;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.common.Pair;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ConsumerIdStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIdStatsDataService;

/**
 * @author qiyin
 *         <p/>
 *         2015年9月17日 下午8:24:20
 */
@Component
public class ConsumerIdStatsAlarmer extends AbstractStatsAlarmer {

    @Autowired
    private ConsumerDataRetriever consumerDataRetriever;

    @Autowired
    private ConsumerStatsDataWapper consumerStatsDataWapper;

    @Autowired
    private ConsumerIdStatsDataService consumerIdStatsDataService;

    @Autowired
    private ResourceContainer resourceContainer;

    @Override
    public void doInitialize() throws Exception {
        super.doInitialize();
        consumerDataRetriever.registerListener(this);
    }

    @Override
    public void doAlarm() {
        Set<String> topicNames = consumerStatsDataWapper.getTopics(false);
        if (topicNames == null) {
            return;
        }
        for (String topicName : topicNames) {
            alarmConsumerIds(topicName);
        }
    }

    private void alarmConsumerIds(String topicName) {
        final List<ConsumerIdStatsData> consumerIdStatsDatas = consumerStatsDataWapper.getConsumerIdStatsDatas(
                topicName, getLastTimeKey(), false);
        if (consumerIdStatsDatas == null || consumerIdStatsDatas.isEmpty()) {
            return;
        }
        for (ConsumerIdStatsData consumerIdStatsData : consumerIdStatsDatas) {
            try {
                String consumerId = consumerIdStatsData.getConsumerId();
                ConsumerIdResource consumerIdResource = resourceContainer.findConsumerIdResource(topicName, consumerId, true);
                TopicResource topicResource = resourceContainer.findTopicResource(topicName, true);
                if ((topicResource != null && !topicResource.isConsumerAlarm()) || consumerIdResource == null
                        || !consumerIdResource.isAlarm()) {
                    continue;
                }
                ConsumerBaseAlarmSetting consumerAlarmSetting = consumerIdResource.getConsumerAlarmSetting();
                QPSAlarmSetting sendQps = consumerAlarmSetting.getSendQpsAlarmSetting();
                QPSAlarmSetting ackQps = consumerAlarmSetting.getAckQpsAlarmSetting();
                long sendDelay = consumerAlarmSetting.getSendDelay();
                long ackDelay = consumerAlarmSetting.getAckDelay();
                long accumulation = consumerAlarmSetting.getAccumulation();
                // 告警
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

            } catch (Exception e) {
                logger.error("[consumerIdAlarm] consumerIdStatsData {} error.", consumerIdStatsData);
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
