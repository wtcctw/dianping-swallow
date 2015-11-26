package com.dianping.swallow.web.monitor.collector;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.controller.listener.ResourceListener;
import com.dianping.swallow.web.controller.listener.ResourceObserver;
import com.dianping.swallow.web.model.resource.BaseResource;
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
public class TopicResourceCollector extends AbstractRealTimeCollector implements MonitorDataListener, ResourceObserver {

    @Autowired
    private TopicResourceService topicResourceService;

    @Autowired
    private ProducerStatsDataWapper pStatsDataWapper;

    @Autowired
    private ProducerDataRetriever producerDataRetriever;

    @Autowired
    private ResourceContainer resourceContainer;

    private static final String FACTORY_NAME = "ResourceCollector-TopicIpMonitor";

    private IpStatusMonitor<String, ProducerIpStatsData> ipStatusMonitor = new IpStatusMonitorImpl<String, ProducerIpStatsData>();

    private List<ResourceListener> listeners = new ArrayList<ResourceListener>();

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
        TopicResource topicResource = resourceContainer.findTopicResource(topicName, false);
        if (topicResource != null) {
            List<IpInfo> currentIpInfos = ipStatusMonitor.getRelatedIpInfo(topicName, topicResource.getProducerIpInfos());
            if (ipStatusMonitor.isChanged(topicResource.getProducerIpInfos(), currentIpInfos)) {
                topicResource.setProducerIpInfos(currentIpInfos);
                boolean result = topicResourceService.update(topicResource);
                if (result) {
                    doUpdateNotify(topicResource);
                }
                logger.info("[updateTopicIpInfos] topicResource {}", topicResourceService.toString());
            }
        } else {
            logger.info("[updateTopicResource] resourceContainer no topicResource {}", topicName);
        }
    }

    @Override
    public void doRegister(ResourceListener listener) {
        listeners.add(listener);
    }

    @Override
    public void doUpdateNotify(BaseResource resource) {
        for (ResourceListener listener : listeners) {
            listener.doUpdateNotify(resource);
        }
    }

    @Override
    public void doDeleteNotify(BaseResource resource) {
        for (ResourceListener listener : listeners) {
            listener.doDeleteNotify(resource);
        }
    }
}
