package com.dianping.swallow.web.alarm;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.util.CommonUtils;
import com.dianping.swallow.web.alarm.impl.AlarmFilterChainFactory;

/**
 * 
 * @author qiyin
 *
 */
public class AlarmScheduledTask extends AbstractLifecycle {

	private static final Logger logger = LoggerFactory.getLogger(AlarmScheduledTask.class);

	protected int alarmInterval = 30;

	private static ScheduledExecutorService scheduled = Executors.newScheduledThreadPool(CommonUtils.DEFAULT_CPU_COUNT * 2);

	private List<WeakReference<ScheduledFuture<?>>> futures = new ArrayList<WeakReference<ScheduledFuture<?>>>();

	private AlarmFilterChainFactory chainFactory;

	private AlarmFilterChain producerServiceFilterChain;
	private AlarmFilterChain producerStatisFilterChain;
	private AlarmFilterChain consumerServiceFilterChain;
	private AlarmFilterChain consumerStatisFilterChain;

	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		createChain();
	}

	@Override
	protected void doStart() throws Exception {
		super.doStart();
//		startAlarm(producerServiceFilterChain);
//		startAlarm(producerStatisFilterChain);
//		startAlarm(consumerServiceFilterChain);
//		startAlarm(consumerStatisFilterChain);
	}

	private void createChain() {
		chainFactory = AlarmFilterChainFactory.chainFactoryInstance;
		producerServiceFilterChain = chainFactory.createProducerServiceFilterChain();
		producerStatisFilterChain = chainFactory.createProducerServiceFilterChain();
		consumerServiceFilterChain = chainFactory.createProducerServiceFilterChain();
		consumerStatisFilterChain = chainFactory.createProducerServiceFilterChain();
	}

	private void startAlarm(final AlarmFilterChain filterChain) {
		ScheduledFuture<?> future = scheduled.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				try {
					filterChain.reset();
					filterChain.doNext();
				} catch (Throwable th) {
					logger.error("[startAlarmer]", th);
				} finally {

				}
			}

		}, getAlarmInterval(), getAlarmInterval(), TimeUnit.SECONDS);
		futures.add(new WeakReference<ScheduledFuture<?>>(future));
	}

	@Override
	protected void doStop() throws Exception {
		super.doStop();
		for (WeakReference<ScheduledFuture<?>> future : futures) {
			if (future.get() == null) {
				continue;
			}
			future.get().cancel(true);
		}
	}

	protected int getAlarmInterval() {
		return alarmInterval;
	}
}
