package com.dianping.swallow.common.server.monitor.data;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * @author mengwenchao
 *
 * 2015年4月25日 上午10:39:40
 */
public interface TotalBuilder{

	void buildTotal();
	
	@JsonIgnore
	Object getTotal();
}
