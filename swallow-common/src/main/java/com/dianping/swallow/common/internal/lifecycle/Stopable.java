package com.dianping.swallow.common.internal.lifecycle;

public interface Stopable {

	public static String PHASE_NAME = "stopped";

	void stop() throws Exception;

}
