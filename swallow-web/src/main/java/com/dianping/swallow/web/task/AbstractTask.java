package com.dianping.swallow.web.task;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

/**
 * @author qi.yin
 *         2015/12/07  上午11:22.
 */
public abstract class AbstractTask extends AbstractLifecycle implements TaskLifecycle {

    protected final Logger logger = LogManager.getLogger(getClass());

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
