package com.dianping.swallow.web.alarmer.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.model.alarm.QPSAlarmSetting;
import com.dianping.swallow.web.model.resource.ProducerServerResource;
import com.dianping.swallow.web.model.stats.ProducerServerStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;

/**
 * @author qiyin
 *         <p/>
 *         2015年8月3日 下午6:06:54
 */
@Component
public class ProducerServerStatsAlarmer extends AbstractStatsAlarmer {

    @Autowired
    private ProducerDataRetriever producerDataRetriever;

    @Autowired
    private ProducerStatsDataWapper producerStatsDataWapper;

    @Autowired
    private ResourceContainer resourceContainer;

    private Map<String, Integer> qpsValleyCounts = new ConcurrentHashMap<String, Integer>();

    @Override
    public void doInitialize() throws Exception {
        super.doInitialize();
        producerDataRetriever.registerListener(this);
    }

    @Override
    public void doAlarm() {
        serverAlarm();
    }

    private void serverAlarm() {
        List<ProducerServerStatsData> serverStatsDatas = producerStatsDataWapper.getServerStatsDatas(getLastTimeKey(),
                false);
        if (serverStatsDatas == null || serverStatsDatas.isEmpty()) {
            return;
        }
        for (ProducerServerStatsData serverStatsData : serverStatsDatas) {
            try {
                String ip = serverStatsData.getIp();
                ProducerServerResource pServerResource = resourceContainer.findProducerServerResource(ip);
                if (pServerResource == null || !pServerResource.isAlarm()) {
                    continue;
                }
                QPSAlarmSetting qps = pServerResource.getSaveAlarmSetting();
                if (pServerResource.isQpsAlarm()) {
                    qpsAlarm(serverStatsData, qps);
                }
            } catch (Exception e) {
                logger.error("[serverAlarm] serverStatsData {} error.", serverStatsData);
            }
        }
    }

    private boolean qpsAlarm(ProducerServerStatsData serverStatsData, QPSAlarmSetting qps) {

        if (qps != null) {
            if (!serverStatsData.checkQpsPeak(qps.getPeak())) {
                qpsValleyCounts.put(serverStatsData.getIp(), 0);
                return false;
            }

            Integer valleyCount = qpsValleyCounts.get(serverStatsData.getIp());
            int qpsValleyCount = valleyCount == null ? 0 : valleyCount.intValue();

            if (!serverStatsData.checkQpsValley(qps.getValley(), qpsValleyCount)) {
                qpsValleyCounts.put(serverStatsData.getIp(), qpsValleyCount + 1);
                return false;
            }
        }
        qpsValleyCounts.put(serverStatsData.getIp(), 0);
        return true;
    }

}
