package com.dianping.swallow.web.monitor.dashboard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerIdStatisData;
import com.dianping.swallow.web.manager.IPDescManager;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.cmdb.IPDesc;
import com.dianping.swallow.web.model.dashboard.Entry;
import com.dianping.swallow.web.model.dashboard.MinHeap;
import com.dianping.swallow.web.model.dashboard.MinuteEntry;
import com.dianping.swallow.web.model.dashboard.TotalData;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.monitor.wapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.monitor.wapper.TopicAlarmSettingServiceWrapper;
import com.google.common.collect.Lists;

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

	@Resource(name = "ipDescManager")
	private IPDescManager ipDescManager;

	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	@Autowired
	TopicAlarmSettingServiceWrapper topicAlarmSettingServiceWrapper;

	private Map<String, TotalData> totalDataMap = new ConcurrentHashMap<String, TotalData>();

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private AtomicBoolean delayeven = new AtomicBoolean(false);

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
				if (logger.isErrorEnabled()) {
					logger.error("Error when get data for all consumerid!", e);
				}
			} finally {
				delayeven.compareAndSet(true, false);
			}
		} else {
			delayeven.compareAndSet(false, true);
		}
	}

	private void updateDelayInsDashboard() {

		boolean timeSet = false;
		Date entryTime = null;
		Set<String> topics = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL);

		for (String topic : topics) {
			Set<String> consumerids = consumerDataRetrieverWrapper.getKeyWithoutTotal(
					ConsumerDataRetrieverWrapper.TOTAL, topic);
			Map<String, StatsData> accuStatsData = accumulationRetriever.getAccumulationForAllConsumerId(topic);

			for (String consumerid : consumerids) {
				ConsumerIdStatisData result = (ConsumerIdStatisData) consumerDataRetrieverWrapper.getValue(
						ConsumerDataRetrieverWrapper.TOTAL, topic, consumerid);

				NavigableMap<Long, Long> senddelay = result.getDelay(StatisType.SEND);
				List<Long> sendList = new ArrayList<Long>(senddelay.values());
				NavigableMap<Long, Long> ackdelay = result.getDelay(StatisType.ACK);
				List<Long> ackList = new ArrayList<Long>(ackdelay.values());
				int sendListSize = sendList.size();
				int ackListSize = ackList.size();
				if (sendListSize < 2 || ackListSize < 2) {
					continue;
				}
				if (!timeSet) {
					entryTime = getMinuteEntryTime(senddelay.lastKey());
					timeSet = true;
					if (logger.isInfoEnabled()) {
						logger.info(String.format("Generage MinuteEntry for time %tc", entryTime));
					}
				}

				List<Long> accuList = new ArrayList<Long>();
				if (accuStatsData != null) {
					StatsData accuSD = accuStatsData.get(consumerid);
					if (!isEmpty(accuSD)) {
						accuList = accuSD.getData();
					}
				}
				while (accuList.size() < 2) {
					accuList.add(0L);
				}
				int accuListSize = accuList.size();

				String mapKey = topic + consumerid;
				TotalData td = totalDataMap.get(mapKey);
				if (td == null) {
					td = new TotalData();
				}

				td.setCid(consumerid).setTopic(topic).setTime(entryTime);
				td.setListSend(sendList.subList(sendListSize - 2, sendListSize));
				td.setListAck(ackList.subList(ackListSize - 2, ackListSize));
				td.setListAccu(accuList.subList(accuListSize - 2, accuListSize));
				totalDataMap.put(mapKey, td);
				if (logger.isInfoEnabled()) {
					logger.info(String.format("Generate totalData for topic %s and consumerid %s", topic, consumerid));
				}
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Generate totalData for all topic and consumerid"));
		}

		for (Map.Entry<String, TotalData> entry : totalDataMap.entrySet()) {
			generateEntrys(entry.getValue());
		}
		if (logger.isInfoEnabled()) {
			logger.info("Generate entrys for all data");
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
	 * @throws Exception
	 */
	private void doGenerateEntrys(TotalData totalData) {

		List<Long> sendList = totalData.getListSend();
		List<Long> ackList = totalData.getListAck();
		List<Long> accuList = totalData.getListAccu();
		List<Entry> entrys = totalData.getEntrys();
		int size = sendList.size();

		for (int i = 0; i < size; ++i) {
			Entry e = new Entry();
			float senddelay = (float) (sendList.get(i) / 1000.0);
			float ackdelay = (float) (ackList.get(i) / 1000.0);
			long accu = accuList.get(i);
			String consumerid = totalData.getCid();
			String topic = totalData.getTopic();

			Set<String> ips = consumerDataRetrieverWrapper.getKeyWithoutTotal(ConsumerDataRetrieverWrapper.TOTAL,
					topic, consumerid);
			
			String ip = loadFirstElement(ips);
			IPDesc iPDesc = ipDescManager.getIPDesc(ip);
			String mobile = "Blank";
			String email = "Blank";
			String name = "Blank";
			if(iPDesc != null){
				mobile = iPDesc.getDpMobile();
				email = iPDesc.getEmail();
				name = iPDesc.getName();
			}

			ConsumerBaseAlarmSetting consumerBaseAlarmSetting = topicAlarmSettingServiceWrapper
					.loadConsumerBaseAlarmSetting(topic);

			long baseSenddelay = consumerBaseAlarmSetting.getSendDelay();
			long baseackdelay = consumerBaseAlarmSetting.getAckDelay();
			float baseAccu = (float) consumerBaseAlarmSetting.getAccumulation();

			int sendAlarm = senddelay >= baseSenddelay ? 1 : 0;
			int ackAlarm = ackdelay >= baseackdelay ? 1 : 0;
			int accuAlarm = accu >= baseAccu ? 1 : 0;
			int numAlarm = sendAlarm + ackAlarm + accuAlarm;

			float normalizedSendDelay = senddelay / baseSenddelay;
			float normalizedAckDelay = ackdelay / baseackdelay;
			float normalizedAccu = accu / baseAccu;

			e.setConsumerId(consumerid).setTopic(topic).setSenddelay(senddelay).setAckdelay(ackdelay).setAccu(accu)
					.setSenddelayAlarm(sendAlarm).setAckdelayAlarm(ackAlarm).setAccuAlarm(accuAlarm)
					.setNumAlarm(numAlarm).setEmail(email).setName(name).setDpMobile(mobile)
					.setNormalizedSendDelaly(normalizedSendDelay).setNormalizedAckDelaly(normalizedAckDelay)
					.setNormalizedAccu(normalizedAccu);
			entrys.add(e);
		}

		totalData.setEntrys(entrys);
	}

	private List<Long> calMinuteStats(List<Long> number) {

		List<Long> result = new ArrayList<Long>();
		long delay = 0;
		int size = number.size();

		for (int i = 0; i < size;) {
			delay = number.get(i++);
			if (i < size) {
				delay += number.get(i++);
				result.add((long) Math.floor(delay / 2));
			} else {
				result.add(delay);
			}
		}

		return result;
	}

	private void doGenerateMinuteEntrys(Map<String, TotalData> map) {

		Map<Date, MinuteEntry> minuteEntryMap = new LinkedHashMap<Date, MinuteEntry>();

		for (Map.Entry<String, TotalData> entry : totalDataMap.entrySet()) {
			TotalData td = entry.getValue();
			List<Entry> entrys = td.getEntrys();

			Date time = td.getTime();
			MinuteEntry me = minuteEntryMap.get(time);
			if (me == null) {
				me = new MinuteEntry();
			}
			if (me.getTime() == null) {
				me.setTime(time);
			}

			for (int i = 0; i < entrys.size(); ++i) {
				me.addEntry(entrys.get(i));
				minuteEntryMap.put(time, me);
			}

		}

		for (Map.Entry<Date, MinuteEntry> entry : minuteEntryMap.entrySet()) {
			MinuteEntry me = entry.getValue();

			MinHeap minHeap = me.getDelayEntry();
			int size = minHeap.getSize();
			List<Entry> sorted = new ArrayList<Entry>();
			for (int i = size - 1; i >= 0; i--) {
				Entry eMin = minHeap.deleteMin();
				if (eMin != null) {
					sorted.add(eMin);
				}
			}
			List<Entry> reverseSorted = Lists.reverse(sorted);
			int listSize = reverseSorted.size();
			Entry[] entryArray = reverseSorted.toArray(new Entry[listSize]);
			minHeap.setHeap(entryArray);
			minHeap.setSize(listSize);

			boolean inserted = dashboardContainer.insertMinuteEntry(me);
			if (logger.isInfoEnabled()) {
				logger.info(String.format("Insert MinuteEntry to dashboard %s", inserted ? "successfully" : "failed"));
			}
		}

		totalDataMap.clear();
	}

	private Date getMinuteEntryTime(Long key) {

		long millis = key * 1000 * 5;
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(millis);
		cal.add(Calendar.MINUTE, 1);
		return cal.getTime();
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
