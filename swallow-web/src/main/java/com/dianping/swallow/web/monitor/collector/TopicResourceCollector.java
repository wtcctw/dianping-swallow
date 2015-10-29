package com.dianping.swallow.web.monitor.collector;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.dianping.swallow.web.util.CountDownLatchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.web.model.resource.IpInfo;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.model.stats.ProducerIpStatsData;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.wapper.ProducerStatsDataWapper;
import com.dianping.swallow.web.service.TopicResourceService;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

/**
 * @author qiyin
 *         <p/>
 *         2015年9月30日 上午11:24:59
 */
@Component
public class TopicResourceCollector extends AbstractRealTimeCollector implements MonitorDataListener {

    @Autowired
    private TopicResourceService topicResourceService;

    @Autowired
    private ProducerStatsDataWapper pStatsDataWapper;

    @Autowired
    private ProducerDataRetriever producerDataRetriever;

    private static final String FACTORY_NAME = "ResourceCollector-TopicIpMonitor";

    private IpStatusMonitor<String, ProducerIpStatsData> ipStatusMonitor = new IpStatusMonitorImpl<String, ProducerIpStatsData>();

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
        collectorName = getClass().getSimpleName();
        producerDataRetriever.registerListener(this);
        executor = Executors.newSingleThreadExecutor(ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));
    }

    @Override
    public void achieveMonitorData() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, collectorName + "-IpMonitor");
                catWrapper.doAction(new SwallowAction() {
                    @Override
                    public void doAction() throws SwallowException {
                        doCollector();
                    }
                });
            }
        });
    }

    @Override
    public void doCollector() {
        Set<String> topicNames = pStatsDataWapper.getTopics(false);
        if (topicNames == null) {
            return;
        }
        final CountDownLatch downLatch = CountDownLatchUtil.createCountDownLatch(topicNames.size());
        for (final String topicName : topicNames) {
            try {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            doIpDataMonitor(topicName);
                        } catch (Throwable t) {
                            logger.error("[run] server {} doIpDataMonitor error.", topicName, t);
                        } finally {
                            downLatch.countDown();
                        }

                    }
                });
            } catch (Throwable t) {
                logger.error("[submit] [doCollector] executor thread submit error.", t);
            } finally {
                downLatch.countDown();
            }
        }
        CountDownLatchUtil.await(downLatch);
    }


    private void doIpDataMonitor(String topicName) {
        List<ProducerIpStatsData> ipStatsDatas = pStatsDataWapper.getIpStatsDatas(topicName, -1, false);
        ipStatusMonitor.putActiveIpDatas(topicName, ipStatsDatas);
        updateTopicResource(topicName);
    }

    private void updateTopicResource(String topicName) {
        TopicResource topicResource = null;
        if (ipStatusMonitor.isNeedLoaded(topicName)) {
            topicResource = topicResourceService.findByTopic(topicName);
            if (topicResource == null) {
                return;
            }
            ipStatusMonitor.setLastIpInfos(topicName, topicResource.getProducerIpInfos());
        }
        List<IpInfo> currentIpInfos = ipStatusMonitor.getRelatedIpInfo(topicName);
        if (ipStatusMonitor.isChanged(topicName, currentIpInfos)) {
            topicResource = topicResourceService.findByTopic(topicName);
            if (topicResource == null) {
                return;
            }
            topicResource.setProducerIpInfos(currentIpInfos);
            topicResourceService.update(topicResource);
            ipStatusMonitor.setLastIpInfos(topicName, topicResource.getProducerIpInfos());
            logger.info("[updateTopicIpInfos] topicResource {}", topicResourceService.toString());
        }

    }
}
