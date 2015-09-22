package com.dianping.swallow.web.model.dom;

import com.dianping.swallow.web.controller.chain.config.Configure;


/**
 * @author mingdongli
 *
 * 2015年9月9日下午5:30:40
 */
public class LionConfigBean {
	
	private String topic;
	
	private Configure.ConfigureResult configureResult;
	
	private boolean test;

	public boolean isTest() {
		return test;
	}

	public void setTest(boolean test) {
		this.test = test;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public Configure.ConfigureResult getConfigureResult() {
		return configureResult;
	}

	public void setConfigureResult(Configure.ConfigureResult configureResult) {
		this.configureResult = configureResult;
	}

}
