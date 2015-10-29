package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author qi.yin
 *         2015/10/27  下午4:52.
 */
public abstract class AbstractRealTimeCollector extends AbstractResourceCollector {

    protected static ExecutorService executor = Executors.newFixedThreadPool(CommonUtils.DEFAULT_CPU_COUNT,
            ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
    }

    public abstract void doCollector();

    protected void doDispose() throws Exception {
        super.doDispose();
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
        }
    }
}
