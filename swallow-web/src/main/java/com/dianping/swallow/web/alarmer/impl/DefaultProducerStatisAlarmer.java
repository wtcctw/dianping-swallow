package com.dianping.swallow.web.alarmer.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.web.alarmer.ProducerStatisAlarmer;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;

/**
 *
 * @author qiyin
 *
 */
public class DefaultProducerStatisAlarmer extends AbstractStatisAlarmer implements ProducerStatisAlarmer {

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Override
	protected void doAlarm() {

	}

	public void doServerQPSAlarm() {
		Map<String, StatsData> serverQpxs = producerDataRetriever.getServerQpx(QPX.SECOND);
		for (Map.Entry<String, StatsData> serverQpx : serverQpxs.entrySet()) {

		}
	}

	public void doTopicQPSAlarm(){
		Set<String> topics = producerDataRetriever.getTopics();
		Iterator itetator = topics.iterator(); 
		while(itetator.hasNext()){
			String topic = String.valueOf(itetator.next());
			producerDataRetriever.getQpx(topic, QPX.SECOND);
		}
	}
	
	public void doIPQPSAlarm(){
		
	}
	
	public void doServerDelayAlarm() {
		
	}

	public void doTopicDelayAlarm() {

	}

}
