package com.dianping.swallow.web.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.charts.ChartBuilder;
import com.dianping.swallow.web.monitor.charts.HighChartsWrapper;
import com.dianping.swallow.web.monitor.impl.ConsumerStatsDataDesc;


/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午9:24:38
 */
@Controller
public class DataMonitorController extends AbstractMonitorController{
	
	
	@Autowired
	private ProducerDataRetriever producerDataRetriever;
	
	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;
	
	@RequestMapping(value = "/console/monitor/producer/{topic}/savedelay", method = RequestMethod.GET)
	public ModelAndView viewProducerDelayMonitor(@PathVariable String topic) throws IOException{

		Map<String, Object> map = createViewMap();
		return new ModelAndView("monitor/producerdelay", map);

	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay", method = RequestMethod.GET)
	public ModelAndView viewConsumerDelayMonitor(@PathVariable String topic) throws IOException{
		
		Map<String, Object> map = createViewMap();
		return new ModelAndView("monitor/consumerdelay", map);
	}

	
	
	@RequestMapping(value = "/console/monitor/topiclist/get", method = RequestMethod.POST)
	@ResponseBody
	public Set<String> getProducerDelayMonitor() throws IOException{
		
		Set<String> producerTopics = producerDataRetriever.getTopics();
		Set<String> consumerTopics = consumerDataRetriever.getTopics();
		producerTopics.addAll(consumerTopics);
		return  producerTopics;
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerDelayMonitor(@PathVariable String topic) throws IOException{

		StatsData 		 producerData = producerDataRetriever.getSaveDelay(topic); 
		List<StatsData>  consumerSendDelay = consumerDataRetriever.getSendDelayForAllConsumerId(topic);
		List<StatsData>  consumerAckDelay = consumerDataRetriever.getAckDelayForAllConsumerId(topic);
		
		return  buildConsumerChartWrapper(topic, producerData, consumerSendDelay, consumerAckDelay); 
		
	}

	private List<HighChartsWrapper> buildConsumerChartWrapper(String topic, StatsData producerData,
			List<StatsData> consumerSendDelay, List<StatsData> consumerAckDelay) {
		
		
		if(consumerSendDelay.size()  != consumerAckDelay.size()){
			throw new IllegalArgumentException("[sendDelaySize != ackDelaySize] " + consumerSendDelay.size() + " VS " + consumerAckDelay.size());
		}
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(consumerSendDelay.size());
		
		for(int i=0;i<consumerSendDelay.size();i++){
			
			StatsData send = consumerSendDelay.get(i);
			StatsData ack  = consumerAckDelay.get(i);
			
			ConsumerStatsDataDesc sendInfo = (ConsumerStatsDataDesc) send.getInfo();
			ConsumerStatsDataDesc ackInfo = (ConsumerStatsDataDesc) ack.getInfo();
			
			if(!sendInfo.getConsumerId().equals(ackInfo.getConsumerId())){
				throw new IllegalStateException("consumerId not equal to each other!!");
			}
			
			String consumerId = sendInfo.getConsumerId(); 
					
			result.add(ChartBuilder.getHighChart(topic, consumerId, producerData, send, ack));
		}
		
		return result;
	}

	@Override
	protected String getSide() {
		return "delay";
	}

	@Override
	public String getSubSide() {
		return "all";
	}

}
