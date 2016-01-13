package com.dianping.swallow.web.alarmer.impl;

import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.event.ClientType;
import com.dianping.swallow.web.model.event.ConsumerClientEvent;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ConsumerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService;
import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author qi.yin
 *         2016/01/12  上午10:27.
 */
@Component
public class ConsumerIpStatsAlarmer extends
        AbstractIpStatsAlarmer<ConsumerIpStatsData.ConsumerIpStatsDataKey, ConsumerIpStatsData, ConsumerIpGroupStatsData> {

    @Autowired
    private ConsumerDataRetriever consumerDataRetriever;

    @Autowired
    private ConsumerStatsDataWapper cStatsDataWapper;

    @Autowired
    private ConsumerIpStatsDataService cIpStatsDataService;

    @Override
    public void doInitialize() throws Exception {
        super.doInitialize();
        consumerDataRetriever.registerListener(this);
    }

    @Override
    public void doAlarm() {
        alarmIpData();
    }

    public void alarmIpData() {
        Set<String> topicNames = cStatsDataWapper.getTopics(false);
        if (topicNames == null) {
            return;
        }

        for (String topicName : topicNames) {
            TopicResource topicResource = resourceContainer.findTopicResource(topicName, true);
            if (topicResource != null && !topicResource.isConsumerAlarm()) {
                continue;
            }
            Set<String> consumerIds = cStatsDataWapper.getConsumerIds(topicName, false);
            if (consumerIds == null) {
                continue;
            }

            for (String consumerId : consumerIds) {
                ConsumerIdResource consumerIdResource = resourceContainer.findConsumerIdResource(topicName, consumerId, true);
                if (consumerIdResource == null || !consumerIdResource.isAlarm()) {
                    continue;
                }
                ConsumerBaseAlarmSetting alarmSetting = consumerIdResource.getConsumerAlarmSetting();
                if (alarmSetting == null || !alarmSetting.isIpAlarm()) {
                    continue;
                }

                List<ConsumerIpStatsData> ipStatsDatas = cStatsDataWapper.getIpStatsDatas(topicName,
                        consumerId, getLastTimeKey(), false);
                Map<String, ConsumerIpGroupStatsData> ipGroupStatsDatas = getIpGroupStatsData(ipStatsDatas);
                if (ipGroupStatsDatas == null || ipGroupStatsDatas.isEmpty()) {
                    continue;
                }

                for (Map.Entry<String, ConsumerIpGroupStatsData> ipGroupStatsData : ipGroupStatsDatas.entrySet()) {
                    checkClusterStats(ipGroupStatsData.getValue());
                }
            }
        }
        alarmIpStatsData();
    }

    @Override
    protected void checkUnClusterStats(ConsumerIpStatsData.ConsumerIpStatsDataKey statsDataKey) {
        List<ConsumerIpStatsData> ipStatsDatas = cIpStatsDataService.find(statsDataKey.getTopicName(),
                statsDataKey.getConsumerId(), statsDataKey.getIp(), getTimeKey(getPreNDayKey(1, unClusterCheckInterval)),
                getTimeKey(getPreNDayKey(1, 0)));

        checkUnClusterStats0(statsDataKey, ipStatsDatas);
    }

    @Override
    protected boolean isReport(ConsumerIpStatsData.ConsumerIpStatsDataKey statsDataKey) {
        TopicResource topicResource = resourceContainer.findTopicResource(statsDataKey.getTopicName(), true);
        if (topicResource == null || !topicResource.isConsumerAlarm()) {
            return false;
        }

        ConsumerIdResource consumerIdResource = resourceContainer.findConsumerIdResource(statsDataKey.getTopicName(),
                statsDataKey.getConsumerId(), true);
        if (consumerIdResource == null || !consumerIdResource.isAlarm()) {
            return false;
        }

        List<IpInfo> ipInfos = consumerIdResource.getConsumerIpInfos();
        if (ipInfos == null || ipInfos.isEmpty()) {
            return true;
        }

        if (StringUtils.isNotBlank(statsDataKey.getIp())) {
            for (IpInfo ipInfo : ipInfos) {
                if (statsDataKey.getIp().equals(ipInfo.getIp())) {
                    return ipInfo.isActiveAndAlarm();
                }
            }
        }
        return true;
    }

    @Override
    protected void report(ConsumerIpStatsData.ConsumerIpStatsDataKey statsDataKey, long checkInterval) {
        if (isReport(statsDataKey)) {

            ConsumerClientEvent clientEvent = eventFactory.createCClientEvent();

            clientEvent.setConsumerId(statsDataKey.getConsumerId()).setTopicName(statsDataKey.getTopicName())
                    .setIp(statsDataKey.getIp()).setClientType(ClientType.CLIENT_RECEIVER)
                    .setEventType(EventType.CONSUMER).setCreateTime(new Date()).setCheckInterval(checkInterval);

            eventReporter.report(clientEvent);

        }
    }

    @Override
    protected ConsumerIpGroupStatsData createIpGroupStatsData() {
        return new ConsumerIpGroupStatsData();
    }
}

