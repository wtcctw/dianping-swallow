package com.dianping.swallow.web.alarmer.container;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.action.SwallowAction;
import com.dianping.swallow.common.internal.action.SwallowActionWrapper;
import com.dianping.swallow.common.internal.action.impl.CatActionWrapper;
import com.dianping.swallow.common.internal.exception.SwallowException;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.web.alarmer.AlarmerLifecycle;
import com.dianping.swallow.web.alarmer.AlamerTaskManager;
import com.dianping.swallow.web.model.alarm.AlarmMeta;
import com.dianping.swallow.web.model.alarm.AlarmType;
import com.dianping.swallow.web.service.AlarmMetaService;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:33:46
 */
@Component("alarmMetaContainer")
public class AlarmMetaContainerImpl extends AbstractLifecycle implements AlarmMetaContainer, AlarmerLifecycle {

	private static final Logger logger = LoggerFactory.getLogger(AlarmMetaContainerImpl.class);

	private static final Map<Integer, AlarmMeta> alarmMetas = new ConcurrentHashMap<Integer, AlarmMeta>();

	@Autowired
	private AlarmMetaService alarmMetaService;

	@Autowired
	private AlamerTaskManager threadManager;

	@Override
	public AlarmMeta getAlarmMeta(int metaId) {
		return alarmMetas.get(metaId);
	}

	private Future<?> future;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		startLoadResource();
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		if (future != null && !future.isCancelled()) {
			future.cancel(false);
		}

	}

	private void startLoadResource() {
		future = threadManager.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				try {
					SwallowActionWrapper catWrapper = new CatActionWrapper("DataContainer",
							"AlarmMetaContainer-doLoadResource");
					catWrapper.doAction(new SwallowAction() {
						@Override
						public void doAction() throws SwallowException {
							doLoadResource();
						}
					});

				} catch (Throwable th) {
					logger.error("[startLoadResource]", th);
				} finally {

				}
			}

		}, 5, 300, TimeUnit.SECONDS);
	}

	private void doLoadResource() {
		logger.info("[doLoadResource] scheduled load alarmMeta info.");
		List<AlarmMeta> alarmMetaTemps = alarmMetaService.findByPage(0, AlarmType.values().length);
		if (alarmMetaTemps != null && alarmMetaTemps.size() > 0) {
			for (AlarmMeta alarmMeta : alarmMetaTemps) {
				alarmMetas.put(alarmMeta.getType().getNumber(), alarmMeta);
			}
		}

	}

}
