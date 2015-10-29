package com.dianping.swallow.web.monitor.collector;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.util.ThreadFactoryUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author qi.yin
 *         2015/10/27  下午4:51.
 */
public abstract class AbstractRegularCollecter extends AbstractResourceCollector {

    private ScheduledFuture<?> future;

    private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(CommonUtils.DEFAULT_CPU_COUNT,
            ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));

    protected int collectorInterval;

    protected int collectorDelay;

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        startCollector();
    }

    public abstract void doCollector();

    public abstract int getCollectorDelay();

    public abstract int getCollectorInterval();

    public void startCollector() {
        future = scheduled.scheduleWithFixedDelay(new Runnable() {

            @Override
            public void run() {
                SwallowActionWrapper catWrapper = new CatActionWrapper(CAT_TYPE, collectorName + "-doCollector");
                catWrapper.doAction(new SwallowAction() {
                    @Override
                    public void doAction() throws SwallowException {
                        doCollector();
                    }
                });
            }

        }, getCollectorDelay(), getCollectorInterval(), TimeUnit.MINUTES);
    }

    @Override
    protected void doStop() throws Exception {
        super.doStop();
        if (future != null && !future.isCancelled()) {
            future.cancel(false);
        }

    }

    protected void doDispose() throws Exception {
        super.doDispose();
        if (scheduled != null && !scheduled.isShutdown()) {
            scheduled.shutdown();
        }
    }

    public ScheduledFuture<?> getFuture() {
        return future;
    }

    public void setFuture(ScheduledFuture<?> future) {
        this.future = future;
    }
}
