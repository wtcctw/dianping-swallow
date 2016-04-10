package com.dianping.swallow.common.server.monitor.collector;


/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月10日 上午11:51:05
 */
public interface ProducerCollector extends Collector {

    void addMessage(String topic, String producerIp, long messageId, long msgSize, long sendTime, long saveTime);

}
