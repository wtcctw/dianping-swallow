package com.dianping.swallow.web.task;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author qi.yin
 *         2015/12/07  上午11:22.
 */
public abstract class AbstractJobTask extends AbstractLifecycle implements TaskLifecycle {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected static final String CAT_TYPE = "JobTask";

    private volatile boolean isOpened = false;

    @Override
    protected void doInitialize() throws Exception {
        super.doInitialize();
        isOpened = true;
    }

    @Override
    protected void doStart() throws Exception {
        super.doStart();
        isOpened = true;
    }

    public boolean isOpened() {
        return isOpened;
    }

    public void setIsOpened(boolean isOpened) {
        this.isOpened = isOpened;
    }

}
