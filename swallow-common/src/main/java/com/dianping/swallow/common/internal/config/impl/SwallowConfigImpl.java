package com.dianping.swallow.common.internal.config.impl;

import java.util.Set;

import org.apache.log4j.Logger;

import com.dianping.swallow.common.internal.config.SwallowConfig;
import com.dianping.swallow.common.internal.lifecycle.SelfManagement;
import com.dianping.swallow.common.internal.lifecycle.impl.AbstractLifecycle;
import com.dianping.swallow.common.internal.observer.Observer;

/**
 * @author mengwenchao
 *
 * 2015年6月12日 下午6:14:29
 */
public class SwallowConfigImpl extends AbstractLifecycle implements SwallowConfig, SelfManagement{

	protected Logger logger = Logger.getLogger(getClass());

	private SwallowConfig config;
	
	private boolean forceDistrubuted = Boolean.parseBoolean(System.getProperty("forceDistrubuted"));

	public SwallowConfigImpl() throws Exception{
		
		if(forceDistrubuted){
			if(logger.isInfoEnabled()){
				logger.info("[<init>][forceDistributedVersion]");
			}
			config = new SwallowConfigDistributed();
			return;
		}
		
		config = new SwallowConfigCentral();
		if(!config.isSupported()){
			if(logger.isInfoEnabled()){
				logger.info("[<init>]useDistributedVersion");
			}
			config = new SwallowConfigDistributed(); 
		}
		
	}
	
	@Override
	protected void doInitialize() throws Exception {
		super.doInitialize();
		config.initialize();
	}
	
	@Override
	protected void doDispose() throws Exception {
		
		super.doDispose();
		config.dispose();
	}
	
	@Override
	public void addObserver(Observer observer) {
		config.addObserver(observer);
	}

	@Override
	public void removeObserver(Observer observer) {
		config.removeObserver(observer);
	}

	@Override
	public Set<String> getCfgTopics() {
		return config.getCfgTopics();
	}

	@Override
	public TopicConfig getTopicConfig(String topic) {
		return config.getTopicConfig(topic);
	}

	@Override
	public String getHeartBeatMongo() {
		return config.getHeartBeatMongo();
	}

	@Override
	public boolean isSupported() {
		return true;
	}
}
