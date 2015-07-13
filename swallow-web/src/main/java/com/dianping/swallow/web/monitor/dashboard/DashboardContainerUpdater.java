package com.dianping.swallow.web.monitor.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.common.server.monitor.collector.AbstractCollector;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerIdStatisData;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.StatsData;
import com.google.common.collect.Lists;

/**
 * @author mingdongli
 *
 *         2015年7月7日上午9:36:49
 */
@Component
public class DashboardContainerUpdater implements Runnable {

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Autowired
	private DashboardContainer dashboardContainer;

	@Autowired
	IPDescManagerWrapper iPDescManagerWrap;

	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	@Autowired
	TopicAlarmSettingServiceWrapper topicAlarmSettingServiceWrapper;

	private ScheduledExecutorService executorService = Executors.newScheduledThreadPool(CommonUtils.getCpuCount());

	private Map<String, TotalData> totalDataMap = new ConcurrentHashMap<String, TotalData>();

	public static final int SAMPLENUMBER = DashboardContainer.ENTRYSIZE * 2;

	private static final String TOTAL = "total";

	private AtomicInteger currentMin = new AtomicInteger(-1);

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@PostConstruct
	void updateDashboardContainer() {

		executorService.scheduleAtFixedRate(this, 1, 60, TimeUnit.SECONDS);
	}

	@Override
	public void run() {

		try {
			getConsumerIdDashboard();
			logger.info("Update minute entry");
		} catch (Exception e) {
			logger.error("Error when get data for all consumerid!", e);
		}
	};

