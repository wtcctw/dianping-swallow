package com.dianping.swallow.common.server.monitor.collector;


import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月10日 上午11:56:21
 */
public class DefaultProducerCollector extends AbstractCollector implements ProducerCollector {

    private ProducerMonitorData producerMonitorData = new ProducerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());

    @Override
    public void addMessage(final String topic, final String producerIp, final long messageId, final long msgSize, final long sendTime,
                           final long saveTime) {

        actionWrapper.doAction(new AbstractMonitorDataAction(topic, producerIp, messageId) {

            @Override
            public void doAction() throws SwallowException {

                producerMonitorData.addData(topic, producerIp, messageId, msgSize, sendTime, saveTime);
            }
        });
    }

    @Override
    protected MonitorData getMonitorData() {
        producerMonitorData.buildTotal();
        return producerMonitorData;
    }

    @Override
    protected String getServerType() {

        return "producer";
    }
}
