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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
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
import com.dianping.swallow.common.server.monitor.data.QPX;
import com.dianping.swallow.common.server.monitor.data.structure.MonitorData;
import com.dianping.swallow.web.dashboard.model.ResultEntry;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever.ConsumerDataPair;
import com.dianping.swallow.web.monitor.ConsumerDataRetriever.ConsumerOrderDataPair;
import com.dianping.swallow.web.monitor.OrderStatsData;
import com.dianping.swallow.web.monitor.ProducerDataRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.StatsDataOrderable;
import com.dianping.swallow.web.monitor.charts.ChartBuilder;
import com.dianping.swallow.web.monitor.charts.HighChartsWrapper;
import com.dianping.swallow.web.service.MinuteEntryService;
import com.dianping.swallow.web.task.TopicScanner;
import com.dianping.swallow.web.util.DateUtil;

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

	public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

	private static final long maxSearchTimeSpan = 3 * 60 * 60 * 1000;

	@Autowired
	private ProducerDataRetriever producerDataRetriever;

	@Autowired
	private ConsumerDataRetriever consumerDataRetriever;

	@Autowired
	private AccumulationRetriever accumulationRetriever;
	
	@Autowired
	private StatsDataOrderable statsDataOrderable;

	@Autowired
	private TopicScanner topicScanner;

	@Resource(name = "minuteEntryService")
	private MinuteEntryService minuteEntryService;

	@RequestMapping(value = "/console/monitor/consumerserver/qps", method = RequestMethod.GET)
	public ModelAndView viewConsumerServerQps() {

		return new ModelAndView("monitor/consumerserverqps", createViewMap("server", "consumerserverqps"));
	}

	@RequestMapping(value = "/console/monitor/producerserver/qps", method = RequestMethod.GET)
	public ModelAndView viewProducerServerQps() {

		return new ModelAndView("monitor/producerserverqps", createViewMap("server", "producerserverqps"));
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps", method = RequestMethod.GET)
	public ModelAndView viewTopicQps(@PathVariable String topic) {

		return new ModelAndView("monitor/consumerqps", createViewMap("topic", "consumerqps"));
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/accu", method = RequestMethod.GET)
	public ModelAndView viewTopicAccumulation(@PathVariable String topic, HttpServletRequest request) {
		if (topic.equals(MonitorData.TOTAL_KEY)) {
			String firstTopic = getFirstTopic(accumulationRetriever.getTopics());
			if (!firstTopic.equals(MonitorData.TOTAL_KEY)) {
				return new ModelAndView("redirect:/console/monitor/consumer/" + firstTopic + "/accu", createViewMap(
						"topic", "consumeraccu"));
			}
		}
		return new ModelAndView("monitor/consumeraccu", createViewMap("topic", "consumeraccu"));
	}

	@RequestMapping(value = "/console/monitor/dashboard", method = RequestMethod.GET)
	public ModelAndView viewTopicdashboarddelay() {

		return new ModelAndView("monitor/consumerdashboarddelay", createViewMap("dashboard", "dashboarddelay"));
	}

	@RequestMapping(value = "/console/monitor/dashboard/delay/minute", method = RequestMethod.GET)
	@ResponseBody
	public Object getConsumerIdDelayDashboard(@RequestParam("date") String date, @RequestParam("step") int step,
			@RequestParam("type") String type) throws Exception {

		Date stop = adjustTimeByStep(date, step);

		Date start = calStartTime(stop);

		List<ResultEntry> entrys = minuteEntryService.loadMinuteEntryPage(start, stop, type);

		return buildResponse(entrys, type.trim());

	}

	private String getFirstTopic(Set<String> topics) {

		if (topics == null || topics.size() == 0) {

			return MonitorData.TOTAL_KEY;
		}
		return topics.toArray(new String[0])[0];
	}

	@RequestMapping(value = "/console/monitor/producer/{topic}/savedelay", method = RequestMethod.GET)
	public ModelAndView viewProducerDelayMonitor(@PathVariable String topic) throws IOException {

		Map<String, Object> map = createViewMap("topic", "delay");
		return new ModelAndView("monitor/producerdelay", map);

	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay", method = RequestMethod.GET)
	public ModelAndView viewConsumerDelayMonitor(@PathVariable String topic) throws IOException {

		Map<String, Object> map = createViewMap("topic", "delay");
		return new ModelAndView("monitor/consumerdelay", map);
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/order", method = RequestMethod.GET)
	public ModelAndView viewConsumerOrder(@PathVariable String topic) {

		return new ModelAndView("monitor/consumerorder", createViewMap("topic", "consumerorder"));
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

		Map<String, ConsumerDataPair> serverQpx = consumerDataRetriever.getServerQpx(QPX.SECOND);

		return buildConsumerHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	}

	@RequestMapping(value = "/console/monitor/consumerserver/qps/get/{startTime}/{endTime}", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerServerQps(@PathVariable String startTime, @PathVariable String endTime) {
		Map<String, ConsumerDataPair> serverQpx = null;
		if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
			return getConsumerServerQps();
		}
		SearchTime searchTime = new SearchTime().getSearchTime(startTime, endTime);

		serverQpx = consumerDataRetriever.getServerQpx(QPX.SECOND, searchTime.getStartTime(), searchTime.getEndTime());
		return buildConsumerHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	}

	@RequestMapping(value = "/console/monitor/producerserver/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getProducerServerQps() {

		Map<String, StatsData> serverQpx = producerDataRetriever.getServerQpx(QPX.SECOND);

		return buildStatsHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	}

	@RequestMapping(value = "/console/monitor/producerserver/qps/get/{startTime}/{endTime}", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getProducerServerQps(@PathVariable String startTime, @PathVariable String endTime) {
		Map<String, StatsData> serverQpx = null;
		if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
			return getProducerServerQps();
		}
		SearchTime searchTime = new SearchTime().getSearchTime(startTime, endTime);
		serverQpx = producerDataRetriever.getServerQpx(QPX.SECOND, searchTime.getStartTime(), searchTime.getEndTime());

		return buildStatsHighChartsWrapper(Y_AXIS_TYPE_QPS, serverQpx);
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicQps(@PathVariable String topic,
			@RequestParam(value = "cid", required = false) String consumerIds) {

		Set<String> interestConsumerIds = getConsumerIds(consumerIds);
		StatsData producerData = producerDataRetriever.getQpx(topic, QPX.SECOND);
		List<ConsumerDataPair> consumerData = consumerDataRetriever.getQpxForAllConsumerId(topic, QPX.SECOND);

		return buildConsumerHighChartsWrapper(topic, Y_AXIS_TYPE_QPS, producerData, consumerData, interestConsumerIds);
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/qps/get/{startTime}/{endTime}", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicQps(@PathVariable String topic,
			@RequestParam(value = "cid", required = false) String consumerIds, @PathVariable String startTime,
			@PathVariable String endTime) {

		if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
			return getTopicQps(topic, consumerIds);
		}

		Set<String> interestConsumerIds = getConsumerIds(consumerIds);

		SearchTime searchTime = new SearchTime().getSearchTime(startTime, endTime);
		StatsData producerData = producerDataRetriever.getQpx(topic, QPX.SECOND, searchTime.getStartTime(),
				searchTime.getEndTime());
		List<ConsumerDataPair> consumerData = consumerDataRetriever.getQpxForAllConsumerId(topic, QPX.SECOND,
				searchTime.getStartTime(), searchTime.getEndTime());

		return buildConsumerHighChartsWrapper(topic, Y_AXIS_TYPE_QPS, producerData, consumerData, interestConsumerIds);
	}

	private Set<String> getConsumerIds(String consumerIds) {

		if (consumerIds == null) {
			return null;
		}

		Set<String> result = new HashSet<String>();
		String[] split = consumerIds.split("\\s*,\\s*");
		for (String consumerId : split) {

			if (StringUtils.isEmpty(consumerId)) {
				continue;
			}
			result.add(consumerId.trim());
		}

		if (logger.isInfoEnabled()) {
			logger.info("[getConsumerIds]" + result);
		}
		return result;
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/accu/get", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicAccumulation(@PathVariable String topic,
			@RequestParam(value = "cid", required = false) String consumerIds) {

		Set<String> interestConsumerIds = getConsumerIds(consumerIds);

		Map<String, StatsData> statsData = accumulationRetriever.getAccumulationForAllConsumerId(topic);

		if (interestConsumerIds != null) {

			List<String> remove = new LinkedList<String>();
			for (String consumerId : statsData.keySet()) {
				if (!interestConsumerIds.contains(consumerId)) {
					remove.add(consumerId);
				}
			}

			for (String consumerId : remove) {
				statsData.remove(consumerId);
			}
		}

		return buildStatsHighChartsWrapper(Y_AXIS_TYPE_ACCUMULATION, statsData);
	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/accu/get/{startTime}/{endTime}", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getTopicAccumulation(@PathVariable String topic,
			@RequestParam(value = "cid", required = false) String consumerIds, @PathVariable String startTime,
			@PathVariable String endTime) {

		if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
			return getTopicAccumulation(topic, consumerIds);
		}

		Set<String> interestConsumerIds = getConsumerIds(consumerIds);

		final SearchTime searchTime = new SearchTime().getSearchTime(startTime, endTime);

		Map<String, StatsData> statsData = accumulationRetriever.getAccumulationForAllConsumerId(topic,
				searchTime.getStartTime(), searchTime.getEndTime());

		if (interestConsumerIds != null) {

			List<String> remove = new LinkedList<String>();
			for (String consumerId : statsData.keySet()) {
				if (!interestConsumerIds.contains(consumerId)) {
					remove.add(consumerId);
				}
			}

			for (String consumerId : remove) {
				statsData.remove(consumerId);
			}
		}

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
	public List<HighChartsWrapper> getConsumerDelayMonitor(@PathVariable final String topic,
			@RequestParam(value = "cid", required = false) String consumerIds) throws Exception {

		final Set<String> interestConsumerIds = getConsumerIds(consumerIds);

		SwallowCallableWrapper<List<HighChartsWrapper>> wrapper = new CatCallableWrapper<List<HighChartsWrapper>>(
				CAT_TYPE, "getConsumerDelayMonitor");

		return wrapper.doCallable(new Callable<List<HighChartsWrapper>>() {

			@Override
			public List<HighChartsWrapper> call() throws Exception {

				StatsData producerData = producerDataRetriever.getSaveDelay(topic);
				List<ConsumerDataPair> consumerDelay = consumerDataRetriever.getDelayForAllConsumerId(topic);

				return buildConsumerHighChartsWrapper(topic, Y_AXIS_TYPE_DELAY, producerData, consumerDelay,
						interestConsumerIds);
			}
		});

	}

	@RequestMapping(value = "/console/monitor/consumer/{topic}/delay/get/{startTime}/{endTime}", method = RequestMethod.POST)
	@ResponseBody
	public List<HighChartsWrapper> getConsumerDelayMonitor(@PathVariable final String topic,
			@RequestParam(value = "cid", required = false) String consumerIds, @PathVariable String startTime,
			@PathVariable String endTime) throws Exception {

		if (StringUtils.isBlank(startTime) && StringUtils.isBlank(endTime)) {
			return getConsumerDelayMonitor(topic, consumerIds);
		}

		final Set<String> interestConsumerIds = getConsumerIds(consumerIds);

		final SearchTime searchTime = new SearchTime().getSearchTime(startTime, endTime);

		SwallowCallableWrapper<List<HighChartsWrapper>> wrapper = new CatCallableWrapper<List<HighChartsWrapper>>(
				CAT_TYPE, "getConsumerDelayMonitor");

		return wrapper.doCallable(new Callable<List<HighChartsWrapper>>() {

			@Override
			public List<HighChartsWrapper> call() throws Exception {

				StatsData producerData = producerDataRetriever.getSaveDelay(topic, searchTime.getStartTime(),
						searchTime.getEndTime());
				List<ConsumerDataPair> consumerDelay = consumerDataRetriever.getDelayForAllConsumerId(topic,
						searchTime.getStartTime(), searchTime.getEndTime());

				return buildConsumerHighChartsWrapper(topic, Y_AXIS_TYPE_DELAY, producerData, consumerDelay,
						interestConsumerIds);
			}
		});

	}

	@RequestMapping(value = "/console/monitor/consumer/total/order/get/{size}", method = RequestMethod.GET)
	@ResponseBody
	public Object getConsumerOrderMonitor(@PathVariable final int size) throws Exception {
		return statsDataOrderable.getOrderStatsData(size);
	}
	
	@RequestMapping(value = "/console/monitor/consumer/total/delay/order/get/{size}", method = RequestMethod.GET)
	@ResponseBody
	public Object getConsumerDelayOrderMonitor(@PathVariable final int size) throws Exception {
		OrderStatsData saveDelayStatsData = producerDataRetriever.getDelayOrder(size);
		ConsumerOrderDataPair consumerDelayOrderData = consumerDataRetriever.getDelayOrderForAllConsumerId(size);
		OrderStatsDataResult result = new OrderStatsDataResult();
		result.add(saveDelayStatsData);
		result.add(consumerDelayOrderData.getSendStatsData());
		result.add(consumerDelayOrderData.getAckStatsData());
		return result;
	}

	@RequestMapping(value = "/console/monitor/consumer/total/accu/order/get/{size}", method = RequestMethod.GET)
	@ResponseBody
	public Object getConsumerAccuOrderMonitor(@PathVariable final int size) throws Exception {
		OrderStatsData accuStatsData = accumulationRetriever.getAccuOrderForAllConsumerId(size);
		OrderStatsDataResult result = new OrderStatsDataResult();
		result.add(accuStatsData);
		return result;
	}

	@RequestMapping(value = "/console/monitor/consumer/total/qpx/order/get/{size}", method = RequestMethod.GET)
	@ResponseBody
	public Object getConsumerQpxOrderMonitor(@PathVariable final int size) throws Exception {
		OrderStatsData saveQpxStatsData = producerDataRetriever.getQpxOrder(size);
		ConsumerOrderDataPair consumerQpxOrderData = consumerDataRetriever.getQpxOrderForAllConsumerId(size);
		OrderStatsDataResult result = new OrderStatsDataResult();
		result.add(saveQpxStatsData);
		result.add(consumerQpxOrderData.getSendStatsData());
		result.add(consumerQpxOrderData.getAckStatsData());
		return result;
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
			List<ConsumerDataPair> consumerData, Set<String> interestConsumerIds) {

		int size = consumerData.size();
		List<HighChartsWrapper> result = new ArrayList<HighChartsWrapper>(size);

		for (int i = 0; i < consumerData.size(); i++) {

			ConsumerDataPair dataPair = consumerData.get(i);
			String currentConsumerId = dataPair.getConsumerId();
			if (interestConsumerIds != null && !interestConsumerIds.contains(currentConsumerId)) {
				continue;
			}

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

	private Object buildResponse(List<ResultEntry> entrys, String type) {

		Map<String, Object> result = new HashMap<String, Object>();

		if (!entrys.isEmpty()) {
			Date first = entrys.get(0).getTime();
			result = addTime(first);

			result.put("entry", entrys);

			return result;
		}

		return addTime(new Date());
	}

	private Map<String, Object> addTime(Date first) {

		Map<String, Object> result = new HashMap<String, Object>();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(first);
		calendar.clear(Calendar.MINUTE);
		calendar.clear(Calendar.SECOND);
		Date starttime = calendar.getTime();

		calendar.add(Calendar.MINUTE, 59);
		calendar.add(Calendar.SECOND, 59);
		Date stoptime = calendar.getTime();

		result.put("starttime", starttime);
		result.put("stoptime", stoptime);

		return result;
	}

	private Date adjustTimeByStep(String date, int step) throws Exception {

		SimpleDateFormat formatter = new SimpleDateFormat(FORMAT);
		String transferDate = date.replaceAll("Z", "+0800").replaceAll("\"", "");
		Date newdate = formatter.parse(transferDate);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(newdate);
		calendar.add(Calendar.HOUR_OF_DAY, step);

		return calendar.getTime();
	}

	private Date calStartTime(Date stop) {

		Calendar calendarstart = Calendar.getInstance();
		calendarstart.setTime(stop);
		calendarstart.add(Calendar.MINUTE, -11);
		calendarstart.clear(Calendar.SECOND);
		calendarstart.clear(Calendar.MILLISECOND);
		Date start = calendarstart.getTime();

		return start;
	}

	@Override
	public String getSide() {
		return "delay";
	}

	private String subSide = "delay";

	@Override
	public String getSubSide() {
		return subSide;
	}

	public static class OrderStatsDataResult {

		List<OrderStatsData> orderStatsDatas = null;

		public OrderStatsDataResult() {
			orderStatsDatas = new ArrayList<OrderStatsData>();
		}

		public void add(OrderStatsData statsData) {
			orderStatsDatas.add(statsData);
		}

	}

	private static class SearchTime {

		private long startTime;

		private long endTime;

		public long getStartTime() {
			return startTime;
		}

		public void setStartTime(long startTime) {
			this.startTime = startTime;
		}

		public long getEndTime() {
			return endTime;
		}

		public void setEndTime(long endTime) {
			this.endTime = endTime;
		}

		public SearchTime getSearchTime(String startTime, String endTime) {

			if (StringUtils.isNotBlank(startTime) && StringUtils.isNotBlank(endTime)) {

				this.setStartTime(DateUtil.convertStrToDate(startTime).getTime());
				this.setEndTime(DateUtil.convertStrToDate(endTime).getTime());

				if (this.getEndTime() - this.getStartTime() > maxSearchTimeSpan) {
					this.setEndTime(this.getStartTime() + maxSearchTimeSpan);
				}

			} else {

				if (StringUtils.isBlank(startTime) && StringUtils.isNotBlank(endTime)) {

					this.setStartTime(DateUtil.convertStrToDate(startTime).getTime());
					this.setEndTime(this.getStartTime() + maxSearchTimeSpan);

				} else if (StringUtils.isNotBlank(startTime) && StringUtils.isBlank(endTime)) {

					this.setEndTime(DateUtil.convertStrToDate(endTime).getTime());
					this.setStartTime(this.getEndTime() - maxSearchTimeSpan);

				}
			}
			return this;
		}

	}

}
