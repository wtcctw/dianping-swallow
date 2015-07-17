package com.dianping.swallow.web.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.dianping.swallow.common.internal.action.SwallowCallableWrapper;
import com.dianping.swallow.common.internal.action.impl.CatCallableWrapper;
import com.dianping.swallow.common.internal.util.StringUtils;
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.dao.impl.DefaultMessageDao;
import com.dianping.swallow.web.model.dashboard.MinuteEntry;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever.ConsumerDataPair;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.charts.ChartBuilder;
import com.dianping.swallow.web.monitor.charts.HighChartsWrapper;
import com.dianping.swallow.web.monitor.dashboard.DashboardContainer;
import com.dianping.swallow.web.task.TopicScanner;

/**
 * @author mengwenchao
 *
 *         2015年4月14日 下午9:24:38
 */
@Controller
public class DataMonitorController extends AbstractMonitorController {

	public static final String Y_AXIS_TYPE_QPS = "QPS";

	public static final String Y_AXIS_TYPE_DELAY = "延时(毫秒)";

	public static final String Y_AXIS_TYPE_ACCUMULATION = "堆积消息数";

	public static final String CAT_TYPE = "MONITOR";
	
	public static final String ENTRYS = "entry";

	public static final int  ENTRYSIZE = 10;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Autowired
	private TopicScanner topicScanner;

	@Autowired
	private DashboardContainer dashboardContainer;

	@RequestMapping(value = "/console/monitor/consumerserver/qps", method = RequestMethod.GET)
	public ModelAndView viewConsumerServerQps() {

		subSide = "consumerserverqps";
		return new ModelAndView("monitor/consumerserverqps", createViewMap());
	}

