package com.dianping.swallow.consumer;

import com.dianping.swallow.common.internal.observer.impl.AbstractObservable;
import com.dianping.swallow.common.internal.util.SwallowHelper;
import com.dianping.swallow.common.message.Destination;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @author qi.yin
 *         2015/12/29  下午4:28.
 */
public abstract class AbstractConsumerFactory extends AbstractObservable implements ConsumerFactory {

    protected Logger logger = LogManager.getLogger(getClass());

    static {

        SwallowHelper.clientInitialize();
    }

}
