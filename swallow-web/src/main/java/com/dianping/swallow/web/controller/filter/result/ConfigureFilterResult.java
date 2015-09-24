package com.dianping.swallow.web.controller.filter.result;

import org.springframework.stereotype.Component;


/**
 * @author mingdongli
 *
 * 2015年9月24日下午2:26:18
 */
@Component
public class ConfigureFilterResult extends BaseFilterResult{

	private LionConfigure lionConfigure;

	public LionConfigure getLionConfigure() {
		return lionConfigure;
	}

	public void setLionConfigure(LionConfigure lionConfigure) {
		this.lionConfigure = lionConfigure;
	}
	
}
