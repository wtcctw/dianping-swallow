package com.dianping.swallow.web.controller;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.common.internal.action.SwallowCallableWrapper;
import com.dianping.swallow.common.internal.action.impl.CatCallableWrapper;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever.ConsumerDataPair;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.charts.ChartBuilder;
import com.dianping.swallow.web.monitor.charts.HighChartsWrapper;
import com.dianping.swallow.web.task.TopicScanner;


/**
 * @author mengwenchao
 *
 * 2015年4月14日 下午9:24:38
 */
@Controller
public class DataMonitorController extends AbstractMonitorController{
	
	
	public static final String Y_AXIS_TYPE_QPS = "QPS";
	
	public static final String Y_AXIS_TYPE_DELAY = "延时(毫秒)";

	public static final String Y_AXIS_TYPE_ACCUMULATION = "堆积消息数";

	public static final String CAT_TYPE = "MONITOR";
	
	
	@Autowired
	private ProducerDataRetriever producerDataRetriever;
	
	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Autowired
	private TopicScanner topicScanner;
	
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

	@RequestMapping(value = "/console/monitor/consumer/{topic}/accu", method = RequestMethod.GET)
	public ModelAndView viewTopicAccumulation(@PathVariable String topic){
		
		subSide = "consumeraccu";
		
		if(topic.equals(MonitorData.TOTAL_KEY)){
			String firstTopic = getFirstTopic(accumulationRetriever.getTopics());
			if(!firstTopic.equals(MonitorData.TOTAL_KEY)){
				return new ModelAndView("redirect:/console/monitor/consumer/" + firstTopic + "/accu", createViewMap());
			}
		}
		return new ModelAndView("monitor/consumeraccu", createViewMap());
	} 

	
	private String getFirstTopic(Set<String> topics) {
		
		if(topics == null || topics.size() == 0){
			
			return MonitorData.TOTAL_KEY;
		}
		return topics.toArray(new String[0])[0];
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


	@RequestMapping(value = "/console/monitor/consumer/debug/{server}", method = RequestMethod.GET)
	@ResponseBody
	public String getConsumerDebug(@PathVariable String server){
		
		if(logger.isInfoEnabled()){
			logger.info("[getConsumerDebug]" + server);
			logger.info(consumerDataRetriever.getDebugInfo(server));
		}
		return "ok";
	}
	
	@RequestMapping(value = "/console/monitor/scanner/debug", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Set<String>> getScannerDebug(){
		
		return topicScanner.getTopics();
	} 

	
	@RequestMapping(value = "/console/monitor/producer/debug/{server}", method = RequestMethod.GET)
	@ResponseBody
	public String getProducerDebug(@PathVariable String server){
		
		if(logger.isInfoEnabled()){
			logger.info("[getProducerDebug]" + server);
			logger.info(producerDataRetriever.getDebugInfo(server));
		}
		return "ok";
	} 

	
	@RequestMapping(value = "/console/monitor/consumerserver/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerServerQps(){
				
		subSide = "consumerserverqps";
		Map<String, ConsumerDataPair> serverQpx = consumerDataRetriever.getServerQpx(QPX.SECOND);
		
		return buildConsumerHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	} 

	
	@RequestMapping(value = "/console/monitor/producerserver/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getProducerServerQps(){
		
		subSide = "producerserverqps";
		Map<String, StatsData> serverQpx = producerDataRetriever.getServerQpx(QPX.SECOND);
		
		return buildStatsHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	} 

	@RequestMapping(value = "/console/monitor/topiclist/get", method = RequestMethod.POST)
	@ResponseBody
	public Set<String> getProducerDelayMonitor() throws IOException{
		return  allTopics();
	}

	
	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicQps(@PathVariable String topic, @RequestParam(value = "cid", required = false) String consumerIds){

		Set<String> interestConsumerIds = getConsumerIds(consumerIds);
		
		StatsData producerData = producerDataRetriever.getQpx(topic, QPX.SECOND);
		List<ConsumerDataPair> consumerData = consumerDataRetriever.getQpxForAllConsumerId(topic, QPX.SECOND);
		
		return buildConsumerHighChartsWrapper(topic, Y_AXIS_TYPE_QPS, producerData, consumerData, interestConsumerIds);
	} 

	private Set<String> getConsumerIds(String consumerIds) {
		
		if(consumerIds == null){
			return null;
		}
		
		Set<String> result = new HashSet<String>();
		String []split = consumerIds.split("\\s*,\\s*");
		for(String consumerId : split){
			
			if(StringUtils.isEmpty(consumerId)){
				continue;
			}
			result.add(consumerId.trim());
		}
		
		if(logger.isInfoEnabled()){
			logger.info("[getConsumerIds]" + result);
		}
		return result;
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/accu/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicAccumulation(@PathVariable String topic, @RequestParam(value = "cid", required = false) String consumerIds){

		Set<String> interestConsumerIds = getConsumerIds(consumerIds);

		Map<String, StatsData> statsData = accumulationRetriever.getAccumulationForAllConsumerId(topic);

		if(interestConsumerIds != null){
			
			List<String> remove = new LinkedList<String>();
			for(String consumerId : statsData.keySet()){
				if(!interestConsumerIds.contains(consumerId)){
					remove.add(consumerId);
				}
			}
			
			for(String consumerId : remove){
				statsData.remove(consumerId);
			}
		}

		
		return buildStatsHighChartsWrapper(Y_AXIS_TYPE_ACCUMULATION, statsData);
	} 

	private Set<String> allTopics() {
		
		Set<String> producerTopics = producerDataRetriever.getTopics();
		Set<String> consumerTopics = consumerDataRetriever.getTopics();
		producerTopics.addAll(consumerTopics);
		return producerTopics;
	}


	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerDelayMonitor(@PathVariable final String topic, @RequestParam(value = "cid", required = false) String consumerIds) throws Exception{
		
		final Set<String> interestConsumerIds = getConsumerIds(consumerIds);

		SwallowCallableWrapper<List<HighChartsWrapper>> wrapper = new CatCallableWrapper<List<HighChartsWrapper>>(CAT_TYPE, "getConsumerDelayMonitor");
		
		return wrapper.doCallable(new Callable<List<HighChartsWrapper>>() {
			
			@Override
			public List<HighChartsWrapper> call() throws Exception {
				
				StatsData 		 producerData = producerDataRetriever.getSaveDelay(topic); 
				List<ConsumerDataPair>  consumerDelay = consumerDataRetriever.getDelayForAllConsumerId(topic);
				
				return  buildConsumerHighChartsWrapper(topic, Y_AXIS_TYPE_DELAY, producerData, consumerDelay, interestConsumerIds); 
			}
		});		
		
		
	}

	private List<HighChartsWrapper> buildStatsHighChartsWrapper(String yAxis, Map<String, StatsData> stats) {

		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(stats.size());
		for(Entry<String, StatsData> entry : stats.entrySet()){
			
			String key = entry.getKey();
			StatsData statsData = entry.getValue();
			
			result.add(ChartBuilder.getHighChart(key, "", yAxis, statsData));

		}

		return result;
	}
	
	private List<HighChartsWrapper> buildConsumerHighChartsWrapper(String yAxis, Map<String, ConsumerDataPair> serverQpx) {

		int size = serverQpx.size();
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(size);
		
		for(Entry<String, ConsumerDataPair> entry : serverQpx.entrySet()){
			
			String ip = entry.getKey();
			ConsumerDataPair dataPair = entry.getValue();
			List<StatsData> allStats = new LinkedList<StatsData>();
			if(isEmpty(dataPair.getSendData()) && isEmpty(dataPair.getAckData())){
				continue;
			}
			allStats.add(dataPair.getSendData());
			allStats.add(dataPair.getAckData());
			result.add(ChartBuilder.getHighChart(ip, "", yAxis, allStats));
		}
			
		return result;
	}

	private boolean isEmpty(StatsData sendData) {
		
		return sendData == null || sendData.getArrayData() == null || sendData.getArrayData().length == 0;
	}

	private List<HighChartsWrapper> buildConsumerHighChartsWrapper(String topic, String yAxis, StatsData producerData, List<ConsumerDataPair>consumerData, Set<String> interestConsumerIds) {
		
		int size = consumerData.size();
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(size);
		
		for(int i=0; i<consumerData.size();i++){
			
			ConsumerDataPair dataPair = consumerData.get(i);
			String currentConsumerId = dataPair.getConsumerId();
			if(interestConsumerIds != null && !interestConsumerIds.contains(currentConsumerId)){
				continue;
			}
			
			List<StatsData> allStats = new LinkedList<StatsData>();
			allStats.add(producerData);
			allStats.add(dataPair.getSendData());
			allStats.add(dataPair.getAckData());
			result.add(ChartBuilder.getHighChart(getTopicDesc(topic), getConsumerIdDesc(currentConsumerId), yAxis, allStats));
		}
		
		return result;
	}

	private String getConsumerIdDesc(String consumerId) {
		
		if(consumerId.equals(MonitorData.TOTAL_KEY)){
			return "所有consumerId";
		}
		return consumerId;
	}

	private String getTopicDesc(String topic) {
		
		if(topic.equals(MonitorData.TOTAL_KEY)){
			return "所有topic";
		}
		return topic;
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
