package com.dianping.swallow.web.controller.filter.lion;

import com.dianping.swallow.web.controller.filter.result.LionConfigure;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午3:25:33
 */
public class LionFilterEntity {

	private String topic;
	
	private boolean test;
	
	private LionConfigure lionConfigure;

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

	public LionConfigure getLionConfigure() {
		return lionConfigure;
	}

	public void setLionConfigure(LionConfigure lionConfigure) {
		this.lionConfigure = lionConfigure;
	}

}
