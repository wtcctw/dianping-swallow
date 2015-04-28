package com.dianping.swallow.web.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
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

import com.dianping.swallow.common.server.monitor.visitor.QPX;
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

	@RequestMapping(value = "/console/monitor/consumerserver/qps", method = RequestMethod.GET)
	public ModelAndView viewConsumerServerQps(@PathVariable String topic){
		
		subSide = "consumerserverqps";
		return new ModelAndView("monitor/consumerserverqps", createViewMap());
	} 

	@RequestMapping(value = "/console/monitor/producerserver/qps", method = RequestMethod.GET)
	public ModelAndView viewProducerServerQps(@PathVariable String topic){
		
		subSide = "producerserverqps";
		return new ModelAndView("monitor/producerserverqps", createViewMap());
	} 

	

	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps", method = RequestMethod.GET)
	public ModelAndView viewTopicQps(@PathVariable String topic){
		
		subSide = "consumerqps";
		return new ModelAndView("monitor/consumerqps", createViewMap());
	} 

	@RequestMapping(value = "/console/monitor/producer/{topic}/savedelay", method = RequestMethod.GET)
	public ModelAndView viewProducerDelayMonitor(@PathVariable String topic) throws IOException{

		Map<String, Object> map = createViewMap();
		return new ModelAndView("monitor/producerdelay", map);

	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay", method = RequestMethod.GET)
	public ModelAndView viewConsumerDelayMonitor(@PathVariable String topic) throws IOException{

		subSide = "delay";
		Map<String, Object> map = createViewMap();
		return new ModelAndView("monitor/consumerdelay", map);
	}

	
	@RequestMapping(value = "/console/monitor/consumerserver/qps/get", method = RequestMethod.POST)
	public List<HighChartsWrapper> viewConsumerServerQps(){
		
		return null;
	} 

	@RequestMapping(value = "/console/monitor/producerserver/qps/get", method = RequestMethod.POST)
	public List<HighChartsWrapper> viewProducerServerQps(@PathVariable String topic){
		
		subSide = "producerserverqps";
		return new ModelAndView("monitor/producerserverqps", createViewMap());
	} 
	

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicQps(@PathVariable String topic){
		
		StatsData producerData = producerDataRetriever.getQpx(topic, QPX.SECOND);
		List<StatsData> consumerSendData = consumerDataRetriever.getSendQpxForAllConsumerId(topic, QPX.SECOND);
		List<StatsData> consumerAckData = consumerDataRetriever.getAckdQpxForAllConsumerId(topic, QPX.SECOND);
		
		return buildConsumerChartWrapper(topic, producerData, consumerSendData, consumerAckData);
	} 

	

	@RequestMapping(value = "/console/monitor/topiclist/get", method = RequestMethod.POST)
	@ResponseBody
	public Set<String> getProducerDelayMonitor() throws IOException{
		return  allTopics();
	}

	private Set<String> allTopics() {
		
		Set<String> producerTopics = producerDataRetriever.getTopics();
		Set<String> consumerTopics = consumerDataRetriever.getTopics();
		producerTopics.addAll(consumerTopics);
		return producerTopics;
	}


	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerDelayMonitor(@PathVariable String topic) throws IOException{

		StatsData 		 producerData = producerDataRetriever.getSaveDelay(topic); 
		List<StatsData>  consumerSendDelay = consumerDataRetriever.getSendDelayForAllConsumerId(topic);
		List<StatsData>  consumerAckDelay = consumerDataRetriever.getAckDelayForAllConsumerId(topic);
		
		return  buildConsumerChartWrapper(topic, producerData, consumerSendDelay, consumerAckDelay); 
		
	}

	private List<HighChartsWrapper> buildConsumerChartWrapper(String topic, StatsData producerData, List<StatsData> ...consumerData) {
		
		int maxSize = -1;
		List<StatsData> maxConsumerData = null;
		for(List<StatsData> item : consumerData){
			if(item.size() > maxSize){
				maxSize = item.size();
				maxConsumerData = item;
			}
		}
		
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(maxSize);
		
		for(int i=0;i<maxConsumerData.size();i++){
			
			StatsData statsData = maxConsumerData.get(i);
			ConsumerStatsDataDesc currentDesc = (ConsumerStatsDataDesc) statsData.getInfo();
			String currentConsumerId = currentDesc.getConsumerId();
			
			List<StatsData> allStats = new LinkedList<StatsData>();
			allStats.add(producerData);
			allStats .add(statsData);
			
			for(int j=0; j < consumerData.length; j++){
				List<StatsData> cmp = consumerData[j];
				if(maxConsumerData == cmp){
					continue;
				}
				for(int k=0;k<cmp.size();k++){
					StatsData cmpStatsData = cmp.get(k);
					ConsumerStatsDataDesc cmpDesc = (ConsumerStatsDataDesc) cmpStatsData.getInfo();
					if(cmpDesc.getConsumerId().equals(currentConsumerId)){
						allStats .add(cmpStatsData);
						break;
					}
				}
			}
			result.add(ChartBuilder.getHighChart(topic, currentConsumerId, allStats));
		}
		
		return result;
	}

	@Override
	protected String getSide() {
		return "delay";
	}

	private String subSide = "delay";
	
	@Override
	public String getSubSide() {
		return subSide;
	}

}