	private void getConsumerIdDashboard() throws Exception {

//		Set<String> topics = consumerDataRetrieverWrapper.getKeyWithoutTotal(TOTAL);
//		for (String topic : topics) {
//			Set<String> consumerids = consumerDataRetrieverWrapper.getKeyWithoutTotal(TOTAL, topic);
//			Map<String, StatsData> accuStatsData = accumulationRetriever.getAccumulationForAllConsumerId(topic);
//			for (String consumerid : consumerids) {
//				ConsumerIdStatisData result = (ConsumerIdStatisData) consumerDataRetrieverWrapper.getValue(TOTAL,
//						topic, consumerid);
//				Set<String> ips = consumerDataRetrieverWrapper.getKeyWithoutTotal(TOTAL, topic, consumerid);
//				String ip = loadFirstElement(ips);
//				String mobile = iPDescManagerWrap.loadDpMobile(ip);
//				String email = iPDescManagerWrap.loadEmail(ip);
//				NavigableMap<Long, Long> senddelay = result.getDelay(StatisType.SEND);
//				List<Long> sendList = new ArrayList<Long>(senddelay.values());
//				if (currentMin.get() < 0) {
//					int minute = loadLastKeyOfMinute(senddelay);
//					currentMin.set(minute);
//				}
//				NavigableMap<Long, Long> ackdelay = result.getDelay(StatisType.ACK);
//				List<Long> ackList = new ArrayList<Long>(ackdelay.values());
//				StatsData accuSD = accuStatsData.get(consumerid);
//				List<Long> accuList = new ArrayList<Long>(Collections.nCopies(sendList.size(), 0L));
//				if (!isEmpty(accuSD)) {
//					accuList = accuSD.getData();
//				}
//
//				int entrySize = dashboardContainer.getEntrySize();
//				int offset = entrySize == 0 ? DashboardContainer.ENTRYSIZE * 2
//								: (entrySize == DashboardContainer.ENTRYSIZE ? 2
//								: (DashboardContainer.ENTRYSIZE - entrySize) * 2);
//				int stopIndex = sendList.size();
//				if(stopIndex < offset){
//					stopIndex = DashboardContainer.ENTRYSIZE * 2;
//				}
//				int startIndex = stopIndex - offset;
//				TotalData td = totalDataMap.get(consumerid);
//				if (td == null) {
//					td = new TotalData();
//				}
//				td.setCid(consumerid).setTopic(topic).setDpMobile(mobile).setEmail(email).setMinute(currentMin.get())
//						.setListSend(sendList.subList(startIndex, stopIndex))
//						.setListAck(ackList.subList(startIndex, stopIndex))
//						.setListAccu(accuList.subList(startIndex, stopIndex));
//				totalDataMap.put(consumerid, td);
//			}
//		}

		// ----------------------------------------------------------------------------
		Map<String, List<Long>> mapSend = new ConcurrentHashMap<String, List<Long>>();
		Map<String, List<Long>> mapAck = new ConcurrentHashMap<String, List<Long>>();
		Map<String, List<Long>> mapAccu = new ConcurrentHashMap<String, List<Long>>();
		Random random = new Random();
		int size = dashboardContainer.getEntrySize();
		int offset = size == DashboardContainer.ENTRYSIZE ? 2 : SAMPLENUMBER;
		String topic = "topic";

		for (int i = 0; i < SAMPLENUMBER; ++i) {
			List<Long> number = new ArrayList<Long>(offset);
			for (int j = 0; j < offset; ++j) {
				long tmp = (long) (random.nextInt(20) % (20 - 10 + 1) + 10);
				number.add(tmp);
			}
			mapAck.put("key" + i, number);
		}
		for (int i = 0; i < SAMPLENUMBER; ++i) {
			List<Long> number = new ArrayList<Long>(offset);
			for (int j = 0; j < offset; ++j) {
				long tmp = (long) (random.nextInt(20) % (20 - 10 + 1) + 10);
				number.add(tmp);
			}
			mapSend.put("key" + i, number);
		}
		for (int i = 0; i < SAMPLENUMBER; ++i) {
			List<Long> number = new ArrayList<Long>(offset);
			for (int j = 0; j < offset; ++j) {
				long tmp = (long) (random.nextInt(20) % (20 - 10 + 1) + 10);
				number.add(tmp);
			}
			mapAccu.put("key" + i, number);
		}
		for (Map.Entry<String, List<Long>> send : mapSend.entrySet()) {
			String cid = send.getKey();
			TotalData td = totalDataMap.get(cid);
			if (td == null) {
				TotalData totalData = new TotalData();
				totalData.setCid(cid).setTopic(topic).setListSend(send.getValue()).setListAck(mapAck.get(cid))
						.setListAccu(mapAccu.get(cid));
				totalDataMap.put(cid, totalData);
			} else {
				td.setListSend(send.getValue()).setListAck(mapAck.get(cid)).setListAccu(mapAccu.get(cid));
				totalDataMap.put(cid, td);
			}
		}
		// -------------------------------------------------------------------------------------------
		for (Map.Entry<String, TotalData> entry : totalDataMap.entrySet()) {
			generateEntrys(entry.getValue());
		}

		doGenerateMinuteEntrys(totalDataMap);
	}

	private void generateEntrys(TotalData totalData) {

		List<Long> mergeData = calMinuteStats(totalData.getListSend());
		totalData.setListSend(mergeData);
		mergeData = calMinuteStats(totalData.getListAck());
		totalData.setListAck(mergeData);
		mergeData = calMinuteStats(totalData.getListAccu());
		totalData.setListAccu(mergeData);
		doGenerateEntrys(totalData);
	}

	/**
	 * 产生每个Entry
	 * 
	 * @param totalData
	 */
	private void doGenerateEntrys(TotalData totalData) {

		List<Long> sendList = totalData.getListSend();
		List<Long> ackList = totalData.getListAck();
		List<Long> accuList = totalData.getListAccu();
		List<Entry> entrys = totalData.getEntrys();
		int size = sendList.size();
		for (int i = 0; i < size; ++i) {
			Entry e = new Entry();
			long senddelay = sendList.get(i);
			long ackdelay = ackList.get(i);
			long accu = accuList.get(i);
			String topic = totalData.getTopic();
			ConsumerBaseAlarmSetting consumerBaseAlarmSetting = topicAlarmSettingServiceWrapper
					.loadConsumerBaseAlarmSetting(topic);
			int sendAlarm = senddelay >= consumerBaseAlarmSetting.getSenderDelay() ? 1 : 0;
			int ackAlarm = ackdelay >= consumerBaseAlarmSetting.getAckDelay() ? 1 : 0;
			int accuAlarm = accu >= consumerBaseAlarmSetting.getAccumulation() ? 1 : 0;
			int numAlarm = sendAlarm + ackAlarm + accuAlarm;
			e.setConsumerId(totalData.getCid()).setTopic(totalData.getTopic()).setSenddelay(senddelay)
					.setAckdelay(ackdelay).setAccu(accu).setSenddelayAlarm(sendAlarm).setAckdelayAlarm(ackAlarm)
					.setAccuAlarm(accuAlarm).setNumAlarm(numAlarm);
			entrys.add(e);
		}
		totalData.setEntrys(entrys);
	}

