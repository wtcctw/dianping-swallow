package com.dianping.swallow.web.alarmer.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.util.ThreadFactoryUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.CatUtil;
import com.dianping.swallow.web.alarmer.AlarmWorker;
import com.dianping.swallow.web.alarmer.AlarmerLifecycle;
import com.dianping.swallow.web.alarmer.TaskManager;
import com.dianping.swallow.web.alarmer.EventChannel;
import com.dianping.swallow.web.model.event.Event;
import com.dianping.swallow.web.util.ThreadUtils;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 下午6:06:26
 */
@Component
public class AlarmWorkerImpl extends AbstractLifecycle implements AlarmerLifecycle, AlarmWorker {

	private static final Logger logger = LogManager.getLogger(AlarmWorkerImpl.class);

	private final static String EXECUTOR_FACTORY_NAME = "ExecutorAlamer-Worker";

	@Autowired
	private EventChannel eventChannel;

	private volatile boolean isStopped = false;

	private ExecutorService executor = null;

	private Thread alarmTaskThread;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		executor = Executors.newFixedThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 2,
				ThreadFactoryUtils.getThreadFactory(EXECUTOR_FACTORY_NAME));
		isStopped = false;
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
		alarmTaskThread = ThreadUtils.createThread(new Runnable() {
			@Override
			public void run() {
				startAlarmer();
			}

		}, "AlarmWorker-Boss", true);
		alarmTaskThread.start();
	}

	@Override
	public void startAlarmer() {
		while (!checkStop()) {
			Event event = null;
			try {
				event = eventChannel.next();
				logger.info("[start] {}. ", event.toString());
				executor.submit(new AlarmTask(event));
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				CatUtil.logException(e);
				try {
					TimeUnit.SECONDS.sleep(200);
				} catch (InterruptedException ex) {
					// ignore
				}
				logger.error("[start] lost event {}. ", event.toString());
			}
		}
	}

	@Override
	protected void doStop() throws Exception {
		stopAlarmer();
	}

	protected void doDispose() throws Exception {
		super.doDispose();
	}

	@Override
	public void stopAlarmer() {
		isStopped = true;
		alarmTaskThread.interrupt();
	}

	private boolean checkStop() {
		return isStopped || Thread.currentThread().isInterrupted();
	}

	private class AlarmTask implements Runnable {

		private Event event;

		public AlarmTask(Event event) {
			this.event = event;
		}

		@Override
		public void run() {
			try {
				event.alarm();
				logger.info("[run] {}.", event.getClass().getSimpleName());
			} catch (Exception e) {
				logger.error("[run] alarm event failed . ", e);
			}
		}

	}

}
