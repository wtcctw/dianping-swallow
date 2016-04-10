package com.dianping.swallow.common.server.monitor;


import com.dianping.swallow.common.internal.util.IPUtil;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.common.server.monitor.data.structure.ProducerMonitorData;

/**
 * @author mengwenchao
 *         <p/>
 *         2015年4月14日 下午10:24:34
 */
public class ProducerMonitorDataTest extends AbstractMonitorDataTest {


    protected Class<? extends MonitorData> getMonitorClass() {

        return ProducerMonitorData.class;
    }

    protected String getJsonString() {

        MonitorData monitorData = createMonitorData();

        return monitorData.jsonSerialize();
    }


    protected MonitorData createMonitorData() {

        ProducerMonitorData producerMonitorData = new ProducerMonitorData(IPUtil.getFirstNoLoopbackIP4Address());
        producerMonitorData.addData("topic", ip, 1L, 50L, System.currentTimeMillis() - 100, System.currentTimeMillis());

        return producerMonitorData;

    }

    @Override
    protected String getUrl() {

        return "http://127.0.0.1:8080/api/stats/producer";
    }

    @Override
    protected void checkTotal(MonitorData monitorData) {
        // TODO Auto-generated method stub

    }

    @Override
    protected void addMessages(MonitorData monitorData) {
        // TODO Auto-generated method stub

    }

}
