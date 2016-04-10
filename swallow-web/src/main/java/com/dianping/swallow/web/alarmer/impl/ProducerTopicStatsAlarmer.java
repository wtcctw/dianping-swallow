package com.dianping.swallow.web.alarmer.impl;

import java.util.List;

import com.dianping.swallow.common.internal.config.ConfigChangeListener;
import com.dianping.swallow.common.internal.config.DynamicConfig;
import com.dianping.swallow.common.internal.config.impl.DefaultDynamicConfig;
import com.dianping.swallow.common.internal.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.alarm.ProducerBaseAlarmSetting;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ProducerTopicStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.ProducerTopicStatsDataService;

/**
 * @author qiyin
 *         <p/>
 *         2015年8月3日 下午6:07:16
 */
@Component
public class ProducerTopicStatsAlarmer extends AbstractStatsAlarmer implements ConfigChangeListener {

    private static final String MESSAGE_SIZE_KEY = "swallow.web.alarmer.messagesize";
    private static final String LION_CONFIG_FILENAME = "swallow-web-lion.properties";

    @Autowired
    private ProducerDataRetriever producerDataRetriever;

    @Autowired
    private ProducerStatsDataWapper producerStatsDataWapper;

    @Autowired
    private ProducerTopicStatsDataService topicStatsDataService;

    @Autowired
    private ResourceContainer resourceContainer;

    protected DynamicConfig dynamicConfig;

    private volatile long msgSize = 1000L;

    @Override
    public void doInitialize() throws Exception {
        super.doInitialize();
        producerDataRetriever.registerListener(this);
        dynamicConfig = new DefaultDynamicConfig(LION_CONFIG_FILENAME);
        dynamicConfig.addConfigChangeListener(this);
        String strMsgSize = dynamicConfig.get(MESSAGE_SIZE_KEY);
        msgSize = Long.parseLong(strMsgSize);
    }

    @Override
    public void doAlarm() {
        topicAlarm();
    }

    private void topicAlarm() {
        List<ProducerTopicStatsData> topicStatsDatas = producerStatsDataWapper.getTopicStatsDatas(getLastTimeKey(),
                false);
        if (topicStatsDatas == null || topicStatsDatas.isEmpty()) {
            return;
        }
        for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
            try {
                String topicName = topicStatsData.getTopicName();
                TopicResource topicResource = resourceContainer.findTopicResource(topicName, true);
                if (topicResource == null || !topicResource.isProducerAlarm()) {
                    continue;
                }
                ProducerBaseAlarmSetting pBaseAlarmSetting = topicResource.getProducerAlarmSetting();
                if (pBaseAlarmSetting == null) {
                    continue;
                }
                QPSAlarmSetting qps = pBaseAlarmSetting.getQpsAlarmSetting();
                long delay = pBaseAlarmSetting.getDelay();
                if (pBaseAlarmSetting.isQpsAlarm()) {
                    qpsAlarm(topicStatsData, qps);
                }
                if (pBaseAlarmSetting.isDelayAlarm()) {
                    topicStatsData.checkDelay(delay);
                }
                topicStatsData.checkMsgSize(msgSize);
            } catch (Exception e) {
                logger.error("[topicAlarm] topicStatsData {} error.", topicStatsData);
            }
        }
    }

    public boolean qpsAlarm(ProducerTopicStatsData topicStatsData, QPSAlarmSetting qps) {
        if (qps != null) {
            if (!topicStatsData.checkQpsPeak(qps.getPeak())) {
                return false;
            }
            if (!topicStatsData.checkQpsValley(qps.getValley())) {
                return false;
            }
            long preQps = getExpectQps(topicStatsData.getTopicName(), topicStatsData.getTimeKey());
            if (!topicStatsData.checkQpsFlu(qps.getFluctuationBase(), preQps, qps.getFluctuation())) {
                return false;
            }
        }
        return true;
    }

    public long getExpectQps(String topicName, long timeKey) {
        long preDayTimeKey = getPreDayKey(timeKey);
        long startKey = preDayTimeKey - getTimeSection();
        long endKey = preDayTimeKey + getTimeSection();
        List<ProducerTopicStatsData> topicStatsDatas = topicStatsDataService.findSectionData(topicName, startKey,
                endKey);
        int dataCount = 0;
        long sumQps = 0L;
        if (topicStatsDatas != null) {
            for (ProducerTopicStatsData topicStatsData : topicStatsDatas) {
                if (topicStatsData.getQps() > 0L) {
                    sumQps += topicStatsData.getQps();
                    dataCount++;
                }
            }
            if (dataCount > 0) {
                return sumQps / dataCount;
            }
        }
        return 0L;
    }

    @Override
    public void onConfigChange(String key, String value) throws Exception {
        if (MESSAGE_SIZE_KEY.equals(key)) {
            if (StringUtils.isEmpty(value)) {
                msgSize = Long.parseLong(value);
                logger.info("[onConfigChange]" + MESSAGE_SIZE_KEY + " msgSize is " + value);
            } else {
                logger.error("[onConfigChange]" + MESSAGE_SIZE_KEY + " value is null.");
            }
        }
    }
}
