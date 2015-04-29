package com.dianping.swallow.web.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.dianping.swallow.web.monitor.ConsumerDataRetriever.ConsumerDataPair;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.charts.ChartBuilder;
import com.dianping.swallow.web.monitor.charts.HighChartsWrapper;


/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午9:24:38
 */
@Controller
public class DataMonitorController extends AbstractMonitorController{
	
	
	public static final String Y_AXIS_TYPE_QPS = "QPS";
	
	public static final String Y_AXIS_TYPE_DELAY = "延时(毫秒)";

	
	@Autowired
	private ProducerDataRetriever producerDataRetriever;
	
	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@RequestMapping(value = "/console/monitor/consumerserver/qps", method = RequestMethod.GET)
	public ModelAndView viewConsumerServerQps(){
		
		subSide = "consumerserverqps";
		return new ModelAndView("monitor/consumerserverqps", createViewMap());
	} 

	@RequestMapping(value = "/console/monitor/producerserver/qps", method = RequestMethod.GET)
	public ModelAndView viewProducerServerQps(){
		
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
	@ResponseBody
	public List<HighChartsWrapper> getConsumerServerQps(){
		
		subSide = "consumerserverqps";
		Map<String, ConsumerDataPair> serverQpx = consumerDataRetriever.getServerQpx(QPX.SECOND);
		
		return buildHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	} 


	@RequestMapping(value = "/console/monitor/producerserver/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getProducerServerQps(){
		
		subSide = "producerserverqps";
		Map<String, StatsData> serverQpx = producerDataRetriever.getServerQpx(QPX.SECOND);
		
		return buildConsumerChartWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	} 
	
	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicQps(@PathVariable String topic){
		
		StatsData producerData = producerDataRetriever.getQpx(topic, QPX.SECOND);
		List<ConsumerDataPair> consumerData = consumerDataRetriever.getQpxForAllConsumerId(topic, QPX.SECOND);
		
		return buildConsumerChartWrapper(topic, Y_AXIS_TYPE_QPS, producerData, consumerData);
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


	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerDelayMonitor(@PathVariable String topic) throws IOException{

		StatsData 		 producerData = producerDataRetriever.getSaveDelay(topic); 
		List<ConsumerDataPair>  consumerDelay = consumerDataRetriever.getDelayForAllConsumerId(topic);
		
		return  buildConsumerChartWrapper(topic, Y_AXIS_TYPE_DELAY, producerData, consumerDelay); 
		
	}

	private List<HighChartsWrapper> buildConsumerChartWrapper(String yAxis, Map<String, StatsData> serverQpx) {

		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(serverQpx.size());
		for(Entry<String, StatsData> entry : serverQpx.entrySet()){
			
			String ip = entry.getKey();
			StatsData statsData = entry.getValue();
			
			result.add(ChartBuilder.getHighChart(ip, "", yAxis, statsData));

		}

		return result;
	}
	
	private List<HighChartsWrapper> buildHighChartsWrapper(String yAxis, Map<String, ConsumerDataPair> serverQpx) {

		int size = serverQpx.size();
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(size);
		
		for(Entry<String, ConsumerDataPair> entry : serverQpx.entrySet()){
			String ip = entry.getKey();
			ConsumerDataPair dataPair = entry.getValue();
			List<StatsData> allStats = new LinkedList<StatsData>();
			allStats.add(dataPair.getSendData());
			allStats.add(dataPair.getAckData());
			result.add(ChartBuilder.getHighChart(ip, "", yAxis, allStats));
		}
			
		return result;
	}


	private List<HighChartsWrapper> buildConsumerChartWrapper(String topic, String yAxis, StatsData producerData, List<ConsumerDataPair>consumerData) {
		
		int size = consumerData.size();
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(size);
		
		for(int i=0; i<consumerData.size();i++){
			
			ConsumerDataPair dataPair = consumerData.get(i);
			String currentConsumerId = dataPair.getConsumerId();
			
			List<StatsData> allStats = new LinkedList<StatsData>();
			allStats.add(producerData);
			allStats.add(dataPair.getSendData());
			allStats.add(dataPair.getAckData());
			result.add(ChartBuilder.getHighChart(topic, currentConsumerId, yAxis, allStats));
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
