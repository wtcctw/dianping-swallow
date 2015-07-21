package com.dianping.swallow.web.util;

public class GenerateIdUtil {

	private static volatile long lastTime;
	private static volatile int index;

	public static synchronized String getUniqueId() {
		long currentTime = System.currentTimeMillis();
		if (lastTime == currentTime) {
			return Long.toString(lastTime) + "_" + Integer.toString(index++);
		} else {
			index = 0;
			lastTime = currentTime;
			return Long.toString(currentTime) + "_" + Integer.toString(index++);
		}
	}
}
