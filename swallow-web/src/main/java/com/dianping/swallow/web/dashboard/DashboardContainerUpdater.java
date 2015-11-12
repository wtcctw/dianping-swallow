package com.dianping.swallow.web.dashboard;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.server.monitor.data.StatisType;
import com.dianping.swallow.common.server.monitor.data.statis.ConsumerIdStatisData;
import com.dianping.swallow.web.container.ResourceContainer;
import com.dianping.swallow.web.dashboard.model.*;
import com.dianping.swallow.web.dashboard.wrapper.ConsumerDataRetrieverWrapper;
import com.dianping.swallow.web.manager.AppResourceManager;
import com.dianping.swallow.web.model.alarm.ConsumerBaseAlarmSetting;
import com.dianping.swallow.web.model.resource.ApplicationResource;
import com.dianping.swallow.web.model.resource.ConsumerIdResource;
import com.dianping.swallow.web.model.resource.TopicResource;
import com.dianping.swallow.web.monitor.AccumulationRetriever;
import com.dianping.swallow.web.monitor.MonitorDataListener;
import com.dianping.swallow.web.monitor.StatsData;
import com.dianping.swallow.web.util.ThreadFactoryUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author mingdongli
 *
 *         2015年7月7日上午9:36:49
 */
@Component
public class DashboardContainerUpdater extends AbstractLifecycle implements MonitorDataListener, DashboardLifecycle,
		Runnable {

	private static final String FACTORY_NAME = "Dashboard";

	private static final String DEFAULT = "default";

	@Autowired
	private AccumulationRetriever accumulationRetriever;

	@Autowired
	private DashboardContainer dashboardContainer;

	@Autowired
	ConsumerDataRetrieverWrapper consumerDataRetrieverWrapper;

	@Autowired
	private AppResourceManager appResourceManager;

	@Resource(name = "resourceContainer")
	private ResourceContainer alarmResourceContainer;

	private Map<TotalDataKey, TotalData> totalDataMap = new ConcurrentHashMap<TotalDataKey, TotalData>();

	private ScheduledExecutorService scheduled = null;

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private AtomicBoolean delayeven = new AtomicBoolean(false);

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		scheduled = Executors.newSingleThreadScheduledExecutor(ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));
		consumerDataRetrieverWrapper.registerListener(this);
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
	}

	@Override
	public void achieveMonitorData() {
		if (scheduled == null) {
			return;
		}
		if (delayeven.get()) {
			try {
				scheduled.submit(this);
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
				ConsumerIdStatisData result = consumerDataRetrieverWrapper.getValue(
						ConsumerDataRetrieverWrapper.TOTAL, topic, consumerid);

				NavigableMap<Long, Long> senddelay = result.getDelay(StatisType.SEND);
				List<Long> sendList = extractListFromMap(senddelay);

				NavigableMap<Long, Long> ackdelay = result.getDelay(StatisType.ACK);
				List<Long> ackList = extractListFromMap(ackdelay);

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

				TotalDataKey totalcDataKey = new TotalDataKey(topic, consumerid);
				TotalData td = totalDataMap.get(totalcDataKey);
				if (td == null) {
					td = new TotalData();
				}

				td.setListSend(sendList.subList(sendListSize - 2, sendListSize));
				td.setListAck(ackList.subList(ackListSize - 2, ackListSize));
				td.setListAccu(accuList.subList(accuListSize - 2, accuListSize));
				totalDataMap.put(totalcDataKey, td);
			}
		}
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Generate totalData for all topic and consumerid"));
		}

		if (totalDataMap.isEmpty()) {
			return;
		}

		FixSizedPriorityQueueContainer pqContainer = new FixSizedPriorityQueueContainer();

		for (Map.Entry<TotalDataKey, TotalData> entry : totalDataMap.entrySet()) {
			Entry e = generateEntry(entry);
			pqContainer.addEntry(e);
		}

		MinuteEntry me = new MinuteEntry();

		me.setTime(entryTime);
		me.setComprehensiveList(setCmdpInfo(pqContainer.getComprehensivePriorityQueue().sortedList()));
		me.setSendList(setCmdpInfo(pqContainer.getSendPriorityQueue().sortedList()));
		me.setAckList(setCmdpInfo(pqContainer.getAckPriorityQueue().sortedList()));
		me.setAccuList(setCmdpInfo(pqContainer.getAccuPriorityQueue().sortedList()));

		boolean inserted = dashboardContainer.insertMinuteEntry(me);
		if (logger.isInfoEnabled()) {
			logger.info(String.format("Insert MinuteEntry to dashboard %s", inserted ? "successfully" : "failed"));
		}

		totalDataMap.clear();
	}

	private Entry generateEntry(Map.Entry<TotalDataKey, TotalData> entry) {

		TotalData totalData = entry.getValue();

		List<Long> mergeData = calMinuteStats(totalData.getListSend());
		totalData.setListSend(mergeData);
		mergeData = calMinuteStats(totalData.getListAck());
		totalData.setListAck(mergeData);
		mergeData = calMinuteStats(totalData.getListAccu());
		totalData.setListAccu(mergeData);

		return doGenerateEntry(entry);
	}

	/**
	 * 产生每个Entry
	 * 
	 * @param totalData
	 * @throws Exception
	 */
	private Entry doGenerateEntry(Map.Entry<TotalDataKey, TotalData> entry) {

		TotalData totalData = entry.getValue();
		TotalDataKey totalDataKey = entry.getKey();

		List<Long> sendList = totalData.getListSend();
		List<Long> ackList = totalData.getListAck();
		List<Long> accuList = totalData.getListAccu();

		float senddelay = (float) (sendList.get(0) / 1000.0);
		float ackdelay = (float) (ackList.get(0) / 1000.0);
		long accu = accuList.get(0);
		String consumerid = totalDataKey.getConsumerId();
		String topic = totalDataKey.getTopic();

		boolean alarm = true;
		ConsumerBaseAlarmSetting consumerBaseAlarmSetting = null;
		TopicResource topicResource = alarmResourceContainer.findTopicResource(topic);

		if (topicResource != null) {

			if (topicResource.isConsumerAlarm()) {
				ConsumerIdResource consumerIdResource = alarmResourceContainer
						.findConsumerIdResource(topic, consumerid);

				if (consumerIdResource != null) {
					alarm = consumerIdResource.isAlarm();
					consumerBaseAlarmSetting = consumerIdResource.getConsumerAlarmSetting();
					if (consumerBaseAlarmSetting == null) {
						consumerBaseAlarmSetting = loadDefaultConfiguration();
					}
				} else {
					consumerBaseAlarmSetting = loadDefaultConfiguration();
				}

			} else {
				alarm = false;
			}

		}

		Entry e = new Entry();

		e.setConsumerId(consumerid).setTopic(topic).setSenddelay(senddelay).setAckdelay(ackdelay).setAccu(accu);

		e.setAlert(consumerBaseAlarmSetting, alarm);

		return e;

	}

	private ConsumerBaseAlarmSetting loadDefaultConfiguration() {

		ConsumerIdResource consumerIdResource = alarmResourceContainer.findConsumerIdResource(DEFAULT, DEFAULT);

		if (consumerIdResource == null) {
			throw new RuntimeException("No default configuration for ConsumerIdResource.");
		} else {
			return consumerIdResource.getConsumerAlarmSetting();
		}

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

	private List<Long> extractListFromMap(NavigableMap<Long, Long> map) {

		List<Long> list;

		if (map != null) {
			list = new ArrayList<Long>(map.values());
		} else {
			list = new ArrayList<Long>();
		}

		return list;
	}

	private List<Entry> setCmdpInfo(List<Entry> list) {

		List<Entry> result = new ArrayList<Entry>();

		for (Entry e : list) {
			if (StringUtils.isBlank(e.getDpMobile()) || StringUtils.isBlank(e.getEmail())
					|| StringUtils.isBlank(e.getName())) {
				doSetCmdpInfo(e);
			}
			result.add(e);
		}

		return result;
	}

	private void doSetCmdpInfo(Entry entry) {

		String topic = entry.getTopic();
		String consumerId = entry.getConsumerId();

		List<ApplicationResource> appResources = appResourceManager.getAppResourceByConsumerId(topic, consumerId);
		String mobile = "Blank";
		String email = "Blank";
		String name = "Blank";
		if (appResources != null && !appResources.isEmpty()) {
			mobile = appResources.get(0).getDpMobile();
			email = appResources.get(0).getEmail();
			name = appResources.get(0).getApplication();
		}

		entry.setEmail(email).setName(name).setDpMobile(mobile);
	}

	@Override
	public void run() {

		SwallowActionWrapper catWrapper = new CatActionWrapper(getClass().getSimpleName(), "CalculateDashboard");
		catWrapper.doAction(new SwallowAction() {
			@Override
			public void doAction() throws SwallowException {
				updateDelayInsDashboard();
			}
		});
	}

}
