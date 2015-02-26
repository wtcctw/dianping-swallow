package com.dianping.swallow.common.internal.lifecycle;

/**
 * 获取当前组件的当前生命周期
 * 
 * @author mengwenchao
 *
 * 2015年2月12日 下午5:47:50
 */
public interface LifecycleManager{
	
	public static String CREATED_PHASE_NAME = "created";

	void initialize(LifecycleCallback callback) throws Exception;
	
	void start(LifecycleCallback callback) throws Exception;

	void stop(LifecycleCallback callback) throws Exception;

	void dispose(LifecycleCallback callback) throws Exception;

	boolean isCreated();

	boolean isInitialized();

	boolean isStarted();
	
	boolean isStopped();
	
	boolean isDisposed();
	
	String getCurrentPhaseName();
	
}
