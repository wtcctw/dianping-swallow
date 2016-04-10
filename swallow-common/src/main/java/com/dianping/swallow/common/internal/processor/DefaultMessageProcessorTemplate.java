package com.dianping.swallow.common.internal.processor;

import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.message.SwallowMessage;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年3月27日 下午3:07:10
 */
public class DefaultMessageProcessorTemplate extends AbstractProcessor implements Processor {

    protected MessageProcessorChain processor;

    public DefaultMessageProcessorTemplate() {
        this(false);
    }

    public DefaultMessageProcessorTemplate(boolean gzipBeforeSend) {

        MessageProcessorChain chain = new MessageProcessorChain();
        chain.addProcessor(new PhoenixContextProcessor());
        chain.addProcessor(new GZipProcessor(gzipBeforeSend));
        processor = chain;
    }


    @Override
    public void beforeSend(SwallowMessage message) throws SwallowException {
        processor.beforeSend(message);
    }

    @Override
    public void beforeOnMessage(SwallowMessage message) throws SwallowException {
        processor.beforeOnMessage(message);
    }

    @Override
    public void afterOnMessage(SwallowMessage message) throws SwallowException {
        processor.afterOnMessage(message);
    }

}
