package com.dianping.swallow.common.internal.lifecycle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mengwenchao
 *
 * 2015年2月12日 下午5:52:21
 */
public class DefaultLifecycleManager implements LifecycleManager {
	
	private final Logger logger     = LoggerFactory.getLogger(getClass());

	private String currentPhaseName = LifecycleManager.CREATED_PHASE_NAME;
	
	@Override
	public void initialize(LifecycleCallback callback) throws Exception {
		
		if(isCreated() || isDisposed()){
			if(logger.isInfoEnabled()){
				logger.info("[initialize]" + callback);
			}
			callback.onTransition();
			currentPhaseName = Initializble.PHASE_NAME;
			return;
		}
		logger.warn("[initialize][can not initialize]" + getCurrentPhaseName() + ", " + callback);
	}

	@Override
	public void start(LifecycleCallback callback) throws Exception {

		if(isInitialized() || isStopped()){
			if(logger.isInfoEnabled()){
				logger.info("[start]" + callback);
			}
			callback.onTransition();
			currentPhaseName = Startable.PHASE_NAME;
			return;
		}
		logger.warn("[start][can not start]" + getCurrentPhaseName() + ", " + callback);
	}

	@Override
	public void stop(LifecycleCallback callback) throws Exception {

		if(isStarted()){
			if(logger.isInfoEnabled()){
				logger.info("[stop]" + callback);
			}
			callback.onTransition();
			currentPhaseName = Stopable.PHASE_NAME;
			return;
		}
		logger.warn("[stop][can not stop]" + getCurrentPhaseName() + ", " + callback);
	}

	@Override
	public void dispose(LifecycleCallback callback) throws Exception {

		if(isInitialized() || isStopped()){
			if(logger.isInfoEnabled()){
				logger.info("[dispose]" + callback);
			}
			callback.onTransition();
			currentPhaseName = Disposable.PHASE_NAME;
			return;
		}
		logger.warn("[dispose][can not dispose]" + getCurrentPhaseName() + ", " + callback);
	}

	@Override
	public boolean isCreated() {
		
		if(phaseIn(LifecycleManager.CREATED_PHASE_NAME)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isInitialized() {
		
		if(phaseIn(Initializble.PHASE_NAME, Startable.PHASE_NAME, Stopable.PHASE_NAME)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isStarted() {
		
		if(phaseIn(Startable.PHASE_NAME)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isStopped() {
		
		if(phaseIn(Stopable.PHASE_NAME)){
			return true;
		}
		return false;
	}

	@Override
	public boolean isDisposed() {
		
		if(phaseIn(Disposable.PHASE_NAME)){
			return true;
		}
		return false;
	}

	private boolean phaseIn(String ... phases) {
		for(String phase : phases){
			if(currentPhaseName.equals(phase)){
				return true;
			}
		}
		return false;
	}

	@Override
	public String getCurrentPhaseName() {
		
		return currentPhaseName;
	}

}