	private List<Long> calMinuteStats(List<Long> number) {

		List<Long> result = new ArrayList<Long>();
		long delay = 0;
		for (int i = 0; i < number.size();) {
			delay = number.get(i++);
			delay += number.get(i++);
			result.add(delay);
		}
		return result;
	}

	private void doGenerateMinuteEntrys(Map<String, TotalData> map) {

		String key = "dashboard";
		List<MinuteEntry> minuteEntryList = dashboardContainer.getDashboards().get(key);
		if (minuteEntryList == null) {
			minuteEntryList = new ArrayList<MinuteEntry>();
		}
		int entrySize = minuteEntryList.size();
		int size = entrySize == 0 ? DashboardContainer.ENTRYSIZE : (entrySize == DashboardContainer.ENTRYSIZE ? 1
				: DashboardContainer.ENTRYSIZE - entrySize);
		List<String> times = getTimes(size, currentMin.get());

		// 按时间升序
		Map<String, MinuteEntry> minuteEntryMap = new LinkedHashMap<String, MinuteEntry>();

		for (Map.Entry<String, TotalData> entry : totalDataMap.entrySet()) {
			TotalData td = entry.getValue();
			List<Entry> entrys = td.getEntrys();
			for (int i = 0; i < entrys.size(); ++i) {
				String time = times.get(i);
				MinuteEntry me = minuteEntryMap.get(time);
				if (me == null) {
					me = new MinuteEntry();
				}
				if (StringUtils.isBlank(me.getTime())) {
					me.setTime(time);
				}
				me.addEntry(entrys.get(i));
				minuteEntryMap.put(time, me);
			}
		}

		for (Map.Entry<String, MinuteEntry> entry : minuteEntryMap.entrySet()) {
			MinuteEntry me = entry.getValue();
			dashboardContainer.insertMinuteEntry(key, me);
		}
		currentMin.set(-1);
		totalDataMap.clear();
	}

	private List<String> getTimes(final int size, final int currentMin) {

		List<String> times = new ArrayList<String>();
		Calendar cal = Calendar.getInstance();
		int m = cal.get(Calendar.MINUTE);
		if(m < currentMin){
			cal.add(Calendar.HOUR_OF_DAY, -1);
		}
		cal.set(Calendar.MINUTE, currentMin);
		dashboardContainer.setCurrentMinute(currentMin);
		for (int i = 0; i < size; ++i) {
			cal.add(Calendar.MINUTE, i == 0 ? 0 : -1);
			int min = cal.get(Calendar.MINUTE);
			times.add(cal.get(Calendar.HOUR_OF_DAY) + ":" + (min < 10 ? "0" + min : min));
		}
		return Lists.reverse(times);
	}

	private boolean isEmpty(StatsData sendData) {

		return sendData == null || sendData.getArrayData() == null || sendData.getArrayData().length == 0;
	}

	private String loadFirstElement(Set<String> set) {

		Iterator<String> it = set.iterator();
		while (it.hasNext()) {
			return it.next();
		}
		return "";
	}

	private int loadLastKeyOfMinute(NavigableMap<Long, Long> map) {

		Long lastKey = map.lastKey();
		Long milis = lastKey * AbstractCollector.SEND_INTERVAL * 1000;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(milis);
		return cal.get(Calendar.MINUTE);

	}

}
