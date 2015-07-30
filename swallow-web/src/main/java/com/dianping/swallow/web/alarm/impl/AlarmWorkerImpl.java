package com.dianping.swallow.web.alarm.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.dianping.swallow.common.internal.util.CatUtil;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.alarm.AlarmWorker;
import com.dianping.swallow.web.alarm.EventChannel;
import com.dianping.swallow.web.manager.MessageManager;
import com.dianping.swallow.web.model.event.ConsumerIdEvent;
import com.dianping.swallow.web.model.event.Event;
import com.dianping.swallow.web.model.event.ServerEvent;
import com.dianping.swallow.web.model.event.ServerStatisEvent;
import com.dianping.swallow.web.model.event.TopicEvent;
import com.dianping.swallow.web.util.ThreadFactoryUtils;
import com.dianping.swallow.web.util.ThreadUtils;

@Component
public class AlarmWorkerImpl implements AlarmWorker {

	private static final Logger logger = LoggerFactory.getLogger(AlarmWorkerImpl.class);

	@Autowired
	private EventChannel eventChannel;

	@Autowired
	private MessageManager messageManager;

	private static final String FACTORY_NAME = "AlarmWorker";

	private static final int poolSize = CommonUtils.DEFAULT_CPU_COUNT * 2;

	private volatile boolean isStopped = false;

	private ExecutorService executorService = null;

	private Thread alarmTaskThread;

	public AlarmWorkerImpl() {
		this(poolSize);
	}

	public AlarmWorkerImpl(int poolSize) {
		executorService = Executors.newFixedThreadPool(poolSize, ThreadFactoryUtils.getThreadFactory(FACTORY_NAME));
	}

	@PostConstruct
	public void init() {
		isStopped = false;
		alarmTaskThread = ThreadUtils.createThread(new Runnable() {
			@Override
			public void run() {
				start();
			}

		}, "AlarmWorker-Start", true);
		alarmTaskThread.start();
	}

	@Override
	public void start() {
		while (!checkStop()) {
			try {
				Event event = eventChannel.next();
				logger.info("[start] get nextSeq. {}", event.getAlarmType());
				executorService.submit(new AlarmTask(event));
			} catch (RejectedExecutionException e) {
				CatUtil.logException(e);
				try {
					TimeUnit.SECONDS.sleep(200);
				} catch (InterruptedException ex) {
					// ignore
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	@PreDestroy
	public void distroy() {
		stop();
	}

	@Override
	public void stop() {
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
				alarmEvent(event);
			} catch (Exception e) {
				logger.error("[run] alarm event failed . ", e);
			}
		}

	}

	private void alarmEvent(Event event) {
		if (event instanceof ServerStatisEvent) {
			switch (event.getEventType()) {
			case PRODUCER:
				messageManager.producerServerStatisAlarm((ServerStatisEvent) event);
				break;
			case CONSUMER:
				messageManager.consumerServerStatisAlarm((ServerStatisEvent) event);
				break;
			}
		} else if (event instanceof ServerEvent) {
			switch (event.getEventType()) {
			case PRODUCER:
				messageManager.producerServerAlarm((ServerEvent) event);
				break;
			case CONSUMER:
				messageManager.consumerServerAlarm((ServerEvent) event);
				break;
			}
		} else if (event instanceof ConsumerIdEvent) {
			switch (event.getEventType()) {
			case CONSUMER:
				messageManager.consumerIdStatisAlarm((ConsumerIdEvent) event);
				break;
			default:
				break;
			}
		} else if (event instanceof TopicEvent) {
			switch (event.getEventType()) {
			case PRODUCER:
				messageManager.producerTopicStatisAlarm((TopicEvent) event);
				break;
			case CONSUMER:
				messageManager.consumerTopicStatisAlarm((TopicEvent) event);
				break;
			}
		} else {
			logger.error("unsupported event type.");
		}
	}

}
