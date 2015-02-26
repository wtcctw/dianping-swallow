package com.dianping.swallow.common.internal.lifecycle;

public interface Disposable {

	public static String PHASE_NAME = "disposed";

	void dispose() throws Exception;

}
