package com.dianping.swallow.web.container;

import org.springframework.beans.factory.InitializingBean;

/**
 * 
 * @author qiyin
 *
 *         2015年8月3日 上午11:34:10
 */
public class AlarmSettingContainer implements InitializingBean {

	private static AlarmSettingContainer instance;

	public static AlarmSettingContainer getInstance() {
		return instance;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		instance = this;
	}

}
