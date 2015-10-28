package com.dianping.swallow.web.alarmer.impl;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.codehaus.plexus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.model.event.ClientType;
import com.dianping.swallow.web.model.event.ConsumerClientEvent;
import com.dianping.swallow.web.model.event.EventType;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ConsumerIpGroupStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData;
import com.dianping.swallow.web.model.stats.ConsumerIpStatsData.ConsumerIpStatsDataKey;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService;
import com.dianping.swallow.web.service.ConsumerIpStatsDataService.ConsumerIpQpsPair;

/**
 * @author qiyin
 *         <p/>
 *         2015年9月17日 下午8:24:14
 */
@Component
public class ConsumerIpStatsAlarmer extends
        AbstractIpStatsAlarmer<ConsumerIpStatsDataKey, ConsumerIpStatsData, ConsumerIpGroupStatsData> {

    @Autowired
    private ConsumerDataRetriever consumerDataRetriever;

    @Autowired
    private ConsumerStatsDataWapper cStatsDataWapper;

    @Autowired
    private ConsumerIpStatsDataService cIpStatsDataService;

    @Override
    public void doInitialize() throws Exception {
        super.doInitialize();
        checkInterval = 10 * 60 * 1000;
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
            Set<String> consumerIds = cStatsDataWapper.getConsumerIds(topicName, false);
            if (consumerIds == null) {
                continue;
            }
            for (String consumerId : consumerIds) {
                ConsumerIpGroupStatsData ipGroupStatsData = cStatsDataWapper.getIpGroupStatsDatas(topicName,
                        consumerId, getLastTimeKey(), false);
                checkIpGroup(ipGroupStatsData);
            }
        }
        alarmSureRecords();
        alarmUnSureRecords();
    }

    @Override
    protected void checkUnSureLastRecords(ConsumerIpStatsDataKey statsDataKey) {
        ConsumerIpQpsPair avgQpsPair = cIpStatsDataService.findAvgQps(statsDataKey.getTopicName(),
                statsDataKey.getConsumerId(), statsDataKey.getIp(), getTimeKey(getPreNDayKey(1, checkInterval)),
                getTimeKey(getPreNDayKey(1, 0)));

        if (avgQpsPair.getSendQps() > 0 || avgQpsPair.getAckQps() > 0) {
            report(statsDataKey);
        }
    }

    @Override
    protected boolean isReport(ConsumerIpStatsDataKey statsDataKey) {
        TopicResource topicResource = resourceContainer.findTopicResource(statsDataKey.getTopicName());
        ConsumerIdResource ConsumerIdResource = resourceContainer.findConsumerIdResource(statsDataKey.getTopicName(),
                statsDataKey.getConsumerId());
        if (!topicResource.isConsumerAlarm()) {
            return false;
        }
        if (ConsumerIdResource.isAlarm()) {
            List<IpInfo> ipInfos = ConsumerIdResource.getConsumerIpInfos();
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
        } else {
            return false;
        }
    }

    @Override
    protected void report(ConsumerIpStatsDataKey statsDataKey) {
        if (isReport(statsDataKey)) {
            ConsumerClientEvent clientEvent = eventFactory.createCClientEvent();
            clientEvent.setConsumerId(statsDataKey.getConsumerId()).setTopicName(statsDataKey.getTopicName())
                    .setIp(statsDataKey.getIp()).setClientType(ClientType.CLIENT_RECEIVER)
                    .setEventType(EventType.CONSUMER).setCreateTime(new Date()).setCheckInterval(checkInterval);
            eventReporter.report(clientEvent);
        }
    }
}
