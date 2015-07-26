package com.dianping.swallow.web.util;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadUtils {

	private static final String PREFIX = "Swallow-thread-";

	private static List<WeakReference<Thread>> threadList = Collections
			.synchronizedList(new ArrayList<WeakReference<Thread>>());

	private static ConcurrentHashMap<String, AtomicInteger> taskToSeq = new ConcurrentHashMap<String, AtomicInteger>();

	private ThreadUtils() {

	}

	public static Thread createThread(Runnable r, String taskName, boolean isDaemon) {
		taskToSeq.putIfAbsent(taskName, new AtomicInteger(1));
		Thread t = new Thread(r, PREFIX + taskName + "-" + taskToSeq.get(taskName).getAndIncrement());
		t.setDaemon(isDaemon);
		threadList.add(new WeakReference<Thread>(t));
		return t;
	}
}
