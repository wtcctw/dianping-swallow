package com.dianping.swallow.web.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.service.AlarmMetaService;

@Service("alarmMetaContainer")
public class AlarmMetaContainer implements InitializingBean {

	private static final Logger logger = LoggerFactory.getLogger(AlarmMetaContainer.class);

	private static final Map<Integer, AlarmMeta> alarmMetas = new ConcurrentHashMap<Integer, AlarmMeta>();

	private static AlarmMetaContainer instance;

	private static ScheduledExecutorService scheduled = Executors.newSingleThreadScheduledExecutor();

	private int interval = 120;// 秒

	private int delay = 5;

	@SuppressWarnings("unused")
	private ScheduledFuture<?> future = null;

	@Autowired
	private AlarmMetaService alarmMetaService;

	public AlarmMeta getAlarmMeta(int metaId) {
		return alarmMetas.get(metaId);
	}

	public static AlarmMetaContainer getInstance() {
		return instance;
	}

	private void scheduleAlarmMetaTask() {
		future = scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					doAlarmMetaTask();
				} catch (Throwable th) {
					logger.error("[scheduleAlarmMetaTask]", th);
				} finally {

				}
			}

		}, delay, interval, TimeUnit.SECONDS);
	}

	private void doAlarmMetaTask() {
		logger.info("[doAlarmMetaTask] scheduled load alarmMeta info.");
		List<AlarmMeta> alarmMetaTemps = alarmMetaService.findByPage(0, AlarmType.values().length);
		if (alarmMetaTemps != null && alarmMetaTemps.size() > 0) {
			for (AlarmMeta alarmMeta : alarmMetaTemps) {
				alarmMetas.put(alarmMeta.getType().getNumber(), alarmMeta);
			}
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
		scheduleAlarmMetaTask();
	}

}
