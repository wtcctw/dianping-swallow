package com.dianping.swallow.common.internal.processor;

/**
 * @author qi.yin
 *         2016/04/10  下午11:10.
 */
public class ProducerMessageProcessorTemplate extends DefaultMessageProcessorTemplate {

    public ProducerMessageProcessorTemplate() {
    }

    public ProducerMessageProcessorTemplate(boolean gzipBeforeSend) {
        super(gzipBeforeSend);
        this.processor.addProcessor(new MessageSizeProcessor());
    }
}
