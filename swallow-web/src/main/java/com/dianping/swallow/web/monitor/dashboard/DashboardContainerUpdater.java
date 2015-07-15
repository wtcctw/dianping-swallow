package com.dianping.swallow.web.monitor.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerIdStatisData;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.StatsData;

/**
 * @author mingdongli
 *
 *         2015年7月7日上午9:36:49
 */
@Component
public class DashboardContainerUpdater implements MonitorDataListener {

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

	private Map<String, TotalData> totalDataMap = new ConcurrentHashMap<String, TotalData>();

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private AtomicBoolean delayeven = new AtomicBoolean(false);

	private AtomicInteger currentMin = new AtomicInteger(Integer.MIN_VALUE);

	private int i = 0;

	@PostConstruct
	void updateDashboardContainer() {

		consumerDataRetrieverWrapper.registerListener(this);
	}

	@Override
	public void achieveMonitorData() {

		if (delayeven.get()) {
			try {
				updateDelayInsDashboard();
			} catch (Exception e) {
				logger.error("Error when get data for all consumerid!", e);
			} finally {
				delayeven.compareAndSet(true, false);
			}
			System.out.println("i is " + (++i));
		} else {
			delayeven.compareAndSet(false, true);
			System.out.println("i is " + (++i));
		}
	}

	private void updateDelayInsDashboard() throws Exception {

		String time = getCurrentTime();
		Set<String> topics = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL);
		for (String topic : topics) {
			Set<String> consumerids = consumerDataRetrieverWrapper.getKeyWithoutTotal(
					ConsumerDataRetrieverWrapper.TOTAL, topic);
			Map<String, StatsData> accuStatsData = accumulationRetriever.getAccumulationForAllConsumerId(topic);
			for (String consumerid : consumerids) {
				ConsumerIdStatisData result = (ConsumerIdStatisData) consumerDataRetrieverWrapper.getValue(
						ConsumerDataRetrieverWrapper.TOTAL, topic, consumerid);
				Set<String> ips = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL,
						topic, consumerid);
				String ip = loadFirstElement(ips);
				String mobile = iPDescManagerWrap.loadDpMobile(ip);
				System.out.println("ip is " + ip);
				System.out.println("consumerid is " + consumerid);
				String email = iPDescManagerWrap.loadEmail(ip);
				NavigableMap<Long, Long> senddelay = result.getDelay(StatisType.SEND);
				List<Long> sendList = new ArrayList<Long>(senddelay.values());
				int sendListSize = sendList.size();
				if (sendListSize < 2) {
					return;
				}
				NavigableMap<Long, Long> ackdelay = result.getDelay(StatisType.ACK);
				List<Long> ackList = new ArrayList<Long>(ackdelay.values());
				List<Long> accuList = new ArrayList<Long>(Collections.nCopies(sendListSize, 0L));
				while (accuList.size() < 2) {
					accuList.add(0L);
				}
				if (accuStatsData != null) {
					StatsData accuSD = accuStatsData.get(consumerid);
					if (!isEmpty(accuSD)) {
						accuList = accuSD.getData();
					}
				}

				int stopIndex = sendListSize;
				int startIndex = stopIndex - 2;
				TotalData td = totalDataMap.get(consumerid);
				if (td == null) {
					td = new TotalData();
				}
				td.setCid(consumerid).setTopic(topic).setDpMobile(mobile).setEmail(email).setTime(time)
						.setListSend(sendList.subList(startIndex, stopIndex))
						.setListAck(ackList.subList(startIndex, stopIndex))
						.setListAccu(accuList.subList(startIndex, stopIndex));
				totalDataMap.put(consumerid, td);
			}
		}

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
			int sendAlarm = senddelay >= consumerBaseAlarmSetting.getSendDelay() ? 1 : 0;
			int ackAlarm = ackdelay >= consumerBaseAlarmSetting.getAckDelay() ? 1 : 0;
			int accuAlarm = accu >= consumerBaseAlarmSetting.getAccumulation() ? 1 : 0;
			int numAlarm = sendAlarm + ackAlarm + accuAlarm;
			e.setConsumerId(totalData.getCid()).setTopic(totalData.getTopic()).setSenddelay(senddelay)
					.setAckdelay(ackdelay).setAccu(accu).setSenddelayAlarm(sendAlarm).setAckdelayAlarm(ackAlarm)
					.setAccuAlarm(accuAlarm).setNumAlarm(numAlarm).setEmail(totalData.getEmail())
					.setDpMobile(totalData.getDpMobile());
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

		Map<String, MinuteEntry> minuteEntryMap = new LinkedHashMap<String, MinuteEntry>();

		for (Map.Entry<String, TotalData> entry : totalDataMap.entrySet()) {
			TotalData td = entry.getValue();
			List<Entry> entrys = td.getEntrys();
			for (int i = 0; i < entrys.size(); ++i) {
				String time = td.getTime();
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
		totalDataMap.clear();
	}

	private String getCurrentTime() {

		Calendar cal = Calendar.getInstance();
		int min = cal.get(Calendar.MINUTE);
		int curMin = currentMin.get();
		int diff = min - curMin;
		if (curMin < 0) {
			curMin = min;
		} else if (diff != 1 && diff != -59) {
			min = curMin + 1;
		}
		currentMin.set(min);
		String time = cal.get(Calendar.HOUR_OF_DAY) + ":" + (min < 10 ? "0" + min : min);
		return time;
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

}
