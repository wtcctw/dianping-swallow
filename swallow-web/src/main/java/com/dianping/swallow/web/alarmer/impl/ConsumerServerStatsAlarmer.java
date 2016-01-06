package com.dianping.swallow.web.alarmer.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;
import com.dianping.swallow.web.service.ConsumerServerStatsDataService;

/**
 * @author qiyin
 *         <p/>
 *         2015年9月17日 下午8:24:46
 */
@Component
public class ConsumerServerStatsAlarmer extends AbstractStatsAlarmer {

    @Autowired
    private ConsumerDataRetriever consumerDataRetriever;

    @Autowired
    private ConsumerStatsDataWapper consumerStatsDataWapper;

    @Autowired
    private ResourceContainer resourceContainer;

    private int ackQpsValleyCount = 0;
    private int sendQpsValleyCount = 0;

    @Override
    public void doInitialize() throws Exception {
        super.doInitialize();
        consumerDataRetriever.registerListener(this);
    }

    @Override
    public void doAlarm() {
        serverAlarm();
    }

    private void serverAlarm() {
        List<ConsumerServerStatsData> serverStatsDatas = consumerStatsDataWapper.getServerStatsDatas(getLastTimeKey(),
                false);
        if (serverStatsDatas == null) {
            return;
        }
        for (ConsumerServerStatsData serverStatsData : serverStatsDatas) {
            try {
                String ip = serverStatsData.getIp();
                ConsumerServerResource cServerResource = resourceContainer.findConsumerServerResource(ip);
                if (cServerResource == null || !cServerResource.isAlarm()) {
                    continue;
                }
                QPSAlarmSetting sendQps = cServerResource.getSendAlarmSetting();
                QPSAlarmSetting ackQps = cServerResource.getAckAlarmSetting();

                qpsSendAlarm(serverStatsData, sendQps);
                qpsAckAlarm(serverStatsData, ackQps);

            } catch (Exception e) {
                logger.error("[serverAlarm] serverStatsData {} error.", serverStatsData);
            }
        }
    }

    private boolean qpsSendAlarm(ConsumerServerStatsData serverStatsData, QPSAlarmSetting qps) {
        if (qps != null) {
            if (!serverStatsData.checkSendQpsPeak(qps.getPeak())) {
                sendQpsValleyCount = 0;
                return false;
            }
            if (!serverStatsData.checkSendQpsValley(qps.getValley())) {
                sendQpsValleyCount++;
                if (sendQpsValleyCount > 3) {
                    return false;
                }
                return true;
            }
        }
        sendQpsValleyCount = 0;
        return true;
    }

    private boolean qpsAckAlarm(ConsumerServerStatsData serverStatsData, QPSAlarmSetting qps) {
        if (qps != null) {
            if (!serverStatsData.checkAckQpsPeak(qps.getPeak())) {
                ackQpsValleyCount = 0;
                return false;
            }
            if (!serverStatsData.checkAckQpsValley(qps.getValley())) {
                ackQpsValleyCount++;
                if (ackQpsValleyCount > 3) {
                    return false;
                }
                return true;
            }
        }
        ackQpsValleyCount = 0;
        return true;
    }

}