	@RequestMapping(value = "/console/monitor/producerserver/qps", method = RequestMethod.GET)
	public ModelAndView viewProducerServerQps() {

		subSide = "producerserverqps";
		return new ModelAndView("monitor/producerserverqps", createViewMap());
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps", method = RequestMethod.GET)
	public ModelAndView viewTopicQps(@PathVariable String topic) {

		subSide = "consumerqps";
		return new ModelAndView("monitor/consumerqps", createViewMap());
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/accu", method = RequestMethod.GET)
	public ModelAndView viewTopicAccumulation(@PathVariable String topic) {

		subSide = "consumeraccu";

		if (topic.equals(MonitorData.TOTAL_KEY)) {
			String firstTopic = getFirstTopic(accumulationRetriever.getTopics());
			if (!firstTopic.equals(MonitorData.TOTAL_KEY)) {
				return new ModelAndView("redirect:/console/monitor/consumer/" + firstTopic + "/accu", createViewMap());
			}
		}
		return new ModelAndView("monitor/consumeraccu", createViewMap());
	}

	@RequestMapping(value = "/console/monitor/dashboard", method = RequestMethod.GET)
	public ModelAndView viewTopicdashboarddelay() {

		subSide = "dashboarddelay";
		
		return new ModelAndView("monitor/consumerdashboarddelay", createViewMap());
	}

	@RequestMapping(value = "/console/monitor/dashboard/delay/{offset}", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> getConsumerIdDelayDashboard(@PathVariable int offset, int currentmin) throws Exception {
		
		int realOffset;
		if(currentmin == -1){
			realOffset = 0;
		}else{
			Calendar calendar = Calendar.getInstance();
			int min = calendar.get(Calendar.MINUTE);
			if(min - currentmin < 0){
				realOffset = min + 60 - currentmin + offset - 1;
			}else{
				realOffset = min - currentmin + offset - 1;
			}
		}
		
		List<MinuteEntry> entrys = dashboardContainer.fetchMinuteEntries("dashboard", realOffset, ENTRYSIZE);
		return addTimeToReport(entrys, offset);
		
	}

	private String getFirstTopic(Set<String> topics) {

		if (topics == null || topics.size() == 0) {

			return MonitorData.TOTAL_KEY;
		}
		return topics.toArray(new String[0])[0];
	}

	@RequestMapping(value = "/console/monitor/producer/{topic}/savedelay", method = RequestMethod.GET)
	public ModelAndView viewProducerDelayMonitor(@PathVariable String topic) throws IOException {

		Map<String, Object> map = createViewMap();
		return new ModelAndView("monitor/producerdelay", map);

	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay", method = RequestMethod.GET)
	public ModelAndView viewConsumerDelayMonitor(@PathVariable String topic) throws IOException {

		subSide = "delay";
		Map<String, Object> map = createViewMap();
		return new ModelAndView("monitor/consumerdelay", map);
	}

	@RequestMapping(value = "/console/monitor/consumer/debug/{server}", method = RequestMethod.GET)
	@ResponseBody
	public String getConsumerDebug(@PathVariable String server) {

		if (logger.isInfoEnabled()) {
			logger.info("[getConsumerDebug]" + server);
			logger.info(consumerDataRetriever.getDebugInfo(server));
		}
		return "ok";
	}

	@RequestMapping(value = "/console/monitor/scanner/debug", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Set<String>> getScannerDebug() {

		return topicScanner.getTopics();
	}

	@RequestMapping(value = "/console/monitor/producer/debug/{server}", method = RequestMethod.GET)
	@ResponseBody
	public String getProducerDebug(@PathVariable String server) {

		if (logger.isInfoEnabled()) {
			logger.info("[getProducerDebug]" + server);
			logger.info(producerDataRetriever.getDebugInfo(server));
		}
		return "ok";
	}

	@RequestMapping(value = "/console/monitor/consumerserver/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerServerQps() {
		subSide = "consumerserverqps";
		Map<String, ConsumerDataPair> serverQpx = consumerDataRetriever.getServerQpx(QPX.SECOND);

		return buildConsumerHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	}

	@RequestMapping(value = "/console/monitor/producerserver/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getProducerServerQps() {

		subSide = "producerserverqps";
		Map<String, StatsData> serverQpx = producerDataRetriever.getServerQpx(QPX.SECOND);

		return buildStatsHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicQps(@PathVariable String topic) {

		StatsData producerData = producerDataRetriever.getQpx(topic, QPX.SECOND);
		List<ConsumerDataPair> consumerData = consumerDataRetriever.getQpxForAllConsumerId(topic, QPX.SECOND);

		return buildConsumerHighChartsWrapper(topic, Y_AXIS_TYPE_QPS, producerData, consumerData);
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
	public List<HighChartsWrapper> getTopicAccumulation(@PathVariable String topic) {

		Map<String, StatsData> statsData = accumulationRetriever.getAccumulationForAllConsumerId(topic);

		return buildStatsHighChartsWrapper(Y_AXIS_TYPE_ACCUMULATION, statsData);
	}

	@RequestMapping(value = "/console/monitor/topiclist/get", method = RequestMethod.POST)
	@ResponseBody
	public Set<String> getProducerDelayMonitor() throws IOException {
		return allTopics();
	}

	private Set<String> allTopics() {

		Set<String> producerTopics = producerDataRetriever.getTopics();
		Set<String> consumerTopics = consumerDataRetriever.getTopics();
		producerTopics.addAll(consumerTopics);
		return producerTopics;
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerDelayMonitor(@PathVariable final String topic) throws Exception {

		SwallowCallableWrapper<List<HighChartsWrapper>> wrapper = new CatCallableWrapper<List<HighChartsWrapper>>(
				CAT_TYPE, "getConsumerDelayMonitor");

		return wrapper.doCallable(new Callable<List<HighChartsWrapper>>() {

			@Override
			public List<HighChartsWrapper> call() throws Exception {

				StatsData producerData = producerDataRetriever.getSaveDelay(topic);
				List<ConsumerDataPair> consumerDelay = consumerDataRetriever.getDelayForAllConsumerId(topic);

				return buildConsumerHighChartsWrapper(topic, Y_AXIS_TYPE_DELAY, producerData, consumerDelay);
			}
		});

	}

	private List<HighChartsWrapper> buildStatsHighChartsWrapper(String yAxis, Map<String, StatsData> stats) {

		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(stats.size());
		for (Entry<String, StatsData> entry : stats.entrySet()) {

			String key = entry.getKey();
			StatsData statsData = entry.getValue();

			result.add(ChartBuilder.getHighChart(key, "", yAxis, statsData));

		}

		return result;
	}

	private List<HighChartsWrapper> buildConsumerHighChartsWrapper(String yAxis, Map<String, ConsumerDataPair> serverQpx) {

		int size = serverQpx.size();
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(size);

		for (Entry<String, ConsumerDataPair> entry : serverQpx.entrySet()) {

			String ip = entry.getKey();
			ConsumerDataPair dataPair = entry.getValue();
			List<StatsData> allStats = new LinkedList<StatsData>();
			if (isEmpty(dataPair.getSendData()) && isEmpty(dataPair.getAckData())) {
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

	private List<HighChartsWrapper> buildConsumerHighChartsWrapper(String topic, String yAxis, StatsData producerData,
			List<ConsumerDataPair> consumerData) {

		int size = consumerData.size();
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(size);

		for (int i = 0; i < consumerData.size(); i++) {

			ConsumerDataPair dataPair = consumerData.get(i);
			String currentConsumerId = dataPair.getConsumerId();

			List<StatsData> allStats = new LinkedList<StatsData>();
			allStats.add(producerData);
			allStats.add(dataPair.getSendData());
			allStats.add(dataPair.getAckData());
			result.add(ChartBuilder.getHighChart(getTopicDesc(topic), getConsumerIdDesc(currentConsumerId), yAxis,
					allStats));
		}

		return result;
	}

	private String getConsumerIdDesc(String consumerId) {

		if (consumerId.equals(MonitorData.TOTAL_KEY)) {
			return "所有consumerId";
		}
		return consumerId;
	}

	private String getTopicDesc(String topic) {

		if (topic.equals(MonitorData.TOTAL_KEY)) {
			return "所有topic";
		}
		return topic;
	}
	
	private Map<String, Object> addTimeToReport(List<MinuteEntry> entry, int offset){
		 
		Map<String, Object> map = new HashMap<String, Object>();
		int stepHour = offset / 60;
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -stepHour);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		String starttime = convertDateToString(calendar.getTime());
		calendar.add(Calendar.MINUTE, 59);
		calendar.add(Calendar.SECOND, 59);
		String stoptime = convertDateToString(calendar.getTime());
		map.put("starttime", starttime);
		map.put("stoptime", stoptime);
		map.put(ENTRYS, entry);
		
		return map;
	}
	
	private String convertDateToString(Date date){
		
		SimpleDateFormat sdf=new SimpleDateFormat(DefaultMessageDao.TIMEFORMAT);
		return sdf.format(date);
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
