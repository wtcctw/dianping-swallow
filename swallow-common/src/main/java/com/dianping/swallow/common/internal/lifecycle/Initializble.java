package com.dianping.swallow.common.internal.lifecycle;

public interface Initializble {
	
	public static String PHASE_NAME = "initialized";
	
	void initialize() throws Exception;

}
