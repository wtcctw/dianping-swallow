package com.dianping.swallow.web.alarmer.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ConsumerServerResource;
import com.dianping.swallow.web.model.stats.ConsumerServerStatsData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ConsumerStatsDataWapper;

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

    private Map<String, ConsumerValleyCount> qpsValleyCounts = new ConcurrentHashMap<String, ConsumerValleyCount>();

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
                if (cServerResource.isQpsAlarm()) {
                    qpsSendAlarm(serverStatsData, sendQps);
                    qpsAckAlarm(serverStatsData, ackQps);
                }

            } catch (Exception e) {
                logger.error("[serverAlarm] serverStatsData {} error.", serverStatsData);
            }
        }
    }

    private boolean qpsSendAlarm(ConsumerServerStatsData serverStatsData, QPSAlarmSetting qps) {

        ConsumerValleyCount valleyCount;
        if (qpsValleyCounts.containsKey(serverStatsData.getIp())) {
            valleyCount = qpsValleyCounts.get(serverStatsData.getIp());
        } else {
            valleyCount = new ConsumerValleyCount();
            qpsValleyCounts.put(serverStatsData.getIp(), valleyCount);
        }

        if (qps != null) {
            if (!serverStatsData.checkSendQpsPeak(qps.getPeak())) {
                valleyCount.setSendQpsValleyCount(0);
                return false;
            }

            if (!serverStatsData.checkSendQpsValley(qps.getValley(), valleyCount.getSendQpsValleyCount())) {
                valleyCount.incSendQpsValleyCount();
                return false;
            }
        }
        valleyCount.setSendQpsValleyCount(0);
        return true;
    }

    private boolean qpsAckAlarm(ConsumerServerStatsData serverStatsData, QPSAlarmSetting qps) {
        ConsumerValleyCount valleyCount;
        if (qpsValleyCounts.containsKey(serverStatsData.getIp())) {
            valleyCount = qpsValleyCounts.get(serverStatsData.getIp());
        } else {
            valleyCount = new ConsumerValleyCount();
            qpsValleyCounts.put(serverStatsData.getIp(), valleyCount);
        }

        if (qps != null) {
            if (!serverStatsData.checkAckQpsPeak(qps.getPeak())) {
                valleyCount.setAckQpsValleyCount(0);
                return false;
            }
            if (!serverStatsData.checkAckQpsValley(qps.getValley(), valleyCount.getAckQpsValleyCount())) {
                valleyCount.incAckQpsValleyCount();
                return false;
            }
        }
        valleyCount.setAckQpsValleyCount(0);
        return true;
    }

    class ConsumerValleyCount {

        private int ackQpsValleyCount = 0;

        private int sendQpsValleyCount = 0;

        public int getAckQpsValleyCount() {
            return ackQpsValleyCount;
        }

        public void setAckQpsValleyCount(int ackQpsValleyCount) {
            this.ackQpsValleyCount = ackQpsValleyCount;
        }

        public int getSendQpsValleyCount() {
            return sendQpsValleyCount;
        }

        public void setSendQpsValleyCount(int sendQpsValleyCount) {
            this.sendQpsValleyCount = sendQpsValleyCount;
        }

        public void incSendQpsValleyCount() {
            this.sendQpsValleyCount++;
        }

        public void incAckQpsValleyCount() {
            this.ackQpsValleyCount++;
        }
    }

}
