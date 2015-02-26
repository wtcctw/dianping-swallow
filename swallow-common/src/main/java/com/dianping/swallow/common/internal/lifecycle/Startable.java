package com.dianping.swallow.common.internal.lifecycle;

public interface Startable {

	public static String PHASE_NAME = "started";

	void start() throws Exception;

}
